-- Función para obtener parkings en un radio
CREATE OR REPLACE FUNCTION get_nearby_parkings(user_lat FLOAT, user_long FLOAT, radius_meters FLOAT)
RETURNS SETOF parkings AS $$
BEGIN
    RETURN QUERY
    SELECT *
    FROM parkings
    WHERE ST_DWithin(
        location, 
        ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::geography, 
        radius_meters
    )
    ORDER BY location <-> ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::geography; -- Ordena por el más cercano
END;
$$ LANGUAGE plpgsql;