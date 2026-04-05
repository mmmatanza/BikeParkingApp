drop function get_parking_areas_by_owner;
drop function get_nearby_parking_areas;

-- Función para obtener los parkings de un propietario
CREATE OR REPLACE FUNCTION get_parking_areas_by_owner(p_owner_id UUID)
RETURNS TABLE (
    parking_area_id UUID,
    owner_id UUID,
    name TEXT,
    latitude FLOAT8,
    longitude FLOAT8,
    capacity INT,
    current_occupancy INT,
    is_operative BOOLEAN,
    is_active BOOLEAN,
    opening_time TIME,
    closing_time TIME,
    rules TEXT[]
)
LANGUAGE sql
AS $$
    SELECT
        parking_area_id,
        owner_id,
        name,
        ST_Y(parking_area_location::geometry) AS latitude,
        ST_X(parking_area_location::geometry) AS longitude,
        capacity,
        current_occupancy,
        is_operative,
        is_active,
        opening_time,
        closing_time,
        rules
    FROM parkingareas
    WHERE owner_id = p_owner_id AND is_active = TRUE; -- Los desactivados no aparecen para el administrador
$$;

-- Función para obtener parkings en un radio
CREATE OR REPLACE FUNCTION get_nearby_parking_areas(user_lat FLOAT, user_long FLOAT, radius_meters FLOAT)
RETURNS SETOF parkingareas AS $$
BEGIN
    RETURN QUERY
    SELECT *
    FROM parkingareas
    WHERE ST_DWithin(
        parking_area_location, 
        ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::geography, 
        radius_meters
    )
    -- El operador <-> funciona para ordenar por distancia
    ORDER BY parking_area_location <-> ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::geography;
END;
$$ LANGUAGE plpgsql;

-- Función para añadir un parking
CREATE OR REPLACE FUNCTION add_parking_area(
    p_owner_id UUID,
    p_name TEXT,
    p_latitude FLOAT8,
    p_longitude FLOAT8,
    p_capacity INT,
    p_opening_time TIME,
    p_closing_time TIME,
    p_rules TEXT[]
)
RETURNS VOID
LANGUAGE sql
AS $$
    INSERT INTO parkingareas (
        owner_id,
        name,
        parking_area_location,
        capacity,
        current_occupancy,
        is_operative,
        is_active,
        opening_time,
        closing_time,
        rules
    ) VALUES (
        p_owner_id,
        p_name,
        ST_SetSRID(ST_MakePoint(p_longitude, p_latitude), 4326)::geography,
        p_capacity,
        0,
        true,
        true,
        p_opening_time,
        p_closing_time,
        p_rules
    );
$$;

-- Función para modificar un parking
CREATE OR REPLACE FUNCTION update_parking_area(
    p_parking_area_id UUID,
    p_name TEXT,
    p_capacity INT,
    p_opening_time TIME,
    p_closing_time TIME,
    p_rules TEXT[]
)
RETURNS VOID
LANGUAGE sql
AS $$
    UPDATE parkingareas SET
        name = p_name,
        capacity = p_capacity,
        opening_time = p_opening_time,
        closing_time = p_closing_time,
        rules = p_rules
    WHERE parking_area_id = p_parking_area_id;
$$;

-- Función para obtener un parking por id
CREATE OR REPLACE FUNCTION get_parking_area_by_id(p_parking_area_id UUID)
RETURNS TABLE (
    parking_area_id UUID,
    owner_id UUID,
    name TEXT,
    latitude FLOAT8,
    longitude FLOAT8,
    capacity INTEGER,
    current_occupancy INTEGER,
    is_operative BOOLEAN,
    is_active BOOLEAN,
    opening_time TIME,
    closing_time TIME,
    rules TEXT[]
)
LANGUAGE sql AS $$
    SELECT
        parking_area_id,
        owner_id,
        name,
        ST_Y(parking_area_location::geometry) AS latitude,
        ST_X(parking_area_location::geometry) AS longitude,
        capacity,
        current_occupancy,
        is_operative,
        is_active,
        opening_time,
        closing_time,
        rules
    FROM parkingareas
    WHERE parking_area_id = p_parking_area_id;
$$;