-- En este archivo se definen los triggers y funciones necesarias para la aplicación

-- Trigger para actualizar updated_at
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql
SECURITY DEFINER;

CREATE TRIGGER trigger_update_updated_at
BEFORE UPDATE ON accounts
FOR EACH ROW EXECUTE FUNCTION update_updated_at();


-- Función para asignar el tema por defecto al crear una cuenta
CREATE OR REPLACE FUNCTION assign_default_theme()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.account_themes (account_id, theme_id, is_applied)
    VALUES (NEW.account_id, '00000000-0000-0000-0000-000000000000', true);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public;

-- Trigger para asignar el tema por defecto
DROP TRIGGER IF EXISTS tr_assign_default_theme_on_account_creation ON accounts;
CREATE TRIGGER tr_assign_default_theme_on_account_creation
AFTER INSERT ON accounts
FOR EACH ROW
EXECUTE FUNCTION assign_default_theme();


-- Función para validar cambios en el parking (desactivación y capacidad)
CREATE OR REPLACE FUNCTION validate_parking_area_update()
RETURNS TRIGGER AS $$
DECLARE
    v_active_reservations_count INT;
BEGIN
    -- Validar que la capacidad no caiga por debajo de la ocupación actual
    IF (NEW.capacity < NEW.current_occupancy) THEN
        RAISE EXCEPTION 'CapacityCannotBeLowerThanOccupancyException'
        USING ERRCODE = 'P0011';
    END IF;

    -- Detectamos si se está intentando desactivar el parking
    IF (OLD.is_active = true AND NEW.is_active = false) THEN

        -- Contamos reservas para ese parking
        SELECT COUNT(*)
        INTO v_active_reservations_count
        FROM reservations
        WHERE parking_area_id = NEW.parking_area_id
          AND state IN ('RESERVED', 'CHECKED_IN', 'OVERDUE');

        -- Si existen reservas, lanzamos la excepción
        IF (v_active_reservations_count > 0) THEN
            RAISE EXCEPTION 'CannotDeactivateParkingWithActiveReservationsException'
            USING ERRCODE = 'P0010';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public ;

-- Trigger para validar cambios en un parking
DROP TRIGGER IF EXISTS tr_check_active_reservations_on_deactivate ON parkingareas;
DROP TRIGGER IF EXISTS tr_validate_parking_update ON parkingareas;
CREATE TRIGGER tr_validate_parking_update
BEFORE UPDATE ON parkingareas
FOR EACH ROW
EXECUTE FUNCTION validate_parking_area_update();


-- Trigger para actualizar la ocupación de un parking y validar reservas
CREATE OR REPLACE FUNCTION update_parking_occupancy()
RETURNS TRIGGER AS $$
DECLARE
    v_capacity        INT;
    v_occupancy       INT;
    v_closing_time    TIME;
    v_opening_time    TIME;
    v_open_days       INTEGER[];
    v_out_day_of_week INT;
    v_in_day_of_week  INT;
    v_is_24h          BOOLEAN;
    v_timezone_id     TEXT;
    v_local_in        TIMESTAMP;
    v_local_out       TIMESTAMP;
    v_active_count    INT;
BEGIN
    -- Para nuevas reservas
    IF (TG_OP = 'INSERT') THEN

        -- Comprobamos que el usuario no tiene reservas activas
        SELECT COUNT(*)
        INTO v_active_count
        FROM reservations
        WHERE account_id = NEW.account_id
            AND state IN ('RESERVED', 'CHECKED_IN', 'OVERDUE');

        -- Si el usuario tiene reservas activas, lanzamos una excepción
        IF (v_active_count > 0) THEN
                    RAISE EXCEPTION 'AccountHasActiveReservationException'
                    USING ERRCODE = 'P0009';
                END IF;

        SELECT capacity, current_occupancy, opening_time, closing_time, open_days, timezone_id
        INTO v_capacity, v_occupancy, v_opening_time, v_closing_time, v_open_days, v_timezone_id
        FROM parkingareas
        WHERE parking_area_id = NEW.parking_area_id
        FOR UPDATE;

        -- Aforo
        IF (v_occupancy >= v_capacity) THEN
            RAISE EXCEPTION 'ParkingHasNoFreeSpotsException'
            USING ERRCODE = 'P0001';
        END IF;

        v_is_24h := (v_opening_time = '00:00:00' AND v_closing_time >= '23:59:00');

        IF NOT v_is_24h THEN
            -- Convertir a hora local del parking
            v_local_in := NEW.in_time AT TIME ZONE v_timezone_id;

            -- Validar in_time
            v_in_day_of_week := EXTRACT(ISODOW FROM v_local_in)::INT - 1;
            IF NOT (v_in_day_of_week = ANY(v_open_days)) THEN
                RAISE EXCEPTION 'InTimeDayNotOpenException'
                USING ERRCODE = 'P0004';
            END IF;
            IF (v_local_in::TIME < v_opening_time OR v_local_in::TIME > v_closing_time) THEN
                RAISE EXCEPTION 'InTimeOutOfScheduleException'
                USING ERRCODE = 'P0005';
            END IF;

            -- Validar out_time
            IF NEW.out_time IS NOT NULL THEN
                v_local_out := NEW.out_time AT TIME ZONE v_timezone_id;

                v_out_day_of_week := EXTRACT(ISODOW FROM v_local_out)::INT - 1;
                IF NOT (v_out_day_of_week = ANY(v_open_days)) THEN
                    RAISE EXCEPTION 'OutTimeDayNotOpenException'
                    USING ERRCODE = 'P0006';
                END IF;
                IF (v_local_out::TIME > v_closing_time) THEN
                    RAISE EXCEPTION 'ExtensionExceedsClosingTimeException'
                    USING ERRCODE = 'P0002';
                END IF;
            END IF;
        END IF;

        UPDATE parkingareas
        SET current_occupancy = current_occupancy + 1
        WHERE parking_area_id = NEW.parking_area_id;

        RETURN NEW;
    END IF;

    -- Para actualizaciones
    IF (TG_OP = 'UPDATE') THEN
        -- La hora de entrada no se puede cambiar
        IF (NEW.in_time IS DISTINCT FROM OLD.in_time) THEN
            RAISE EXCEPTION 'InTimeCannotBeModifiedException'
            USING ERRCODE = 'P0007';
        END IF;

        -- Si se cambió la hora de salida, se valida
        IF (NEW.out_time IS DISTINCT FROM OLD.out_time AND NEW.out_time IS NOT NULL) THEN

            SELECT opening_time, closing_time, open_days, timezone_id
            INTO v_opening_time, v_closing_time, v_open_days, v_timezone_id
            FROM parkingareas
            WHERE parking_area_id = NEW.parking_area_id;

            v_is_24h := (v_opening_time = '00:00:00' AND v_closing_time >= '23:59:00');

            IF NOT v_is_24h THEN
                v_local_out := NEW.out_time AT TIME ZONE v_timezone_id;

                v_out_day_of_week := EXTRACT(ISODOW FROM v_local_out)::INT - 1;
                IF NOT (v_out_day_of_week = ANY(v_open_days)) THEN
                    RAISE EXCEPTION 'ExtensionDayNotOpenException'
                    USING ERRCODE = 'P0003';
                END IF;
                IF (v_local_out::TIME > v_closing_time) THEN
                    RAISE EXCEPTION 'ExtensionBeyondClosingTimeException'
                    USING ERRCODE = 'P0008';
                END IF;
            END IF;
        END IF;

        -- Registrar overstay si se hace checkout desde OVERDUE
        IF (OLD.state = 'OVERDUE' AND NEW.state = 'CHECKED_OUT') THEN
            INSERT INTO overstays (reservation_id, extra_minutes)
            VALUES (
                NEW.reservation_id,
                GREATEST(0, EXTRACT(EPOCH FROM (NOW() - NEW.out_time)) / 60)::INTEGER
            );
        END IF;

        -- Actualizar la ocupación
        IF (OLD.state NOT IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED') AND
            NEW.state IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED')) THEN
            UPDATE parkingareas
            SET current_occupancy = GREATEST(0, current_occupancy - 1)
            WHERE parking_area_id = NEW.parking_area_id;

            -- Si la reserva ha sido cancelada, insertamos un aviso para el usuario
            IF (NEW.state = 'CANCELLED') THEN
                INSERT INTO alerts (account_id, parking_area_id, reservation_id, alert_type, is_read)
                VALUES (NEW.account_id, NEW.parking_area_id, NEW.reservation_id, 'RESERVATION_CANCELLED', false);
            END IF;

        -- Lógica por si en el futuro se implementase algún cambio de estado inverso
        ELSIF (OLD.state IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED') AND
               NEW.state NOT IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED')) THEN
            UPDATE parkingareas
            SET current_occupancy = current_occupancy + 1
            WHERE parking_area_id = NEW.parking_area_id;
        END IF;

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public ;

-- Antes de la inserción
DROP TRIGGER IF EXISTS tr_update_occupancy_on_reservation ON reservations;
CREATE TRIGGER tr_update_occupancy_on_reservation
BEFORE INSERT OR UPDATE ON reservations
FOR EACH ROW
EXECUTE FUNCTION update_parking_occupancy();
