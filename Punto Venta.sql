/*=========================================================
        PROYECTO: PUNTO DE VENTA
        PARTE 1
        CREACIÓN DE LA BASE DE DATOS
=========================================================*/

DROP DATABASE IF EXISTS PuntoVenta;

CREATE DATABASE PuntoVenta;

USE PuntoVenta;

-- =====================================================
-- TABLA USUARIO
-- =====================================================

CREATE TABLE Usuario(

    idUsuario INT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    correo VARCHAR(100) NOT NULL,

    contrasena VARCHAR(100) NOT NULL,

    telefono VARCHAR(20) NOT NULL

);

-- =====================================================
-- TABLA CLIENTE
-- =====================================================

CREATE TABLE Cliente(

    idCliente INT AUTO_INCREMENT PRIMARY KEY,

    direccion VARCHAR(150) NOT NULL,

    idUsuario INT NOT NULL,

    CONSTRAINT FK_Cliente_Usuario

    FOREIGN KEY(idUsuario)

    REFERENCES Usuario(idUsuario)

        ON UPDATE CASCADE

        ON DELETE RESTRICT

);

-- =====================================================
-- TABLA EMPLEADO
-- =====================================================

CREATE TABLE Empleado(

    idEmpleado INT AUTO_INCREMENT PRIMARY KEY,

    puesto VARCHAR(60) NOT NULL,

    salario DECIMAL(10,2) NOT NULL,

    idUsuario INT NOT NULL,

    CONSTRAINT FK_Empleado_Usuario

    FOREIGN KEY(idUsuario)

    REFERENCES Usuario(idUsuario)

        ON UPDATE CASCADE

        ON DELETE RESTRICT

);

-- =====================================================
-- TABLA ADMINISTRADOR
-- =====================================================

CREATE TABLE Administrador(

    idAdministrador INT AUTO_INCREMENT PRIMARY KEY,

    cargo VARCHAR(60) NOT NULL,

    idUsuario INT NOT NULL,

    CONSTRAINT FK_Administrador_Usuario

    FOREIGN KEY(idUsuario)

    REFERENCES Usuario(idUsuario)

        ON UPDATE CASCADE

        ON DELETE RESTRICT

);

-- =====================================================
-- TABLA CATEGORIA
-- =====================================================

CREATE TABLE Categoria(

    idCategoria INT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    descripcion VARCHAR(200) NOT NULL

);

-- =====================================================
-- TABLA PROVEEDOR
-- =====================================================

CREATE TABLE Proveedor(

    idProveedor INT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    telefono VARCHAR(20) NOT NULL,

    correo VARCHAR(100) NOT NULL

);

/*=========================================================
        PROYECTO: PUNTO DE VENTA
        PARTE 2
        TABLAS RESTANTES
=========================================================*/

-- =====================================================
-- TABLA PRODUCTO
-- =====================================================

CREATE TABLE Producto(

    idProducto INT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    descripcion VARCHAR(200) NOT NULL,

    precio DECIMAL(10,2) NOT NULL,

    codigoBarras VARCHAR(50) NOT NULL UNIQUE,

    idCategoria INT NOT NULL,

    idProveedor INT NOT NULL,

    CONSTRAINT FK_Producto_Categoria
    FOREIGN KEY(idCategoria)
    REFERENCES Categoria(idCategoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT FK_Producto_Proveedor
    FOREIGN KEY(idProveedor)
    REFERENCES Proveedor(idProveedor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

);

-- =====================================================
-- TABLA STOCK
-- =====================================================

CREATE TABLE Stock(

    idStock INT AUTO_INCREMENT PRIMARY KEY,

    cantidadActual INT NOT NULL,

    stockMinimo INT NOT NULL,

    stockMaximo INT NOT NULL,

    estado VARCHAR(30) NOT NULL,

    idProducto INT NOT NULL UNIQUE,

    CONSTRAINT FK_Stock_Producto
    FOREIGN KEY(idProducto)
    REFERENCES Producto(idProducto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

);

-- =====================================================
-- TABLA PEDIDO
-- =====================================================

CREATE TABLE Pedido(

    idPedido INT AUTO_INCREMENT PRIMARY KEY,

    fecha DATE NOT NULL,

    estado VARCHAR(30) NOT NULL,

    total DECIMAL(10,2) NOT NULL,

    idCliente INT NOT NULL,

    CONSTRAINT FK_Pedido_Cliente
    FOREIGN KEY(idCliente)
    REFERENCES Cliente(idCliente)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

);

-- =====================================================
-- TABLA DETALLE_PEDIDO
-- =====================================================

CREATE TABLE Detalle_Pedido(

    idDetalle INT AUTO_INCREMENT PRIMARY KEY,

    cantidad INT NOT NULL,

    precioUnitario DECIMAL(10,2) NOT NULL,

    subtotal DECIMAL(10,2) NOT NULL,

    idPedido INT NOT NULL,

    idProducto INT NOT NULL,

    CONSTRAINT FK_Detalle_Pedido
    FOREIGN KEY(idPedido)
    REFERENCES Pedido(idPedido)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT FK_Detalle_Producto
    FOREIGN KEY(idProducto)
    REFERENCES Producto(idProducto)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

);

-- =====================================================
-- TABLA PAGO
-- =====================================================

CREATE TABLE Pago(

    idPago INT AUTO_INCREMENT PRIMARY KEY,

    fecha DATE NOT NULL,

    metodo VARCHAR(40) NOT NULL,

    total DECIMAL(10,2) NOT NULL,

    idPedido INT NOT NULL UNIQUE,

    CONSTRAINT FK_Pago_Pedido
    FOREIGN KEY(idPedido)
    REFERENCES Pedido(idPedido)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

);

-- =====================================================
-- TABLA ADEUDO
-- =====================================================

CREATE TABLE Adeudo(

    idAdeudo INT AUTO_INCREMENT PRIMARY KEY,

    fecha DATE NOT NULL,

    montoTotal DECIMAL(10,2) NOT NULL,

    montoPagado DECIMAL(10,2) NOT NULL,

    saldoPendiente DECIMAL(10,2) NOT NULL,

    estado VARCHAR(30) NOT NULL,

    idPago INT UNIQUE,

    CONSTRAINT FK_Adeudo_Pago
    FOREIGN KEY(idPago)
    REFERENCES Pago(idPago)
        ON UPDATE CASCADE
        ON DELETE RESTRICT

);

/*=========================================================
        PROYECTO: PUNTO DE VENTA
        PARTE 3
        INSERTAR REGISTROS
=========================================================*/

-- =====================================================
-- USUARIO
-- =====================================================

INSERT INTO Usuario(nombre,correo,contrasena,telefono) VALUES
('Juan Pérez','juan@gmail.com','123456','9991111111'),
('María López','maria@gmail.com','123456','9992222222'),
('Carlos Ruiz','carlos@gmail.com','123456','9993333333'),
('Ana Torres','ana@gmail.com','123456','9994444444'),
('Luis Gómez','luis@gmail.com','123456','9995555555');

-- =====================================================
-- CLIENTE
-- =====================================================

INSERT INTO Cliente(direccion,idUsuario) VALUES
('Av. Tulum 120',1),
('Región 95',2);

-- =====================================================
-- EMPLEADO
-- =====================================================

INSERT INTO Empleado(puesto,salario,idUsuario) VALUES
('Cajero',8500.00,3),
('Vendedor',9000.00,4);

-- =====================================================
-- ADMINISTRADOR
-- =====================================================

INSERT INTO Administrador(cargo,idUsuario) VALUES
('Gerente General',5);

-- =====================================================
-- CATEGORIA
-- =====================================================

INSERT INTO Categoria(nombre,descripcion) VALUES
('Lácteos','Productos refrigerados'),
('Abarrotes','Productos básicos'),
('Bebidas','Refrescos y jugos');

-- =====================================================
-- PROVEEDOR
-- =====================================================

INSERT INTO Proveedor(nombre,telefono,correo) VALUES
('Sigma Alimentos','9981001001','sigma@gmail.com'),
('Coca Cola','9981001002','coca@gmail.com'),
('Bimbo','9981001003','bimbo@gmail.com');

-- =====================================================
-- PRODUCTO
-- =====================================================

INSERT INTO Producto
(nombre,descripcion,precio,codigoBarras,idCategoria,idProveedor)
VALUES

('Leche Lala',
'Leche Entera 1L',
28.50,
'7501020512345',
1,
1),

('Coca Cola 600ml',
'Refresco',
20.00,
'7501055300128',
3,
2),

('Pan Blanco',
'Pan de caja',
45.00,
'7501000111209',
2,
3),

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

-- =====================================================
-- STOCK
-- =====================================================

INSERT INTO Stock
(cantidadActual,stockMinimo,stockMaximo,estado,idProducto)
VALUES

(80,20,120,'Disponible',1),

(150,40,200,'Disponible',2),

(45,15,100,'Disponible',3),
(60,15,100,'Disponible',4),
(55,15,100,'Disponible',5),
(70,20,120,'Disponible',6),
(65,20,110,'Disponible',7),
(50,15,90,'Disponible',8),
(35,10,70,'Disponible',9),
(80,20,140,'Disponible',10),
(120,30,180,'Disponible',11),
(75,20,120,'Disponible',12),
(95,25,150,'Disponible',13),
(48,12,90,'Disponible',14),
(40,10,75,'Disponible',15),
(52,15,90,'Disponible',16),
(45,12,80,'Disponible',17),
(38,10,70,'Disponible',18),
(68,18,110,'Disponible',19),
(58,15,100,'Disponible',20),
(70,15,110,'Disponible',21),
(65,15,100,'Disponible',22),
(55,12,90,'Disponible',23),
(45,10,75,'Disponible',24),
(50,12,85,'Disponible',25),
(60,15,100,'Disponible',26),
(48,12,80,'Disponible',27),
(72,18,120,'Disponible',28),
(42,10,70,'Disponible',29),
(38,10,65,'Disponible',30),
(45,12,75,'Disponible',31),
(90,20,140,'Disponible',32),
(85,20,130,'Disponible',33),
(78,18,120,'Disponible',34),
(40,10,70,'Disponible',35),
(68,15,110,'Disponible',36),
(52,12,90,'Disponible',37),
(46,10,80,'Disponible',38),
(75,20,120,'Disponible',39),
(58,15,100,'Disponible',40);

-- =====================================================
-- PEDIDO
-- =====================================================

INSERT INTO Pedido
(fecha,estado,total,idCliente)
VALUES

('2025-07-10',
'Pagado',
73.50,
1),

('2025-07-11',
'Pendiente',
40.00,
2),

('2025-07-12',
'Pagado',
90.00,
1);

-- =====================================================
-- DETALLE PEDIDO
-- =====================================================

INSERT INTO Detalle_Pedido
(cantidad,precioUnitario,subtotal,idPedido,idProducto)
VALUES

(1,28.50,28.50,1,1),

(2,22.50,45.00,1,3),

(2,20.00,40.00,2,2),

(2,45.00,90.00,3,3);

-- =====================================================
-- PAGO
-- =====================================================

INSERT INTO Pago
(fecha,metodo,total,idPedido)
VALUES

('2025-07-10',
'Efectivo',
73.50,
1),

('2025-07-11',
'Transferencia',
40.00,
2),

('2025-07-12',
'Tarjeta',
90.00,
3);

-- =====================================================
-- ADEUDO
-- =====================================================

INSERT INTO Adeudo
(fecha,montoTotal,montoPagado,saldoPendiente,estado,idPago)
VALUES

('2025-07-11',
40.00,
10.00,
30.00,
'Pendiente',
2);

/*=========================================================
        PROYECTO: PUNTO DE VENTA
        PARTE 4
        CONSULTAS SQL
=========================================================*/

USE PuntoVenta;

-- =====================================================
-- MOSTRAR TODAS LAS TABLAS
-- =====================================================

SELECT * FROM Usuario;
SELECT * FROM Cliente;
SELECT * FROM Empleado;
SELECT * FROM Administrador;
SELECT * FROM Categoria;
SELECT * FROM Proveedor;
SELECT * FROM Producto;
SELECT * FROM Stock;
SELECT * FROM Pedido;
SELECT * FROM Detalle_Pedido;
SELECT * FROM Pago;
SELECT * FROM Adeudo;

-- =====================================================
-- CONSULTA 1
-- CLIENTES Y SUS PEDIDOS
-- =====================================================

SELECT

    c.idCliente,
    u.nombre AS Cliente,
    c.direccion,
    p.idPedido,
    p.fecha,
    p.estado,
    p.total

FROM Cliente c

INNER JOIN Usuario u
ON c.idUsuario = u.idUsuario

INNER JOIN Pedido p
ON c.idCliente = p.idCliente

ORDER BY p.fecha;

-- =====================================================
-- CONSULTA 2
-- DETALLE DE LOS PEDIDOS
-- =====================================================

SELECT

    pe.idPedido,
    u.nombre AS Cliente,
    pr.nombre AS Producto,
    dp.cantidad,
    dp.precioUnitario,
    dp.subtotal

FROM Detalle_Pedido dp

INNER JOIN Pedido pe
ON dp.idPedido = pe.idPedido

INNER JOIN Cliente c
ON pe.idCliente = c.idCliente

INNER JOIN Usuario u
ON c.idUsuario = u.idUsuario

INNER JOIN Producto pr
ON dp.idProducto = pr.idProducto

ORDER BY pe.idPedido;

-- =====================================================
-- CONSULTA 3
-- PRODUCTOS POR CATEGORÍA
-- =====================================================

SELECT

    pr.nombre AS Producto,
    ca.nombre AS Categoria,
    pr.precio

FROM Producto pr

INNER JOIN Categoria ca
ON pr.idCategoria = ca.idCategoria

ORDER BY ca.nombre;

-- =====================================================
-- CONSULTA 4
-- PRODUCTOS Y SU PROVEEDOR
-- =====================================================

SELECT

    pr.nombre AS Producto,
    pv.nombre AS Proveedor,
    pv.telefono

FROM Producto pr

INNER JOIN Proveedor pv
ON pr.idProveedor = pv.idProveedor

ORDER BY pv.nombre;

-- =====================================================
-- CONSULTA 5
-- STOCK DE PRODUCTOS
-- =====================================================

SELECT

    pr.nombre AS Producto,
    s.cantidadActual,
    s.stockMinimo,
    s.stockMaximo,
    s.estado

FROM Stock s

INNER JOIN Producto pr
ON s.idProducto = pr.idProducto

ORDER BY pr.nombre;

-- =====================================================
-- CONSULTA 6
-- PAGOS REALIZADOS
-- =====================================================

SELECT

    pa.idPago,
    u.nombre AS Cliente,
    pa.fecha,
    pa.metodo,
    pa.total

FROM Pago pa

INNER JOIN Pedido pe
ON pa.idPedido = pe.idPedido

INNER JOIN Cliente c
ON pe.idCliente = c.idCliente

INNER JOIN Usuario u
ON c.idUsuario = u.idUsuario

ORDER BY pa.fecha;

-- =====================================================
-- CONSULTA 7
-- ADEUDOS
-- =====================================================

SELECT

    a.idAdeudo,
    u.nombre AS Cliente,
    a.montoTotal,
    a.montoPagado,
    a.saldoPendiente,
    a.estado

FROM Adeudo a

INNER JOIN Pago pa
ON a.idPago = pa.idPago

INNER JOIN Pedido pe
ON pa.idPedido = pe.idPedido

INNER JOIN Cliente c
ON pe.idCliente = c.idCliente

INNER JOIN Usuario u
ON c.idUsuario = u.idUsuario;

-- =====================================================
-- CONSULTA 8
-- EMPLEADOS
-- =====================================================

SELECT

    e.idEmpleado,
    u.nombre,
    e.puesto,
    e.salario

FROM Empleado e

INNER JOIN Usuario u
ON e.idUsuario = u.idUsuario;

-- =====================================================
-- CONSULTA 9
-- ADMINISTRADORES
-- =====================================================

SELECT

    a.idAdministrador,
    u.nombre,
    a.cargo

FROM Administrador a

INNER JOIN Usuario u
ON a.idUsuario = u.idUsuario;

-- =====================================================
-- CONSULTA 10
-- TOTAL DE VENTAS
-- =====================================================

SELECT

    COUNT(*) AS TotalPedidos,
    SUM(total) AS TotalVentas,
    AVG(total) AS PromedioVenta,
    MAX(total) AS VentaMayor,
    MIN(total) AS VentaMenor

FROM Pedido;
