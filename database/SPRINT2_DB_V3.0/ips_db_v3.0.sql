PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE "TDEVOLUCIONES" (
	"ID"	INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	"ID_VENTA"	INTEGER NOT NULL CHECK(ID_VENTA>0),
	"ID_PRODUCTO"	INTEGER NOT NULL CHECK(ID_PRODUCTO>0),
	"MOTIVO"	TEXT NOT NULL DEFAULT 'OTROS',
	"CANTIDAD_DEVUELTA"	INTEGER NOT NULL DEFAULT 1 CHECK(CANTIDAD_DEVUELTA>0),
	PRIMARY KEY("ID"),
	FOREIGN KEY("ID_VENTA") REFERENCES "TVENTA_PRODUCTOS"("ID_VENTA"),
	FOREIGN KEY("ID_PRODUCTO") REFERENCES "TVENTA_PRODUCTOS"("ID_PRODUCTO")
);
INSERT INTO "TDEVOLUCIONES" VALUES(1,1,5,'OTROS',1);
INSERT INTO "TDEVOLUCIONES" VALUES(2,6,1,'OTROS',2);
INSERT INTO "TDEVOLUCIONES" VALUES(3,6,4,'OTROS',2);
INSERT INTO "TDEVOLUCIONES" VALUES(4,6,6,'OTROS',1);
INSERT INTO "TDEVOLUCIONES" VALUES(5,8,1,'OTROS',3);
INSERT INTO "TDEVOLUCIONES" VALUES(6,2,6,'OTROS',1);
INSERT INTO "TDEVOLUCIONES" VALUES(7,5,6,'Pedido por error',2);
INSERT INTO "TDEVOLUCIONES" VALUES(8,3,16,'Pedido por error',1);
CREATE TABLE "TCLIENTES" (
	"ID"	INTEGER NOT NULL CHECK(ID>0) UNIQUE, 
	"DNI"	TEXT NOT NULL,
	"NOMBRE"	TEXT NOT NULL,
	"DIRECCION"	TEXT NOT NULL,
	"CP"	TEXT NOT NULL,
	"TELEFONO"	INTEGER NOT NULL CHECK(TELEFONO>0),
	PRIMARY KEY("ID")
);
INSERT INTO "TCLIENTES" VALUES(1,'12312312S','Jose','milicias nacionales','33031',432457235);
INSERT INTO "TCLIENTES" VALUES(2,'23423423S','Ana','melquiades alvarez','33021',321132546);
INSERT INTO "TCLIENTES" VALUES(3,'34534534S','Marta','uria','33011',987456234);
INSERT INTO "TCLIENTES" VALUES(4,'45645645S','Raul','division azul','33041',123456987);
INSERT INTO "TCLIENTES" VALUES(5,'56756756S','Marcos','la paz','33011',321456987);
INSERT INTO "TCLIENTES" VALUES(6,'67867867S','Julian','campoamor','33010',123456789);
INSERT INTO "TCLIENTES" VALUES(7,'78978978S','Pedro','campomanes','33021',432455879);
INSERT INTO "TCLIENTES" VALUES(8,'89089089S','Sara','fray ceferino','33012',789546123);
INSERT INTO "TCLIENTES" VALUES(9,'90190190S','Alba','general elorza','33013',987654321);
INSERT INTO "TCLIENTES" VALUES(10,'777777883W','Juan','milicias nacionales','77883',778393729);
INSERT INTO "TCLIENTES" VALUES(11,'83983912Y','Juan','calle campomanes','83838',83293293);
INSERT INTO "TCLIENTES" VALUES(12,'33393328Q','Juanillo','alcorcon','84433',94384348);
INSERT INTO "TCLIENTES" VALUES(13,'32889050Q','Angel','c/La Teyerona','28080',888888998);
INSERT INTO "TCLIENTES" VALUES(14,'99999999W','Juanillo','murcia','88393',993839383);
INSERT INTO "TCLIENTES" VALUES(15,'887722772Y','Kek','sevilla','77737',77383938);
CREATE TABLE "TUSUARIOS" (
	"ID" INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	"USERNAME" TEXT NOT NULL UNIQUE,
	"HASHSHA512" TEXT NOT NULL,
	"HASHMD5" TEXT NOT NULL,
	PRIMARY KEY("ID")
);
INSERT INTO "TUSUARIOS" VALUES (1,'transportista1','2cb86799b5f69d866a5e23ef448340a066abae3be8659655b8d85b1dfaf16a387d229e2a265e253da88823d5b7576b4f9ac22a353cfe9a624cc76d5d4d96b836','7e28a0ad1540330b40a18c1f556aaaf3');
INSERT INTO "TUSUARIOS" VALUES (2,'transportista2','7ea8416b128dabeadfc82a2f1bf845009f33ed577058481ffc7b6ec35e4a94301b11874907c4b003cf67a8c78e0d26b6d7a13d16eaf97d64b7592abe6dd3e088','b3dac820eacd26bd3e6adbd3351085a5');
INSERT INTO "TUSUARIOS" VALUES (3,'vendedor1','77266624489b68f0e5ab10ea2dbe719fe75f306c9423b3043d76f5062920e7715ea5c6f3f53f6a18b0273caf2b8d3c7b706430882b63eeca854a11bd0b3e5f31','2be8079fd3f01980b06cc39c1f0cc2ba');
INSERT INTO "TUSUARIOS" VALUES (4,'vendedor2','bc5c36125858ca5f945aaef375d2b8dc889593b9296b23edae820e61dc7aedc2c528b300bf782860d32699aedd1a80f6f1b48244b0ac589a5f1c8771cb2d4f3b','6758edb0ee92329fefe3f5ba5bb9b840');
CREATE TABLE "TTRANSPORTISTAS" (
	"ID" INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	PRIMARY KEY("ID"),
	FOREIGN KEY("ID") REFERENCES "TUSUARIOS"("ID")
);
INSERT INTO "TTRANSPORTISTAS" VALUES(1);
INSERT INTO "TTRANSPORTISTAS" VALUES(2);
CREATE TABLE "TVENDEDORES" (
	"ID" INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	PRIMARY KEY("ID"),
	FOREIGN KEY("ID") REFERENCES "TUSUARIOS"("ID")
);
INSERT INTO "TVENDEDORES" VALUES(3);
INSERT INTO "TVENDEDORES" VALUES(4);
CREATE TABLE "TPRODUCTOS" (
	"ID"	INTEGER NOT NULL CHECK(ID>0),
	"NOMBRE"	TEXT NOT NULL,
	"PRECIO_PUBLICO"	REAL NOT NULL DEFAULT 0.00,
	"PRECIO_PROVEEDOR"	REAL NOT NULL,
	"PRECIO_ORIGINAL" REAL NOT NULL,
	"CATEGORIA"	TEXT NOT NULL,
	"CANTIDAD_ALMACEN"	INTEGER NOT NULL DEFAULT 0 CHECK(CANTIDAD_ALMACEN>=0),
	PRIMARY KEY("ID")
);
INSERT INTO "TPRODUCTOS" VALUES(1,'Cama Familiar Mandal',499.95,419.99,100.0,'Camas',3);
INSERT INTO "TPRODUCTOS" VALUES(2,'Mesilla de noche Kullen',19.95,7.95,100.0,'Comodas',4);
INSERT INTO "TPRODUCTOS" VALUES(3,'Armario Brimnes',149.99,99.95,100.0,'Armarios',12);
INSERT INTO "TPRODUCTOS" VALUES(4,'Sofa Kivik',549.99,499.95,100.0,'Sofas',19);
INSERT INTO "TPRODUCTOS" VALUES(5,'Cesto Stuk',5.0,0.99,100.0,'Organizadores',5);
INSERT INTO "TPRODUCTOS" VALUES(6,'Cama Familiar Dunvik',1134.0,1000.0,100.0,'Camas',21);
INSERT INTO "TPRODUCTOS" VALUES(7,'Armario Platsa',474.99,399.95,100.0,'Armarios',13);
INSERT INTO "TPRODUCTOS" VALUES(8,'Escritorio Brusali',49.99,20.0,100.0,'Escritorios',5);
INSERT INTO "TPRODUCTOS" VALUES(9,'Estanteria Kallax',79.0,49.95,100.0,'Estanterias',9);
INSERT INTO "TPRODUCTOS" VALUES(10,'Sillon Nolmyra',34.99,10.0,100.0,'Sillones',0);
INSERT INTO "TPRODUCTOS" VALUES(11,'Sillon Agen',39.99,19.95,100.0,'Sillones',30);
INSERT INTO "TPRODUCTOS" VALUES(12,'Armario Visthus',279.0,199.95,100.0,'Armarios',0);
INSERT INTO "TPRODUCTOS" VALUES(13,'Armario Pax',373.0,278.95,100.0,'Armarios',9);
INSERT INTO "TPRODUCTOS" VALUES(14,'Mesa de ordenador Micke',69.95,40.0,100.0,'Mesas',138);
INSERT INTO "TPRODUCTOS" VALUES(15,'Escritorio Malm',129.99,99.95,100.0,'Mesas',27);
INSERT INTO "TPRODUCTOS" VALUES(16,'Libreria Kallax',79.95,40.95,100.0,'Estanterias',10);
INSERT INTO "TPRODUCTOS" VALUES(17,'Silla Sundmo',39.99,19.95,100.0,'Sillas',0);
INSERT INTO "TPRODUCTOS" VALUES(18,'Armario Stuva',8000.0,89.95,100.0,'Armarios',0);
INSERT INTO "TPRODUCTOS" VALUES(19,'Silla Stefan',15.0,8.95,100.0,'Sillas',1);
INSERT INTO "TPRODUCTOS" VALUES(20,'Sillon Muren',269.0,195.95,100.0,'Sillones',2);
INSERT INTO "TPRODUCTOS" VALUES(21,'Lampara generica',100.5,60.95,100.0,'Decoraciones',6);
INSERT INTO "TPRODUCTOS" VALUES(22,'Maceta ficus',100.5,69.95,100.0,'Decoraciones',198);
INSERT INTO "TPRODUCTOS" VALUES(23,'Jarron flores veraniegas',100.5,29.99,100.0,'Decoraciones',3);
INSERT INTO "TPRODUCTOS" VALUES(24,'Paraguero joifron',100.5,20.95,100.0,'Decoraciones',15);
INSERT INTO "TPRODUCTOS" VALUES(25,'Cuadro arte moderno',100.5,69.95,100.0,'Decoraciones',0);
INSERT INTO "TPRODUCTOS" VALUES(26,'Imanes nevera',100.5,40.94,100.0,'Decoraciones',29);
INSERT INTO "TPRODUCTOS" VALUES(27,'Vela decorada con estampados',100.5,10.99,100.0,'Decoraciones',74);
INSERT INTO "TPRODUCTOS" VALUES(28,'Espejo curvo',100.5,89.95,100.0,'Decoraciones',86);
INSERT INTO "TPRODUCTOS" VALUES(29,'Calendario chino',100.5,60.95,100.0,'Decoraciones',7);
INSERT INTO "TPRODUCTOS" VALUES(30,'Toallero profesional',100.5,70.95,100.0,'Decoraciones',8);
CREATE TABLE "TMETODOS_DE_PAGO" (
	"ID" INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	PRIMARY KEY("ID")
);
INSERT INTO "TMETODOS_DE_PAGO" VALUES(2);
INSERT INTO "TMETODOS_DE_PAGO" VALUES(3);
INSERT INTO "TMETODOS_DE_PAGO" VALUES(4);
CREATE TABLE "TTRANSFERENCIAS_BANCARIAS" (
	"ID" INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	"CUENTA_CARGO" TEXT NOT NULL,
	"CUENTA_DESTINO" TEXT NOT NULL,
	"BENEFICIARIO" TEXT NOT NULL,
	"CONCEPTO" TEXT NOT NULL,
	"ANOTACIONES" TEXT NOT NULL,
	FOREIGN KEY("ID") REFERENCES "TMETODOS_DE_PAGO"("ID"),
	PRIMARY KEY("ID")
);
INSERT INTO "TTRANSFERENCIAS_BANCARIAS" VALUES(4,'fdf','ffdf','df','dfds','');
CREATE TABLE "TTARJETAS_DE_CREDITO" (
	"ID" INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	"NOMBRE_TITULAR" TEXT NOT NULL,
	"CVV" INTEGER NOT NULL CHECK(CVV>0),
	"FECHA_EXPIRACION" TEXT NOT NULL,
	"NUMERO" TEXT NOT NULL,
	FOREIGN KEY("ID") REFERENCES "TMETODOS_DE_PAGO"("ID"),
	PRIMARY KEY("ID")
);
CREATE TABLE "TPEDIDOS_PROVEEDOR" (
	"ID"	INTEGER NOT NULL CHECK(ID>0),
	"ESTADO"	TEXT NOT NULL DEFAULT 'SOLICITADO' CHECK(ESTADO IN ('SOLICITADO','RECIBIDO')),
	"ID_VENTA"	INTEGER CHECK(ID_VENTA>0) UNIQUE,
	"FECHA" TEXT NOT NULL,
	PRIMARY KEY("ID"),
	FOREIGN KEY("ID_VENTA") REFERENCES "TVENTAS"("ID")
);
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(1,'RECIBIDO',NULL,'01/11/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(2,'SOLICITADO',NULL,'03/10/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(3,'SOLICITADO',NULL,'03/09/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(4,'SOLICITADO',NULL,'04/03/2018');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(5,'SOLICITADO',NULL,'03/01/2018');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(6,'RECIBIDO',NULL,'14/08/2018');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(7,'SOLICITADO',NULL,'18/05/2017');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(8,'SOLICITADO',NULL,'16/04/2017');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(9,'SOLICITADO',NULL,'02/03/2017');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(10,'SOLICITADO',NULL,'24/02/2017');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(11,'SOLICITADO',NULL,'26/01/2017');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(12,'RECIBIDO',10,'02/12/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(13,'SOLICITADO',NULL,'02/12/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(14,'SOLICITADO',12,'02/12/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(15,'SOLICITADO',15,'02/12/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(16,'SOLICITADO',NULL,'02/12/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(17,'SOLICITADO',NULL,'02/12/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(18,'SOLICITADO',NULL,'02/12/2019');
INSERT INTO "TPEDIDOS_PROVEEDOR" VALUES(19,'SOLICITADO',18,'02/12/2019');
CREATE TABLE "TPRESUPUESTADOS" (
	"ID_PRESUPUESTO"	INTEGER NOT NULL CHECK(ID_PRESUPUESTO>0),
	"ID_PRODUCTO"	INTEGER NOT NULL CHECK(ID_PRODUCTO>0),
	"CANTIDAD"	INTEGER NOT NULL DEFAULT 1 CHECK(CANTIDAD>0),
	"CUANTIA"	REAL NOT NULL CHECK(CUANTIA>0),
	FOREIGN KEY("ID_PRESUPUESTO") REFERENCES "TPRESUPUESTOS"("ID"),
	PRIMARY KEY("ID_PRESUPUESTO","ID_PRODUCTO"),
	FOREIGN KEY("ID_PRODUCTO") REFERENCES "TPRODUCTOS"("ID")
);
INSERT INTO "TPRESUPUESTADOS" VALUES(1,5,2,5.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(2,3,1,149.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(2,6,1,1134.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(2,8,1,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(3,16,2,79.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(3,14,4,69.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(4,3,2,149.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(4,4,1,549.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(4,8,1,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(4,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(4,12,5,279.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(4,10,1,34.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(5,6,2,1134.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(5,9,2,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(6,3,1,149.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(6,4,3,549.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(6,2,2,19.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(6,5,2,5.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(6,1,2,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(6,6,1,1134.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(7,8,3,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(8,1,3,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(8,4,1,549.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(8,8,1,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(8,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(12,19,1,15.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(12,23,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,13,1,373.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,19,1,15.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,24,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,27,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,30,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,26,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,28,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,23,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,29,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,21,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,20,1,269.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(13,22,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(14,25,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(15,15,1,129.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(16,24,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(16,26,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(17,21,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(18,23,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(19,24,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(20,26,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(21,16,1,79.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(21,17,63,39.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(22,18,1,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(23,1,1,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(24,1,1,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(25,10,1,34.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(26,13,1,373.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(27,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(28,18,1,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(29,17,1,39.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(30,25,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(30,16,1,79.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(31,1,6,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(32,1,40,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(32,9,2,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(33,1,110,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(34,1,120,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(35,1,110,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(35,7,1,474.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(36,1,130,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(37,18,1,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(38,18,1,125.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(39,1,110,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(40,1,40,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(40,9,2,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(41,1,40,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(41,9,2,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(42,1,40,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(42,9,2,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(43,18,1,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(43,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(43,20,1,269.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(43,8,1,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(43,25,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(44,8,1,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(44,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(44,18,1,125.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(44,20,1,269.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(44,25,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(45,18,1,125.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(46,1,1,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(46,4,1,549.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(46,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(46,5,1,5.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(46,20,3001,269.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(47,1,1,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(47,5,1,5.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(47,11,1,39.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(47,19,1,15.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(47,16,1,79.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(47,8,1,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(48,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(48,18,1,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(49,1,3,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(50,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(50,18,1,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,5,3,5.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,9,3,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,17,3,39.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,3,4,149.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,1,1,499.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,24,5,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,28,3,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,27,3,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,11,3,39.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,8,5,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,18,3,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,22,2,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,15,3,129.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,7,3,474.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,19,2,15.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,14,2,69.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,23,2,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,29,3,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,26,3,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,30,2,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,12,2,279.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,13,2,373.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,20,2,269.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,25,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,21,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,16,1,79.95);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,10,1,34.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,6,1,1134.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(51,4,1,549.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(52,8,1,49.99);
INSERT INTO "TPRESUPUESTADOS" VALUES(52,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(52,18,1,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(52,20,1,269.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(52,25,1,100.5);
INSERT INTO "TPRESUPUESTADOS" VALUES(53,20,6,269.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(54,18,1,127.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(55,18,1,125.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(56,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(56,18,1,125.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(57,9,1,79.0);
INSERT INTO "TPRESUPUESTADOS" VALUES(57,18,1,5000.0);
CREATE TABLE "TVENTA_PRODUCTOS" (
	"ID_VENTA"	INTEGER NOT NULL CHECK(ID_VENTA>0),
	"ID_PRODUCTO"	INTEGER NOT NULL CHECK(ID_PRODUCTO>0),
	"CANTIDAD"	INTEGER NOT NULL DEFAULT 1 CHECK(CANTIDAD>0),
	"CUANTIA"	REAL NOT NULL CHECK(CUANTIA>0),
	FOREIGN KEY("ID_PRODUCTO") REFERENCES "TPRODUCTOS"("ID"),
	PRIMARY KEY("ID_VENTA","ID_PRODUCTO"),
	FOREIGN KEY("ID_VENTA") REFERENCES "TVENTAS"("ID")
);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(1,5,2,5.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(2,3,1,149.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(2,6,1,1134.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(2,8,1,49.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(3,16,2,79.95);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(3,14,4,69.95);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(4,3,2,149.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(4,4,1,549.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(4,8,1,49.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(4,9,1,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(4,12,5,279.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(4,10,1,34.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(5,6,2,1134.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(5,9,2,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(6,3,1,149.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(6,4,3,549.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(6,2,2,19.95);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(6,5,2,5.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(6,1,2,519.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(6,6,1,1134.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(7,8,3,49.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(8,1,3,519.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(8,4,1,549.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(8,8,1,49.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(8,9,1,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(9,9,1,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(9,18,1,127.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,1,1,499.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,3,4,149.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,4,1,549.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,5,3,5.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,6,1,1134.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,7,3,474.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,8,5,49.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,9,3,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,10,1,34.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,11,3,39.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,12,2,279.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,13,2,373.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,14,2,69.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,15,3,129.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,16,1,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,17,3,39.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,18,3,127.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,19,2,15.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,20,2,269.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,21,1,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,22,2,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,23,2,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,24,5,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,25,1,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,26,3,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,27,3,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,28,3,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,29,3,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(10,30,2,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(11,1,1,499.95);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(12,8,1,49.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(12,9,1,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(12,18,1,127.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(12,20,1,269.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(12,25,1,100.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(13,18,1,127.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(14,18,1,125.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(15,2,5,19.95);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(15,10,9,34.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(15,12,1,279.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(16,8,2,49.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(16,3,4,149.99);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(17,9,1,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(17,18,1,125.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(18,9,1,79.0);
INSERT INTO "TVENTA_PRODUCTOS" VALUES(18,18,1,5000.0);
CREATE TABLE "TSOLICITADOS" (
	"ID_PRODUCTO"	INTEGER NOT NULL CHECK(ID_PRODUCTO>0),
	"ID_PEDIDO_PROVEEDOR"	INTEGER NOT NULL CHECK(ID_PEDIDO_PROVEEDOR>0),
	"CANTIDAD"	INTEGER NOT NULL DEFAULT 1 CHECK(CANTIDAD>0),
	"CUANTIA"	REAL NOT NULL CHECK(CUANTIA>0),
	FOREIGN KEY("ID_PEDIDO_PROVEEDOR") REFERENCES "TPEDIDOS_PROVEEDOR"("ID"),
	FOREIGN KEY("ID_PRODUCTO") REFERENCES "TPRODUCTOS"("ID"),
	PRIMARY KEY("ID_PRODUCTO","ID_PEDIDO_PROVEEDOR")
);
INSERT INTO "TSOLICITADOS" VALUES(1,1,1,419.99);
INSERT INTO "TSOLICITADOS" VALUES(3,1,5,99.95);
INSERT INTO "TSOLICITADOS" VALUES(5,1,3,0.99);
INSERT INTO "TSOLICITADOS" VALUES(7,1,6,399.95);
INSERT INTO "TSOLICITADOS" VALUES(9,1,9,49.95);
INSERT INTO "TSOLICITADOS" VALUES(11,1,3,19.95);
INSERT INTO "TSOLICITADOS" VALUES(13,1,6,278.95);
INSERT INTO "TSOLICITADOS" VALUES(15,1,10,99.95);
INSERT INTO "TSOLICITADOS" VALUES(2,2,1,7.95);
INSERT INTO "TSOLICITADOS" VALUES(4,2,2,499.95);
INSERT INTO "TSOLICITADOS" VALUES(6,2,4,1000.0);
INSERT INTO "TSOLICITADOS" VALUES(8,2,1,20.0);
INSERT INTO "TSOLICITADOS" VALUES(10,2,7,10.0);
INSERT INTO "TSOLICITADOS" VALUES(12,2,10,199.95);
INSERT INTO "TSOLICITADOS" VALUES(14,2,20,40.0);
INSERT INTO "TSOLICITADOS" VALUES(16,2,5,40.95);
INSERT INTO "TSOLICITADOS" VALUES(5,3,9,0.99);
INSERT INTO "TSOLICITADOS" VALUES(10,3,7,10.0);
INSERT INTO "TSOLICITADOS" VALUES(15,3,5,99.95);
INSERT INTO "TSOLICITADOS" VALUES(20,3,3,195.95);
INSERT INTO "TSOLICITADOS" VALUES(25,3,10,69.95);
INSERT INTO "TSOLICITADOS" VALUES(30,3,10,70.95);
INSERT INTO "TSOLICITADOS" VALUES(3,4,2,99.95);
INSERT INTO "TSOLICITADOS" VALUES(13,4,20,278.95);
INSERT INTO "TSOLICITADOS" VALUES(23,4,12,29.99);
INSERT INTO "TSOLICITADOS" VALUES(1,5,20,419.99);
INSERT INTO "TSOLICITADOS" VALUES(12,5,15,199.95);
INSERT INTO "TSOLICITADOS" VALUES(14,6,50,40.0);
INSERT INTO "TSOLICITADOS" VALUES(8,7,30,20.0);
INSERT INTO "TSOLICITADOS" VALUES(22,7,20,69.95);
INSERT INTO "TSOLICITADOS" VALUES(14,8,24,40.0);
INSERT INTO "TSOLICITADOS" VALUES(27,8,16,10.99);
INSERT INTO "TSOLICITADOS" VALUES(1,8,13,419.99);
INSERT INTO "TSOLICITADOS" VALUES(2,8,3,7.95);
INSERT INTO "TSOLICITADOS" VALUES(16,9,4,40.95);
INSERT INTO "TSOLICITADOS" VALUES(2,9,2,7.95);
INSERT INTO "TSOLICITADOS" VALUES(7,10,6,399.95);
INSERT INTO "TSOLICITADOS" VALUES(5,10,13,0.99);
INSERT INTO "TSOLICITADOS" VALUES(23,10,19,29.99);
INSERT INTO "TSOLICITADOS" VALUES(9,11,10,49.95);
INSERT INTO "TSOLICITADOS" VALUES(7,11,5,399.95);
INSERT INTO "TSOLICITADOS" VALUES(17,12,2,19.0);
INSERT INTO "TSOLICITADOS" VALUES(1,13,100000,335.99);
INSERT INTO "TSOLICITADOS" VALUES(25,14,1,69.0);
INSERT INTO "TSOLICITADOS" VALUES(10,15,7,10.0);
INSERT INTO "TSOLICITADOS" VALUES(12,15,1,199.0);
INSERT INTO "TSOLICITADOS" VALUES(10,16,11,9.5);
INSERT INTO "TSOLICITADOS" VALUES(10,17,21,9.0);
INSERT INTO "TSOLICITADOS" VALUES(10,18,51,8.0);
INSERT INTO "TSOLICITADOS" VALUES(18,19,1,89.0);
CREATE TABLE "TTRANSPORTES" (
	"ID"	INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	"ID_VENTA"	INTEGER NOT NULL CHECK(ID_VENTA>0) UNIQUE,
	"SOLICITA_MONTAJE"	TEXT NOT NULL CHECK(SOLICITA_MONTAJE IN ('SI','NO')),
	"ESTADO"	TEXT NOT NULL DEFAULT 'SOLICITADO' CHECK(ESTADO IN ('SOLICITADO','RECIBIDO','CANCELADO','BLOQUEADO')),
	"FECHA_ENTREGA"	TEXT NOT NULL,
	"HORA_ENTREGA"	TEXT NOT NULL,
	"ID_TRANSPORTISTA"	INTEGER CHECK(ID_TRANSPORTISTA>0),
	FOREIGN KEY("ID_VENTA") REFERENCES "TVENTAS"("ID"),
	PRIMARY KEY("ID")
);
INSERT INTO "TTRANSPORTES" VALUES(1,4,'SI','SOLICITADO','22/11/2019','12:00',NULL);
INSERT INTO "TTRANSPORTES" VALUES(2,8,'NO','SOLICITADO','15/11/2019','18:00',NULL);
INSERT INTO "TTRANSPORTES" VALUES(3,6,'SI','SOLICITADO','16/12/2019','17:00',NULL);
INSERT INTO "TTRANSPORTES" VALUES(4,2,'NO','SOLICITADO','18/12/2019','12:00',NULL);
INSERT INTO "TTRANSPORTES" VALUES(5,3,'SI','SOLICITADO','30/11/2019','12:05',NULL);
INSERT INTO "TTRANSPORTES" VALUES(6,5,'NO','RECIBIDO','25/10/2019','10:00',NULL);
INSERT INTO "TTRANSPORTES" VALUES(7,7,'NO','SOLICITADO','02/11/2024','11:00',NULL);
INSERT INTO "TTRANSPORTES" VALUES(8,9,'SI','SOLICITADO','28/12/2019','11:03',NULL);
INSERT INTO "TTRANSPORTES" VALUES(9,10,'SI','SOLICITADO','30/12/2021','18:25',NULL);
INSERT INTO "TTRANSPORTES" VALUES(10,11,'SI','SOLICITADO','24/12/2019','15:04',NULL);
CREATE TABLE "TPRESUPUESTOS" (
	"ID"	INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	"ESTADO"	TEXT NOT NULL DEFAULT 'NO ACEPTADO' CHECK(ESTADO IN ('NO ACEPTADO','ACEPTADO','MODELO')),
	"FECHA_CREACION"	TEXT NOT NULL,
	"FECHA_CADUCIDAD"	TEXT NOT NULL,
	"ID_CLIENTE"	INTEGER CHECK(ID_CLIENTE>0),
	PRIMARY KEY("ID"),
	FOREIGN KEY("ID_CLIENTE") REFERENCES "TCLIENTES"("ID")
);
INSERT INTO "TPRESUPUESTOS" VALUES(1,'NO ACEPTADO','01/11/2019','16/11/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(2,'NO ACEPTADO','02/11/2019','17/11/2019',2);
INSERT INTO "TPRESUPUESTOS" VALUES(3,'NO ACEPTADO','03/11/2019','18/11/2019',2);
INSERT INTO "TPRESUPUESTOS" VALUES(4,'NO ACEPTADO','04/11/2019','19/11/2019',2);
INSERT INTO "TPRESUPUESTOS" VALUES(5,'NO ACEPTADO','05/11/2019','20/11/2019',3);
INSERT INTO "TPRESUPUESTOS" VALUES(6,'NO ACEPTADO','06/11/2019','21/11/2019',3);
INSERT INTO "TPRESUPUESTOS" VALUES(7,'NO ACEPTADO','07/11/2019','22/11/2019',4);
INSERT INTO "TPRESUPUESTOS" VALUES(8,'NO ACEPTADO','08/11/2019','23/11/2019',5);
INSERT INTO "TPRESUPUESTOS" VALUES(9,'NO ACEPTADO','09/11/2019','24/11/2019',6);
INSERT INTO "TPRESUPUESTOS" VALUES(10,'NO ACEPTADO','10/11/2019','25/11/2019',7);
INSERT INTO "TPRESUPUESTOS" VALUES(11,'NO ACEPTADO','11/11/2019','26/11/2019',8);
INSERT INTO "TPRESUPUESTOS" VALUES(12,'NO ACEPTADO','08/11/2019','23/11/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(13,'NO ACEPTADO','08/11/2019','23/11/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(14,'NO ACEPTADO','08/11/2019','23/11/2019',4);
INSERT INTO "TPRESUPUESTOS" VALUES(15,'NO ACEPTADO','08/11/2019','23/11/2019',2);
INSERT INTO "TPRESUPUESTOS" VALUES(16,'NO ACEPTADO','08/11/2019','23/11/2019',3);
INSERT INTO "TPRESUPUESTOS" VALUES(17,'NO ACEPTADO','08/11/2019','23/11/2019',3);
INSERT INTO "TPRESUPUESTOS" VALUES(18,'NO ACEPTADO','08/11/2019','23/11/2019',5);
INSERT INTO "TPRESUPUESTOS" VALUES(19,'NO ACEPTADO','08/11/2019','23/11/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(20,'NO ACEPTADO','08/11/2019','23/11/2019',5);
INSERT INTO "TPRESUPUESTOS" VALUES(21,'NO ACEPTADO','11/11/2019','26/11/2019',4);
INSERT INTO "TPRESUPUESTOS" VALUES(22,'MODELO','11/11/2019','26/11/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(23,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(24,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(25,'NO ACEPTADO','01/12/2019','16/12/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(26,'NO ACEPTADO','01/12/2019','16/12/2019',10);
INSERT INTO "TPRESUPUESTOS" VALUES(27,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(28,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(29,'NO ACEPTADO','01/12/2019','16/12/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(30,'NO ACEPTADO','01/12/2019','16/12/2019',11);
INSERT INTO "TPRESUPUESTOS" VALUES(31,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(32,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(33,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(34,'NO ACEPTADO','01/12/2019','16/12/2019',3);
INSERT INTO "TPRESUPUESTOS" VALUES(35,'NO ACEPTADO','01/12/2019','16/12/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(36,'NO ACEPTADO','01/12/2019','16/12/2019',12);
INSERT INTO "TPRESUPUESTOS" VALUES(37,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(38,'NO ACEPTADO','01/12/2019','16/12/2019',2);
INSERT INTO "TPRESUPUESTOS" VALUES(39,'NO ACEPTADO','01/12/2019','16/12/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(40,'NO ACEPTADO','01/12/2019','16/12/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(41,'NO ACEPTADO','01/12/2019','16/12/2019',13);
INSERT INTO "TPRESUPUESTOS" VALUES(42,'NO ACEPTADO','01/12/2019','16/12/2019',14);
INSERT INTO "TPRESUPUESTOS" VALUES(43,'MODELO','01/12/2019','16/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(44,'NO ACEPTADO','01/12/2019','16/12/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(45,'NO ACEPTADO','01/12/2019','16/12/2019',4);
INSERT INTO "TPRESUPUESTOS" VALUES(46,'NO ACEPTADO','02/12/2019','17/12/2019',14);
INSERT INTO "TPRESUPUESTOS" VALUES(47,'MODELO','02/12/2019','17/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(48,'MODELO','02/12/2019','17/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(49,'NO ACEPTADO','02/12/2019','17/12/2019',15);
INSERT INTO "TPRESUPUESTOS" VALUES(50,'ACEPTADO','02/12/2019','17/12/2019',15);
INSERT INTO "TPRESUPUESTOS" VALUES(51,'ACEPTADO','02/12/2019','17/12/2019',15);
INSERT INTO "TPRESUPUESTOS" VALUES(52,'ACEPTADO','02/12/2019','17/12/2019',6);
INSERT INTO "TPRESUPUESTOS" VALUES(53,'MODELO','02/12/2019','17/12/2019',NULL);
INSERT INTO "TPRESUPUESTOS" VALUES(54,'ACEPTADO','02/12/2019','17/12/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(55,'ACEPTADO','02/12/2019','17/12/2019',1);
INSERT INTO "TPRESUPUESTOS" VALUES(56,'ACEPTADO','02/12/2019','17/12/2019',5);
INSERT INTO "TPRESUPUESTOS" VALUES(57,'ACEPTADO','02/12/2019','17/12/2019',4);
CREATE TABLE "TVENTAS" (
	"ID"	INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	"ID_PRESUPUESTO"	INTEGER CHECK(ID_PRESUPUESTO>0) UNIQUE,
	"ID_CLIENTE"	INTEGER CHECK(ID_CLIENTE>0),
	"FECHA"	TEXT NOT NULL,
	"ID_METODO_DE_PAGO"	INTEGER CHECK(ID_METODO_DE_PAGO>0) UNIQUE,
	PRIMARY KEY("ID"),
	FOREIGN KEY("ID_PRESUPUESTO") REFERENCES "TPRESUPUESTOS"("ID"),
	FOREIGN KEY("ID_METODO_DE_PAGO") REFERENCES "TMETODOS_DE_PAGO"("ID")
);
INSERT INTO "TVENTAS" VALUES(1,1,1,'20/10/2019',NULL);
INSERT INTO "TVENTAS" VALUES(2,2,2,'20/09/2019',NULL);
INSERT INTO "TVENTAS" VALUES(3,3,2,'20/08/2019',NULL);
INSERT INTO "TVENTAS" VALUES(4,4,2,'20/07/2019',NULL);
INSERT INTO "TVENTAS" VALUES(5,5,3,'15/10/2019',NULL);
INSERT INTO "TVENTAS" VALUES(6,6,3,'14/10/2018',NULL);
INSERT INTO "TVENTAS" VALUES(7,7,4,'13/10/2018',NULL);
INSERT INTO "TVENTAS" VALUES(8,8,5,'12/10/2018',NULL);
INSERT INTO "TVENTAS" VALUES(9,50,15,'02/12/2019',NULL);
INSERT INTO "TVENTAS" VALUES(10,51,15,'02/12/2019',NULL);
INSERT INTO "TVENTAS" VALUES(11,NULL,NULL,'02/12/2019',2);
INSERT INTO "TVENTAS" VALUES(12,52,6,'02/12/2019',NULL);
INSERT INTO "TVENTAS" VALUES(13,54,1,'02/12/2019',NULL);
INSERT INTO "TVENTAS" VALUES(14,55,1,'02/12/2019',NULL);
INSERT INTO "TVENTAS" VALUES(15,NULL,NULL,'02/12/2019',3);
INSERT INTO "TVENTAS" VALUES(16,NULL,NULL,'02/12/2019',4);
INSERT INTO "TVENTAS" VALUES(17,56,5,'02/12/2019',NULL);
INSERT INTO "TVENTAS" VALUES(18,57,4,'02/12/2019',NULL);
CREATE TABLE "TEFECTIVOS" (
	"ID"	INTEGER NOT NULL CHECK(ID>0) UNIQUE,
	FOREIGN KEY("ID") REFERENCES "TMETODOS_DE_PAGO"("ID"),
	PRIMARY KEY("ID")
);
INSERT INTO "TEFECTIVOS" VALUES(2);
INSERT INTO "TEFECTIVOS" VALUES(3);
COMMIT;
