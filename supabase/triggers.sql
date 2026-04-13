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
BEGIN
    -- INSERT: Se crea una reserva nueva
    IF (TG_OP = 'INSERT') THEN
        UPDATE parkingareas
        SET current_occupancy = current_occupancy + 1
        WHERE parking_area_id = NEW.parking_area_id;
        RETURN NEW;
    END IF;

    -- UPDATE: Cambio de estado
    IF (TG_OP = 'UPDATE') THEN
        -- Definimos los estados que LIBERAN la plaza
        IF (OLD.state NOT IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED') AND
            NEW.state IN ('CHECKED_OUT', 'CANCELLED', 'EXPIRED')) THEN

            UPDATE parkingareas
            SET current_occupancy = GREATEST(0, current_occupancy - 1)
            WHERE parking_area_id = NEW.parking_area_id;

        -- Si por algún motivo una reserva pasara de CANCELLED a RESERVED
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

-- Aplicamos el trigger a la tabla
DROP TRIGGER IF EXISTS tr_update_occupancy_on_reservation ON reservations;
CREATE TRIGGER tr_update_occupancy_on_reservation
AFTER INSERT OR UPDATE ON reservations
FOR EACH ROW
EXECUTE FUNCTION update_parking_occupancy();
