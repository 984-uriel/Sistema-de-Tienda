USE PuntoVenta;

ALTER TABLE Producto
    ADD COLUMN IF NOT EXISTS codigoBarras VARCHAR(50) NULL UNIQUE AFTER precio;

-- Añade los productos faltantes sin borrar los datos existentes.
INSERT IGNORE INTO Producto
(nombre, descripcion, precio, codigoBarras, idCategoria, idProveedor) VALUES
('Arroz 1 kg','Arroz blanco',32.50,'7501000000041',2,3),
('Frijol Negro 1 kg','Frijol negro seleccionado',38.00,'7501000000058',2,3),
('Aceite Vegetal 900 ml','Aceite para cocinar',42.90,'7501000000065',2,1),
('Azúcar 1 kg','Azúcar estándar',29.50,'7501000000072',2,3),
('Sal 1 kg','Sal refinada',18.00,'7501000000089',2,3),
('Café Soluble 100 g','Café soluble clásico',58.00,'7501000000096',2,1),
('Galletas de Chocolate','Paquete de galletas 170 g',24.50,'7501000000102',2,3),
('Agua Purificada 1 L','Botella de agua',14.00,'7501000000119',3,2),
('Jugo de Naranja 1 L','Bebida sabor naranja',31.00,'7501000000126',3,2),
('Refresco de Limón 600 ml','Refresco sabor limón',19.00,'7501000000133',3,2),
('Yogur Natural 1 L','Yogur natural bebible',39.90,'7501000000140',1,1),
('Queso Manchego 400 g','Queso tipo manchego',78.50,'7501000000157',1,1),
('Mantequilla 90 g','Mantequilla con sal',27.00,'7501000000164',1,1),
('Crema 450 ml','Crema de leche',36.50,'7501000000171',1,1),
('Cereal de Maíz 500 g','Hojuelas de maíz',62.00,'7501000000188',2,3),
('Atún en Agua 140 g','Atún enlatado',26.00,'7501000000195',2,1),
('Papel Higiénico 4 rollos','Paquete de cuatro rollos',47.50,'7501000000201',2,3),
('Pasta Espagueti 200 g','Pasta de trigo durum',13.50,'7501000000218',2,3),
('Sopa de Codito 200 g','Pasta corta de trigo',12.90,'7501000000225',2,3),
('Harina de Trigo 1 kg','Harina para todo uso',25.50,'7501000000232',2,3),
('Mayonesa 390 g','Mayonesa clásica',44.00,'7501000000249',2,1),
('Salsa Cátsup 397 g','Salsa de tomate',29.90,'7501000000256',2,1),
('Chiles Jalapeños 220 g','Chiles en escabeche',21.50,'7501000000263',2,3),
('Elote en Grano 400 g','Granos de elote enlatados',23.00,'7501000000270',2,3),
('Leche Deslactosada 1 L','Leche sin lactosa',31.50,'7501000000287',1,1),
('Leche Condensada 387 g','Leche condensada azucarada',38.90,'7501000000294',1,1),
('Queso Panela 400 g','Queso fresco tipo panela',72.00,'7501000000300',1,1),
('Jamón de Pavo 250 g','Jamón rebanado de pavo',55.50,'7501000000317',1,1),
('Agua Mineral 600 ml','Agua mineral gasificada',18.00,'7501000000324',3,2),
('Refresco de Cola 2 L','Refresco familiar sabor cola',39.00,'7501000000331',3,2),
('Té Helado Limón 600 ml','Bebida de té sabor limón',22.50,'7501000000348',3,2),
('Bebida Energética 473 ml','Bebida energética en lata',42.00,'7501000000355',3,2),
('Néctar de Mango 1 L','Bebida con pulpa de mango',33.50,'7501000000362',3,2),
('Avena 400 g','Hojuelas de avena integral',28.00,'7501000000379',2,3),
('Chocolate en Polvo 400 g','Polvo para preparar bebida',49.90,'7501000000386',2,1),
('Servilletas 125 piezas','Servilletas blancas de papel',26.50,'7501000000393',2,3),
('Detergente en Polvo 1 kg','Detergente para ropa',46.00,'7501000000409',2,3);

INSERT INTO Stock (cantidadActual, stockMinimo, stockMaximo, estado, idProducto)
SELECT 60, 15, 100, 'Disponible', p.idProducto
FROM Producto p
LEFT JOIN Stock s ON s.idProducto = p.idProducto
WHERE p.codigoBarras IN (
 '7501000000041','7501000000058','7501000000065','7501000000072',
 '7501000000089','7501000000096','7501000000102','7501000000119',
 '7501000000126','7501000000133','7501000000140','7501000000157',
 '7501000000164','7501000000171','7501000000188','7501000000195',
 '7501000000201','7501000000218','7501000000225','7501000000232',
 '7501000000249','7501000000256','7501000000263','7501000000270',
 '7501000000287','7501000000294','7501000000300','7501000000317',
 '7501000000324','7501000000331','7501000000348','7501000000355',
 '7501000000362','7501000000379','7501000000386','7501000000393',
 '7501000000409'
) AND s.idStock IS NULL;

SELECT idProducto, codigoBarras, nombre, precio FROM Producto ORDER BY nombre;
