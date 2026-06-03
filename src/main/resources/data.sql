INSERT INTO parametros_catalogo (clave, valor, descripcion, activo) VALUES
('MAX_RESERVAS_POR_DIA', '3', 'Máximo de reservas permitidas por conductor por día', true),
('PRECIO_MINIMO', '1000', 'Precio mínimo permitido por hora en COP', true),
('TIEMPO_MAXIMO_RESERVA_HORAS', '8', 'Tiempo máximo de una reserva en horas', true),
('RADIO_BUSQUEDA_KM', '10', 'Radio de búsqueda de parqueaderos en kilómetros', true)
ON CONFLICT (clave) DO NOTHING;

INSERT INTO mensajes_catalogo (codigo, mensaje, idioma, activo) VALUES
('auth.login.invalid', 'Correo o contraseña incorrectos', 'es', true),
('auth.register.email.exists', 'El correo ya está registrado', 'es', true),
('reserva.conflicto', 'El espacio no está disponible en ese horario', 'es', true),
('parqueadero.not.found', 'Parqueadero no encontrado', 'es', true),
('usuario.inactive', 'Usuario inactivo, contacte al administrador', 'es', true)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO notificaciones_catalogo (tipo, titulo, plantilla, activo) VALUES
('RESERVA_CONFIRMADA', 'Reserva confirmada', 'Tu reserva en {parqueadero} ha sido confirmada para {fecha}', true),
('RESERVA_RECHAZADA', 'Reserva rechazada', 'Tu reserva en {parqueadero} fue rechazada. Motivo: {motivo}', true),
('BIENVENIDA', 'Bienvenido a EasyPark', 'Hola {nombre}, bienvenido a EasyPark. Ya puedes reservar parqueaderos.', true)
ON CONFLICT (tipo) DO NOTHING;
