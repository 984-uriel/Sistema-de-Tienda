USE PuntoVenta;

-- Ejecuta estas instrucciones una sola vez si ya habías creado la base de datos.
ALTER TABLE Producto ADD COLUMN codigoBarras VARCHAR(50) NULL UNIQUE AFTER precio;

UPDATE Producto SET codigoBarras = '7501020512345' WHERE idProducto = 1;
UPDATE Producto SET codigoBarras = '7501055300128' WHERE idProducto = 2;
UPDATE Producto SET codigoBarras = '7501000111209' WHERE idProducto = 3;

ALTER TABLE Producto MODIFY codigoBarras VARCHAR(50) NOT NULL;
