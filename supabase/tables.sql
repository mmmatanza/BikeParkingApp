-- Para manejar coordenadas
CREATE EXTENSION IF NOT EXISTS postgis;

-- Tabla de cuentas de usuarios
CREATE TABLE accounts (
    account_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    tax_id TEXT NOT NULL UNIQUE,
    role TEXT NOT NULL DEFAULT 'user',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabla de parkings
CREATE TABLE parkingareas (
    parking_area_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    parking_area_location GEOGRAPHY(Point, 4326) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    current_occupancy INTEGER NOT NULL DEFAULT 0 CHECK (current_occupancy >= 0),
    is_operative BOOLEAN NOT NULL DEFAULT true,
    is_active BOOLEAN NOT NULL DEFAULT true,
    opening_time TIME NOT NULL DEFAULT '00:00:00',
    closing_time TIME NOT NULL DEFAULT '23:59:59',
    open_days INTEGER[] NOT NULL DEFAULT '{}',
    rules TEXT[] DEFAULT '{}',
    -- La ocupación no puede superar a la capacidad
    CONSTRAINT check_occupancy_limit CHECK (current_occupancy <= capacity),
    -- No puede cerrar antes de abrir
    CONSTRAINT check_opening_closing CHECK (opening_time < closing_time),
    -- Los días deben ser valores entre 0 (lunes) y 6 (domingo)
    CONSTRAINT check_open_days_valid CHECK (open_days <@ ARRAY[0,1,2,3,4,5,6])
);
-- Índice para la ubicación
CREATE INDEX parking_area_location_idx ON parkingareas USING GIST (parking_area_location);


-- Tabla de reservas
CREATE TABLE reservations (
    reservation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Relaciones
    account_id UUID NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,
    parking_area_id UUID NOT NULL REFERENCES parkingareas(parking_area_id) ON DELETE CASCADE,
    -- Tiempos
    in_time TIMESTAMPTZ NOT NULL,
    out_time TIMESTAMPTZ, -- Puede ser NULL si es flujo en vivo hasta el check-out
    -- Estado con restricción para asegurar que solo entren valores válidos del Enum
    state TEXT NOT NULL DEFAULT 'RESERVED'
        CHECK (state IN ('RESERVED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'EXPIRED', 'OVERDUE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- La hora de entrada debe ser anterior a la de salida (si existe)
    CONSTRAINT check_reservation_times CHECK (out_time IS NULL OR in_time < out_time)
);

-- Índice para acelerar las búsquedas de reservas activas por usuario
CREATE INDEX idx_reservations_account ON reservations(account_id);

-- Índice para acelerar el cálculo de ocupación por parking
CREATE INDEX idx_reservations_parking_active ON reservations(parking_area_id)
WHERE state IN ('RESERVED', 'CHECKED_IN');
