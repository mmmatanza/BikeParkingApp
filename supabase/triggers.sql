-- En este archivo se definen los triggers y funciones necesarias para la aplicación

-- Trigger para actualizar updated_at
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_updated_at
BEFORE UPDATE ON accounts
FOR EACH ROW EXECUTE FUNCTION update_updated_at();



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

        -- Actualizar la ocupación
        IF (OLD.state NOT IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED') AND
            NEW.state IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED')) THEN
            UPDATE parkingareas
            SET current_occupancy = GREATEST(0, current_occupancy - 1)
            WHERE parking_area_id = NEW.parking_area_id;

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
$$ LANGUAGE plpgsql;

-- Antes de la inserción
DROP TRIGGER IF EXISTS tr_update_occupancy_on_reservation ON reservations;
CREATE TRIGGER tr_update_occupancy_on_reservation
BEFORE INSERT OR UPDATE ON reservations
FOR EACH ROW
EXECUTE FUNCTION update_parking_occupancy();
