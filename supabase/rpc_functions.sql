-- En este archivo se definen funciones RPC utilizadas con Supabase

-- Función para añadir un parking
CREATE OR REPLACE FUNCTION add_parking_area(
    p_owner_id UUID,
    p_name TEXT,
    p_address TEXT,
    p_latitude FLOAT8,
    p_longitude FLOAT8,
    p_capacity INT,
    p_timezone_id TEXT,
    p_opening_time TIME,
    p_closing_time TIME,
    p_open_days INTEGER[],
    p_rules TEXT[],
    p_occupancy_threshold INTEGER
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
        timezone_id,
        opening_time,
        closing_time,
        open_days,
        rules,
        occupancy_threshold
    ) VALUES (
        p_owner_id,
        p_name,
        p_address,
        ST_SetSRID(ST_MakePoint(p_longitude, p_latitude), 4326)::geography,
        p_capacity,
        0,
        true,
        true,
        p_timezone_id,
        p_opening_time,
        p_closing_time,
        p_open_days,
        p_rules,
        p_occupancy_threshold
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
    p_timezone_id TEXT,
    p_opening_time TIME,
    p_closing_time TIME,
    p_open_days INTEGER[],
    p_rules TEXT[],
    p_occupancy_threshold INTEGER
) RETURNS VOID
LANGUAGE sql AS $$
    UPDATE parkingareas SET
        name = p_name,
        address = p_address,
        parking_area_location = ST_SetSRID(ST_MakePoint(p_longitude, p_latitude), 4326)::geography,
        capacity = p_capacity,
        timezone_id = p_timezone_id,
        opening_time = p_opening_time,
        closing_time = p_closing_time,
        open_days = p_open_days,
        rules = p_rules,
        occupancy_threshold = p_occupancy_threshold
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
    timezone_id TEXT,
    opening_time TIME,
    closing_time TIME,
    open_days INTEGER[],
    rules TEXT[],
    occupancy_threshold INTEGER
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
        timezone_id,
        opening_time,
        closing_time,
        open_days,
        rules,
        occupancy_threshold
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
    timezone_id TEXT,
    opening_time TIME,
    closing_time TIME,
    open_days INTEGER[],
    rules TEXT[],
    occupancy_threshold INTEGER
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
        timezone_id,
        opening_time,
        closing_time,
        open_days,
        rules,
        occupancy_threshold
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
    timezone_id TEXT,
    opening_time TIME,
    closing_time TIME,
    open_days INTEGER[],
    rules TEXT[],
    occupancy_threshold INTEGER
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
        p.timezone_id,
        p.opening_time,
        p.closing_time,
        p.open_days,
        p.rules,
        p.occupancy_threshold
    FROM parkingareas p
    WHERE ST_DWithin(
        p.parking_area_location,
        ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::geography,
        radius_meters
    )
    ORDER BY p.parking_area_location <-> ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::geography;
END;
$$ LANGUAGE plpgsql;

-- Función para publicar una alerta manual a los usuarios con reserva activa en un parking
CREATE OR REPLACE FUNCTION publish_parking_alert(
    p_parking_area_id UUID,
    p_message TEXT
)
RETURNS VOID
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO alerts (account_id, parking_area_id, alert_type, custom_message)
    SELECT DISTINCT account_id, p_parking_area_id, 'PARKING_NOTIFICATION', p_message
    FROM reservations
    WHERE parking_area_id = p_parking_area_id
    AND state IN ('RESERVED', 'CHECKED_IN', 'OVERDUE');
END;
$$;
