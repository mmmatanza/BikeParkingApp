-- En este archivo se definen procedimientos que realizará Supabase cada cierto intervalo de tiempo

create extension if not exists pg_cron;

-- Trabajo para comprobar reservas vencidas (la bicicleta no ha sido retirada)
-- y actualizar su estado cada 5 minutos
create or replace function check_overdue_reservations()
returns void
language plpgsql
SECURITY DEFINER -- Para poder saltarse las políticas RLS
SET search_path = public -- Por motivos de seguridad
as $$
begin
  update reservations
  set state = 'OVERDUE'
  where state = 'CHECKED_IN'
    and out_time < now();
end;
$$;

select cron.schedule(
  'check-overdue-reservations',   -- nombre del job (debe ser único)
  '*/5 * * * *',                  -- cada 5 minutos
  $$
    select check_overdue_reservations();
  $$
);

-- Trabajo para comprobar reservas expiradas (no se ha producido un check-in)
-- y actualizar su estado cada 5 minutos
create or replace function check_expired_reservations()
returns void
language plpgsql
SECURITY DEFINER
SET search_path = public
as $$
begin
  update reservations
  set state = 'EXPIRED'
  where state = 'RESERVED'
    and in_time < now();
end;
$$;

select cron.schedule(
  'check-expired-reservations',   -- nombre del job (debe ser único)
  '*/5 * * * *',                  -- cada 5 minutos
  $$
    select check_expired_reservations();
  $$
);

-- Comprobar que se registraron los trabajos:
-- select * from cron.job;

-- Ejemplo de ejecución manual:
-- select check_expired_reservations();

-- Historial de ejecuciones:
-- select * from cron.job_run_details
-- order by start_time desc
-- limit 10;

-- Para eliminar un job:
-- select cron.unschedule('check-expired-reservations');