INSERT INTO parkingareas (
    owner_id, 
    name, 
    parking_area_location, 
    capacity, 
    current_occupancy, 
    is_operative, 
    is_active
) VALUES (
    '4366d7b9-ac05-4b9d-8257-6bc737b0481e', 
    'Parking Central Estación', 
    ST_GeographyFromText('SRID=4326;POINT(-3.703790 40.416775)'), 
    100, 
    0, 
    true, 
    true
);