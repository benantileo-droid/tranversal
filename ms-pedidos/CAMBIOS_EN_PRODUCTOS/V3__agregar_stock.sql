-- Agregar columna stock a la tabla producto
ALTER TABLE producto ADD COLUMN stock INT NOT NULL DEFAULT 0;

-- Asignar stock inicial a cada producto
UPDATE producto SET stock = 50  WHERE nombre = 'notebook';
UPDATE producto SET stock = 30  WHERE nombre = 'notebook gamer';
UPDATE producto SET stock = 20  WHERE nombre = 'pc torre';
UPDATE producto SET stock = 15  WHERE nombre = 'pc torre gamer';
UPDATE producto SET stock = 100 WHERE nombre = 'monitor';
UPDATE producto SET stock = 40  WHERE nombre = 'monitor 144Hz';
UPDATE producto SET stock = 200 WHERE nombre = 'mouse';
UPDATE producto SET stock = 80  WHERE nombre = 'mouse gamer';
UPDATE producto SET stock = 300 WHERE nombre = 'teclado';
UPDATE producto SET stock = 100 WHERE nombre = 'teclado gamer';
