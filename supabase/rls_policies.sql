-- En este archivo se definen políticas RLS necesarias para aumentar la seguridad el backend
-- Aquí se determina que comportamientos están permitidos para cada tabla

---------------------------------------------------------------------------------------------------
-- Tabla de cuentas de usuarios
---------------------------------------------------------------------------------------------------

-- SELECT de los propios datos
create policy "Select Accounts"
on "public"."accounts"
as PERMISSIVE
for SELECT
to authenticated
using (
(select auth.uid()) = account_id
);

-- UPDATE de los propios datos
create policy "Update Accounts"
on "public"."accounts"
as PERMISSIVE
for UPDATE
to authenticated
using (
(select auth.uid()) = account_id
) with check (
auth.uid() = account_id
);

-- INSERT de los propios datos
create policy "Insert Accounts"
on "public"."accounts"
as PERMISSIVE
for INSERT
to authenticated
with check (
(select auth.uid()) = account_id
);

---------------------------------------------------------------------------------------------------



---------------------------------------------------------------------------------------------------
-- Tabla de parkings
---------------------------------------------------------------------------------------------------

-- SELECT para parkings. Los puede consultar cualquier usuario
create policy "SELECT Parking"
on "public"."parkingareas"
as PERMISSIVE
for SELECT
to authenticated
using (
true
);

-- INSERT para parkings. Permite a los admin crear sus parkings
create policy "INSERT Parking"
on "public"."parkingareas"
as PERMISSIVE
for INSERT
to authenticated
with check (
(select auth.uid()) = owner_id
AND
exists (
    select 1 from accounts
    where account_id = auth.uid()
    and role = 'admin'
  )
);

-- UPDATE para parkings. Permite a los admin modificar sus parkings
create policy "UPDATE Parking"
on "public"."parkingareas"
as PERMISSIVE
for UPDATE
to authenticated
using (
(auth.uid() = owner_id)
) with check (
(auth.uid() = owner_id)
);

---------------------------------------------------------------------------------------------------



---------------------------------------------------------------------------------------------------
-- Tabla de reservas
---------------------------------------------------------------------------------------------------

-- SELECT para reservas.
-- Un usuario puede ver las suyas y un admin, las de sus parkings.
create policy "SELECT reservations"
on "public"."reservations"
as PERMISSIVE
for SELECT
to authenticated
using (
  -- El usuario ve sus propias reservas
  (auth.uid() = account_id)
  OR
  -- El usuario es el dueño del parking al que pertenece la reserva
  exists (
    select 1 from parkingareas
    where parking_area_id = reservations.parking_area_id
    and owner_id = auth.uid()
  )
);

-- INSERT para reservas.
-- Un usuario puede insertar sus propias reservas
create policy "INSERT reservations"
on "public"."reservations"
as PERMISSIVE
for INSERT
to authenticated
with check (
  -- El usuario solo puede insertar si el account_id es el suyo
  (auth.uid() = account_id)
);

-- UPDATE para reservas.
create policy "UPDATE reservations"
on "public"."reservations"
as PERMISSIVE
for UPDATE
to authenticated
-- Es el usuario o el dueño del parking al que pertenece la reserva
using (
(auth.uid() = account_id)
OR
  exists (
    select 1 from parkingareas
    where parking_area_id = reservations.parking_area_id
    and owner_id = auth.uid()
  )
) with check (
-- El usuario puede cambiar el estado de las reservas
(auth.uid() = account_id
    AND state IN ('CANCELLED', 'CHECKED_IN', 'CHECKED_OUT', 'RESERVED')
    and account_id = account_id
  )
  OR
-- El admin puede modificarlas
  exists (
    select 1 from parkingareas
    where parking_area_id = reservations.parking_area_id
    and owner_id = auth.uid()
  )
);

---------------------------------------------------------------------------------------------------



---------------------------------------------------------------------------------------------------
-- Tabla de alertas
---------------------------------------------------------------------------------------------------

-- SELECT para alertas.
create policy "Select Alerts"
on "public"."alerts"
as PERMISSIVE
for SELECT
to authenticated
using (
    (account_id = auth.uid())
    OR
    exists (
    --  El usuario ve las suyas y el dueño del parking las de su recinto
        select 1 from parkingareas
        where parking_area_id = alerts.parking_area_id
        and owner_id = auth.uid()
    )
);

-- UPDATE para alertas.
create policy "Update Alerts"
on "public"."alerts"
as PERMISSIVE
for UPDATE
to authenticated
-- El usuario puede marcar las suyas como leídas
using (
    (account_id = auth.uid())
) with check (
    (account_id = auth.uid())
);

-- INSERT para alertas.
create policy "Insert Alerts"
on "public"."alerts"
as PERMISSIVE
for INSERT
to authenticated
with check (
    -- El dueño del parking puede emitir alertas para los usuarios de su parking
    exists (
        select 1 from parkingareas
        where parking_area_id = alerts.parking_area_id
        and owner_id = auth.uid()
    )
    OR
    -- El propio usuario
    (account_id = auth.uid())
);

---------------------------------------------------------------------------------------------------