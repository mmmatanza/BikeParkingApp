-- En este archivo se definen las tablas necesarias para la aplicación

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

-- Permisos
GRANT SELECT, UPDATE, INSERT ON public.accounts TO authenticated;



-- Función para validar si una zona horaria es válida
CREATE OR REPLACE FUNCTION is_valid_timezone(tz TEXT) RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (SELECT 1 FROM pg_timezone_names WHERE name = tz);
END;
$$ LANGUAGE plpgsql STABLE;

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
    timezone_id TEXT NOT NULL DEFAULT 'Europe/Madrid',
    opening_time TIME NOT NULL DEFAULT '00:00:00',
    closing_time TIME NOT NULL DEFAULT '23:59:59',
    open_days INTEGER[] NOT NULL DEFAULT '{}',
    rules TEXT[] DEFAULT '{}',
    occupancy_threshold INTEGER CHECK (occupancy_threshold >= 0 AND occupancy_threshold <= 100),
    -- La ocupación no puede superar a la capacidad
    CONSTRAINT check_occupancy_limit CHECK (current_occupancy <= capacity),
    -- No puede cerrar antes de abrir
    CONSTRAINT check_opening_closing CHECK (opening_time < closing_time),
    -- Los días deben ser valores entre 0 (lunes) y 6 (domingo)
    CONSTRAINT check_open_days_valid CHECK (open_days <@ ARRAY[0,1,2,3,4,5,6]),
    -- La zona horaria debe ser válida
    CONSTRAINT check_timezone_valid CHECK (is_valid_timezone(timezone_id))
);
-- Índice para la ubicación
CREATE INDEX parking_area_location_idx ON parkingareas USING GIST (parking_area_location);

-- Permisos
GRANT SELECT, UPDATE, INSERT ON public.parkingareas TO authenticated;



-- Tabla de reservas
CREATE TABLE reservations (
    reservation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Relaciones
    account_id UUID NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,
    parking_area_id UUID NOT NULL REFERENCES parkingareas(parking_area_id) ON DELETE CASCADE,
    -- Tiempos
    in_time TIMESTAMPTZ NOT NULL,
    out_time TIMESTAMPTZ,
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
WHERE state IN ('RESERVED', 'CHECKED_IN', 'OVERDUE');

-- Permisos
GRANT SELECT, UPDATE, INSERT ON public.reservations TO authenticated;



-- Tabla para registrar excesos de tiempo
CREATE TABLE overstays (
    overstay_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reservation_id UUID NOT NULL REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    extra_minutes INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Tabla de alertas
CREATE TABLE alerts (
    alert_id        UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id      UUID        NOT NULL REFERENCES accounts(account_id) ON DELETE CASCADE,

    parking_area_id UUID        REFERENCES parkingareas(parking_area_id) ON DELETE SET NULL,
    reservation_id  UUID        REFERENCES reservations(reservation_id) ON DELETE SET NULL,

    alert_type      TEXT        NOT NULL,
    alert_value FLOAT8,
    custom_message TEXT DEFAULT '',
    is_read         BOOLEAN     NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Índice para buscar alertas no leídas de un usuario
CREATE INDEX idx_alerts_account_unread ON alerts(account_id) WHERE is_read = false;

-- Permisos
GRANT SELECT, INSERT, UPDATE ON public.alerts TO authenticated;
