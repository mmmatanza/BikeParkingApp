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
    parking_area_location GEOGRAPHY(Point, 4326) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    current_occupancy INTEGER NOT NULL DEFAULT 0 CHECK (current_occupancy >= 0),
    is_operative BOOLEAN NOT NULL DEFAULT true,
    is_active BOOLEAN NOT NULL DEFAULT true,
    opening_time TIME NOT NULL DEFAULT '00:00:00',
    closing_time TIME NOT NULL DEFAULT '23:59:59',
    rules TEXT[] DEFAULT '{}',
    -- La ocupación no puede superar a la capacidad
    CONSTRAINT check_occupancy_limit CHECK (current_occupancy <= capacity),
    -- No puede cerrar antes de abrir
    CONSTRAINT check_opening_closing CHECK (opening_time < closing_time)
);

-- Índice para la ubicación
CREATE INDEX parking_area_location_idx ON parkingareas USING GIST (parking_area_location);

-- Tabla de reservas 
CREATE TABLE reservations (
    account_id UUID NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,
    parking_area_id UUID NOT NULL REFERENCES parkingareas(parking_area_id) ON DELETE CASCADE,
    reservation_size INTEGER NOT NULL CHECK (reservation_size > 0),
    in_time TIMESTAMPTZ NOT NULL,
    out_time TIMESTAMPTZ NOT NULL,
    reservation_state TEXT NOT NULL, 
    access_code TEXT UNIQUE,
    CONSTRAINT check_times CHECK (out_time > in_time)
);



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