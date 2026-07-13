CREATE TABLE stock (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    producto_id BIGINT       NOT NULL UNIQUE,
    nombre      VARCHAR(100) NOT NULL,
    cantidad    INT          NOT NULL DEFAULT 0,
    stock_min   INT          NOT NULL DEFAULT 5
);

INSERT INTO stock (producto_id, nombre, cantidad, stock_min) VALUES
(1,  'notebook',        50,  5),
(2,  'notebook gamer',  30,  5),
(3,  'pc torre',        20,  3),
(4,  'pc torre gamer',  15,  3),
(5,  'monitor',         100, 10),
(6,  'monitor 144Hz',   40,  5),
(7,  'mouse',           200, 20),
(8,  'mouse gamer',     80,  10),
(9,  'teclado',         300, 20),
(10, 'teclado gamer',   100, 10);
