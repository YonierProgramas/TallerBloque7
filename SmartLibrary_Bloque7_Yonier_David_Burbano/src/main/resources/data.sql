-- Identificadores fijos para demostrar la API sin crear otros endpoints.
INSERT INTO estudiantes (id, nombre, correo) VALUES
  (1, 'Ana Gomez', 'ana@smartlibrary.edu'),
  (2, 'Luis Mora', 'luis@smartlibrary.edu');

INSERT INTO libros (id, titulo, disponible) VALUES
  (1, 'Ingenieria de Software', TRUE),
  (2, 'Estructuras de Datos', TRUE),
  (3, 'Patrones de Diseno', FALSE);
