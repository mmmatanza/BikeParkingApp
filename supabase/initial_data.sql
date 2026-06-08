-- Datos iniciales para la base de datos

-- Insertar el tema por defecto (gratis)
INSERT INTO public.themes (theme_id, name, cost, primary_color, secondary_color)
VALUES ('00000000-0000-0000-0000-000000000000', 'Default', 0, '#6200EE', '#03DAC6')
ON CONFLICT (theme_id) DO NOTHING;

-- Tema eco (gratis)
INSERT INTO public.themes (name, cost, primary_color, secondary_color)
VALUES ('Eco Nature', 0, '#2E7D32', '#81C784');

-- Tema night city (50 puntos)
INSERT INTO public.themes (name, cost, primary_color, secondary_color)
VALUES ('Night City', 50, '#37474F', '#FF4081');

-- Tema solar (100 puntos)
INSERT INTO public.themes (name, cost, primary_color, secondary_color)
VALUES ('Solar Energy', 100, '#F57C00', '#FFF176');
