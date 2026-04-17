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



-- Trigger para actualizar la ocupación de un parking
CREATE OR REPLACE FUNCTION update_parking_occupancy()
RETURNS TRIGGER AS $$
DECLARE
    v_capacity INT;
    v_occupancy INT;
BEGIN
    -- Lógica para nuevas reservas
    IF (TG_OP = 'INSERT') THEN
        -- Bloqueamos la fila del parking para evitar que otros procesos
        -- modifiquen la ocupación al mismo tiempo
        SELECT capacity, current_occupancy
        INTO v_capacity, v_occupancy
        FROM parkingareas
        WHERE parking_area_id = NEW.parking_area_id
        FOR UPDATE;

        -- Verificamos si hay sitio real antes de dejar pasar la reserva
        IF (v_occupancy >= v_capacity) THEN
            RAISE EXCEPTION 'ParkingHasNoFreeSpotsException'
            USING ERRCODE = 'P0001';
        END IF;

        -- Si hay sitio, actualizamos
        UPDATE parkingareas
        SET current_occupancy = current_occupancy + 1
        WHERE parking_area_id = NEW.parking_area_id;

        RETURN NEW;
    END IF;

    -- Lógica para UPDATES
    IF (TG_OP = 'UPDATE') THEN
        -- Si pasa a un estado final (Libera plaza)
        IF (OLD.state NOT IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED') AND
            NEW.state IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED')) THEN

            UPDATE parkingareas
            SET current_occupancy = GREATEST(0, current_occupancy - 1)
            WHERE parking_area_id = NEW.parking_area_id;

        -- Si una reserva cancelada vuelve a activarse (Ocupa plaza)
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
