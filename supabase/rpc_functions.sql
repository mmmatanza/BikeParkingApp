-- Función para añadir un parking
CREATE OR REPLACE FUNCTION add_parking_area(
    p_owner_id UUID,
    p_name TEXT,
    p_address TEXT,
    p_latitude FLOAT8,
    p_longitude FLOAT8,
    p_capacity INT,
    p_opening_time TIME,
    p_closing_time TIME,
    p_open_days INTEGER[],
    p_rules TEXT[]
)
RETURNS VOID
LANGUAGE sql
AS $$
    INSERT INTO parkingareas (
        owner_id,
        name,
        address,
        parking_area_location,
        capacity,
        current_occupancy,
        is_operative,
        is_active,
        opening_time,
        closing_time,
        open_days,
        rules
    ) VALUES (
        p_owner_id,
        p_name,
        p_address,
        ST_SetSRID(ST_MakePoint(p_longitude, p_latitude), 4326)::geography,
        p_capacity,
        0,
        true,
        true,
        p_opening_time,
        p_closing_time,
        p_open_days,
        p_rules
    );
$$;

-- Función para modificar un parking
CREATE OR REPLACE FUNCTION update_parking_area(
    p_parking_area_id UUID,
    p_name TEXT,
    p_address TEXT,
    p_latitude FLOAT8,
    p_longitude FLOAT8,
    p_capacity INT,
    p_opening_time TIME,
    p_closing_time TIME,
    p_open_days INTEGER[],
    p_rules TEXT[]
) RETURNS VOID
LANGUAGE sql AS $$
    UPDATE parkingareas SET
        name = p_name,
        address = p_address,
        parking_area_location = ST_SetSRID(ST_MakePoint(p_longitude, p_latitude), 4326)::geography,
        capacity = p_capacity,
        opening_time = p_opening_time,
        closing_time = p_closing_time,
        open_days = p_open_days,
        rules = p_rules
    WHERE parking_area_id = p_parking_area_id;
$$;

-- Función para obtener un parking por id
CREATE OR REPLACE FUNCTION get_parking_area_by_id(p_parking_area_id UUID)
RETURNS TABLE (
    parking_area_id UUID,
    owner_id UUID,
    name TEXT,
    address TEXT,
    latitude FLOAT8,
    longitude FLOAT8,
    capacity INTEGER,
    current_occupancy INTEGER,
    is_operative BOOLEAN,
    is_active BOOLEAN,
    opening_time TIME,
    closing_time TIME,
    open_days INTEGER[],
    rules TEXT[]
)
LANGUAGE sql AS $$
    SELECT
        parking_area_id,
        owner_id,
        name,
        address,
        ST_Y(parking_area_location::geometry) AS latitude,
        ST_X(parking_area_location::geometry) AS longitude,
        capacity,
        current_occupancy,
        is_operative,
        is_active,
        opening_time,
        closing_time,
        open_days,
        rules
    FROM parkingareas
    WHERE parking_area_id = p_parking_area_id;
$$;

-- Función para obtener los parkings de un propietario
CREATE OR REPLACE FUNCTION get_parking_areas_by_owner(p_owner_id UUID)
RETURNS TABLE (
    parking_area_id UUID,
    owner_id UUID,
    name TEXT,
    address TEXT,
    latitude FLOAT8,
    longitude FLOAT8,
    capacity INT,
    current_occupancy INT,
    is_operative BOOLEAN,
    is_active BOOLEAN,
    opening_time TIME,
    closing_time TIME,
    open_days INTEGER[],
    rules TEXT[]
)
LANGUAGE sql
AS $$
    SELECT
        parking_area_id,
        owner_id,
        name,
        address,
        ST_Y(parking_area_location::geometry) AS latitude,
        ST_X(parking_area_location::geometry) AS longitude,
        capacity,
        current_occupancy,
        is_operative,
        is_active,
        opening_time,
        closing_time,
        open_days,
        rules
    FROM parkingareas
    WHERE owner_id = p_owner_id AND is_active = TRUE; -- Los desactivados no aparecen para el administrador
$$;

-- Función para obtener parkings en un radio
CREATE OR REPLACE FUNCTION get_nearby_parking_areas(
    user_lat FLOAT,
    user_long FLOAT,
    radius_meters FLOAT
)
RETURNS TABLE (
    parking_area_id UUID,
    owner_id UUID,
    name TEXT,
    address TEXT,
    latitude FLOAT,
    longitude FLOAT,
    capacity INT,
    current_occupancy INT,
    is_operative BOOLEAN,
    is_active BOOLEAN,
    opening_time TIME,
    closing_time TIME,
    open_days INTEGER[],
    rules TEXT[]
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.parking_area_id,
        p.owner_id,
        p.name,
        p.address,
        ST_Y(p.parking_area_location::geometry)::FLOAT AS latitude,
        ST_X(p.parking_area_location::geometry)::FLOAT AS longitude,
        p.capacity,
        p.current_occupancy,
        p.is_operative,
        p.is_active,
        p.opening_time,
        p.closing_time,
        p.open_days,
        p.rules
    FROM parkingareas p
    WHERE ST_DWithin(
        p.parking_area_location,
        ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::geography,
        radius_meters
    )
    ORDER BY p.parking_area_location <-> ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::geography;
END;
$$ LANGUAGE plpgsql;