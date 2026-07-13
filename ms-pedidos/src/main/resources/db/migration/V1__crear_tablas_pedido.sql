CREATE TABLE pedido (
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    fecha    DATETIME     NOT NULL,
    total    DOUBLE       NOT NULL,
    estado   VARCHAR(50)  NOT NULL DEFAULT 'CONFIRMADO'
);

CREATE TABLE detalle_pedido (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    pedido_id    BIGINT NOT NULL,
    producto_id  BIGINT NOT NULL,
    cantidad     INT    NOT NULL,
    precio_unit  DOUBLE NOT NULL,
    subtotal     DOUBLE NOT NULL,
    CONSTRAINT fk_pedido FOREIGN KEY (pedido_id) REFERENCES pedido (id)
);
