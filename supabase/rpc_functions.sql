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
SET search_path = public
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
LANGUAGE sql
SET search_path = public
AS $$
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
LANGUAGE sql
SET search_path = public
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
SET search_path = public
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
)
LANGUAGE plpgsql
SET search_path = public
AS $$
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
$$;

-- Función para publicar una alerta manual a los usuarios con reserva activa en un parking
CREATE OR REPLACE FUNCTION publish_parking_alert(
    p_parking_area_id UUID,
    p_message TEXT
)
RETURNS VOID
LANGUAGE plpgsql
SET search_path = public
AS $$
BEGIN
    INSERT INTO alerts (account_id, parking_area_id, alert_type, custom_message)
    SELECT DISTINCT account_id, p_parking_area_id, 'PARKING_NOTIFICATION', p_message
    FROM reservations
    WHERE parking_area_id = p_parking_area_id
    AND state IN ('RESERVED', 'CHECKED_IN', 'OVERDUE');
END;
$$;


-- Función para obtener el top 3 de usuarios por distancia recorrida en un parking
CREATE OR REPLACE FUNCTION get_parking_top_users(p_parking_area_id UUID)
RETURNS TABLE (
    period TEXT,
    user_name TEXT,
    total_distance FLOAT8
)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    -- Control de seguridad para que solo el propietario del parking pueda llamar a esta función
    IF NOT EXISTS (
        SELECT 1 FROM public.parkingareas
        WHERE parking_area_id = p_parking_area_id
          AND owner_id = auth.uid()
    ) THEN
        RETURN;
    END IF;

    RETURN QUERY
    WITH periods AS (
        -- Definimos los límites de tiempo
        SELECT 'WEEK'::TEXT AS p, NOW() - INTERVAL '7 days' AS start_date
        UNION ALL
        SELECT 'MONTH'::TEXT, NOW() - INTERVAL '30 days'
        UNION ALL
        SELECT 'YEAR'::TEXT, NOW() - INTERVAL '1 year'
    ),
    filtered_reservations AS (
        -- Escaneamos las reservas de este parking una sola vez para el año completo
        SELECT
            p.p AS period_name,
            r.account_id,
            r.distance
        FROM periods p
        JOIN reservations r ON r.created_at >= p.start_date
        WHERE r.parking_area_id = p_parking_area_id
          AND r.state = 'CHECKED_OUT'
          AND r.distance IS NOT NULL
    ),
    user_sums AS (
        -- Agrupamos por periodo y usuario para tener sus kilómetros totales
        SELECT
            fr.period_name,
            fr.account_id,
            SUM(fr.distance)::FLOAT8 AS total_dist
        FROM filtered_reservations fr
        GROUP BY fr.period_name, fr.account_id
    ),
    ranked_users AS (
        -- Calculamos la posición de cada usuario dentro de su periodo
        SELECT
            us.period_name,
            us.account_id,
            us.total_dist,
            ROW_NUMBER() OVER (
                PARTITION BY us.period_name
                ORDER BY us.total_dist DESC
            ) AS rn
        FROM user_sums us
    )
    -- Hacemos el JOIN con las cuentas exclusivamente para los que entraron en el Top 3
    SELECT
        ru.period_name,
        a.name,
        ru.total_dist
    FROM ranked_users ru
    JOIN accounts a ON ru.account_id = a.account_id
    WHERE ru.rn <= 3
    ORDER BY
        CASE ru.period_name WHEN 'WEEK' THEN 1 WHEN 'MONTH' THEN 2 WHEN 'YEAR' THEN 3 END,
        ru.total_dist DESC;
END;
$$;

-- Función para obtener la distancia total sumada de las reservas CHECKED_OUT de un parking por periodos
CREATE OR REPLACE FUNCTION get_parking_eco_metrics(p_parking_area_id UUID)
RETURNS TABLE (
    period TEXT,
    total_distance FLOAT8
)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM public.parkingareas
        WHERE parking_area_id = p_parking_area_id
        AND owner_id = auth.uid()
    )  THEN
        RAISE EXCEPTION 'Access forbidden.';
    END IF;

    RETURN QUERY
    WITH raw_data AS (
        -- Escaneamos la tabla el periodo máximo (1 año)
        SELECT
            created_at,
            distance::FLOAT8 AS dist
        FROM reservations
        WHERE parking_area_id = p_parking_area_id
          AND created_at >= (NOW() - INTERVAL '1 year')
          AND state = 'CHECKED_OUT'
          AND distance IS NOT NULL
    ),
    periods AS (
        SELECT 'WEEK'::TEXT AS p UNION ALL
        SELECT 'MONTH'::TEXT UNION ALL
        SELECT 'YEAR'::TEXT
    )
    SELECT
        p.p,
        CASE
            WHEN p.p = 'WEEK'  THEN COALESCE(SUM(rd.dist) FILTER (WHERE rd.created_at >= NOW() - INTERVAL '7 days'), 0)::FLOAT8
            WHEN p.p = 'MONTH' THEN COALESCE(SUM(rd.dist) FILTER (WHERE rd.created_at >= NOW() - INTERVAL '30 days'), 0)::FLOAT8
            WHEN p.p = 'YEAR'  THEN COALESCE(SUM(rd.dist), 0)::FLOAT8
        END AS total_distance
    FROM periods p
    LEFT JOIN raw_data rd ON true
    GROUP BY p.p
    ORDER BY CASE p.p WHEN 'WEEK' THEN 1 WHEN 'MONTH' THEN 2 WHEN 'YEAR' THEN 3 END;
END;
$$;

-- Función para obtener las métricas ecológicas y posición en el ranking de un usuario
CREATE OR REPLACE FUNCTION get_user_eco_metrics()
RETURNS TABLE (
    period TEXT,
    user_distance FLOAT8,
    ranking_position BIGINT,
    total_users BIGINT
)
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
    v_current_user_id UUID;
BEGIN
    -- Capturamos el ID del usuario autenticado inmediatamente
    v_current_user_id := auth.uid();

    -- Control de seguridad: Si no hay un usuario autenticado, disparamos un error explícito
    IF v_current_user_id IS NULL THEN
        RAISE EXCEPTION 'NOT LOGGED.';
    END IF;

    RETURN QUERY
    WITH periods AS (
        -- Definimos los intervalos de tiempo en una tabla temporal
        SELECT 'WEEK'::TEXT AS p, NOW() - INTERVAL '7 days' AS start_date
        UNION ALL
        SELECT 'MONTH'::TEXT, NOW() - INTERVAL '30 days'
        UNION ALL
        SELECT 'YEAR'::TEXT, NOW() - INTERVAL '1 year'
    ),
    filtered_reservations AS (
        -- Filtramos las reservas válidas cruzando con los períodos (Requiere saltarse RLS para armar el ranking global)
        SELECT
            p.p AS period_name,
            r.account_id,
            r.distance
        FROM periods p
        JOIN reservations r ON r.created_at >= p.start_date
        WHERE r.state = 'CHECKED_OUT'
          AND r.distance IS NOT NULL
    ),
    aggregated_stats AS (
        -- Agrupamos por periodo y usuario, calculando la distancia total de cada uno
        SELECT
            fr.period_name,
            fr.account_id,
            SUM(fr.distance)::FLOAT8 AS total_dist
        FROM filtered_reservations fr
        GROUP BY fr.period_name, fr.account_id
    ),
    ranked_stats AS (
        -- Calculamos el ranking y el total de usuarios por periodo
        SELECT
            ags.period_name,
            ags.account_id,
            ags.total_dist,
            RANK() OVER(PARTITION BY ags.period_name ORDER BY ags.total_dist DESC)::BIGINT AS pos,
            COUNT(*) OVER(PARTITION BY ags.period_name)::BIGINT AS total_u
        FROM aggregated_stats ags
    )
    -- Seleccionamos el resultado final asegurando que si el usuario no tiene registros devuelva 0
    SELECT
        p.p,
        COALESCE(rs.total_dist, 0)::FLOAT8,
        COALESCE(rs.pos, COALESCE(p_total.total_u, 0) + 1)::BIGINT,
        COALESCE(p_total.total_u, 1)::BIGINT
    FROM periods p
    -- Subconsulta para saber cuántos usuarios únicos hay por periodo
    LEFT JOIN (
        SELECT period_name, COUNT(DISTINCT account_id) as total_u
        FROM filtered_reservations GROUP BY period_name
    ) p_total ON p.p = p_total.period_name
    -- Cruzamos únicamente con los datos específicos del usuario que invocó la petición
    LEFT JOIN ranked_stats rs ON p.p = rs.period_name AND rs.account_id = v_current_user_id
    ORDER BY
        CASE p.p WHEN 'WEEK' THEN 1 WHEN 'MONTH' THEN 2 WHEN 'YEAR' THEN 3 END;
END;
$$;