CREATE TABLE Usuarios (
    Nickname VARCHAR(50) PRIMARY KEY,
    NombreCompleto VARCHAR(255) NOT NULL,
    Email VARCHAR(255) UNIQUE NOT NULL,
    Password TEXT NOT NULL,
    FechaCreacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Admin BOOLEAN DEFAULT FALSE,
    FotoPerfil BYTEA  -- Se puede almacenar la imagen en formato binario
);

CREATE TABLE Locales (
    ID SERIAL PRIMARY KEY,
    Nombre VARCHAR(255) NOT NULL,
    Categoria VARCHAR(50) NOT NULL,
    FechaAdmision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Ubicacion TEXT NOT NULL,
    DescripcionTextual TEXT,
    Ecosostenible INT CHECK (Ecosostenible BETWEEN 0 AND 5),
    InclusionSocial INT CHECK (InclusionSocial BETWEEN 0 AND 5),
    Accesibilidad INT CHECK (Accesibilidad BETWEEN 0 AND 5)
);

CREATE TABLE LocalesFotos (
    ID SERIAL PRIMARY KEY,
    LocalID INT REFERENCES Locales(ID) ON DELETE CASCADE,
    Foto BYTEA  -- Se almacena la imagen en formato binario
);

CREATE TABLE Favoritos (
    UsuarioNickname VARCHAR(50) REFERENCES Usuarios(Nickname) ON DELETE CASCADE,
    LocalID INT REFERENCES Locales(ID) ON DELETE CASCADE,
    PRIMARY KEY (UsuarioNickname, LocalID)
);

CREATE TABLE Opiniones (
    ID SERIAL PRIMARY KEY,
    UsuarioNickname VARCHAR(50) REFERENCES Usuarios(Nickname) ON DELETE CASCADE,
    LocalID INT REFERENCES Locales(ID) ON DELETE CASCADE,
    FechaPublicacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ResenaTexto TEXT,
    Ecosostenible INT CHECK (Ecosostenible BETWEEN 0 AND 5),
    InclusionSocial INT CHECK (InclusionSocial BETWEEN 0 AND 5),
    Accesibilidad INT CHECK (Accesibilidad BETWEEN 0 AND 5)
);

CREATE TABLE OpinionesFotos (
    ID SERIAL PRIMARY KEY,
    OpinionID INT REFERENCES Opiniones(ID) ON DELETE CASCADE,
    Foto BYTEA  -- Se almacena la imagen en formato binario
);


------- INSERTAMOS DATOS --------

-- Insertar Usuarios
INSERT INTO Usuarios (Nickname, NombreCompleto, Email, Password, Admin) VALUES
('juan23', 'Juan Pérez', 'juan@example.com', 'hashed_password_juan', TRUE),
('maria89', 'María López', 'maria@example.com', 'hashed_password_maria', FALSE),
('carlos_m', 'Carlos Martínez', 'carlos@example.com', 'hashed_password_carlos', FALSE);

-- Insertar Locales
INSERT INTO Locales (Nombre, Categoria, Ubicacion, DescripcionTextual, Ecosostenible, InclusionSocial, Accesibilidad) VALUES
('Café Verde', 'Cafetería', 'Calle Mayor 123, Madrid', 'Un café ecológico con opciones veganas.', 5, 4, 5),
('BiblioBar', 'Bar', 'Avenida del Saber 45, Barcelona', 'Un bar con un ambiente tranquilo y muchos libros.', 3, 5, 4),
('Parque Natural', 'Zona Recreativa', 'Calle del Bosque 10, Valencia', 'Un parque con espacios verdes y senderos accesibles.', 5, 5, 5);

-- Insertar LocalesFotos (Ejemplo sin datos binarios reales)
INSERT INTO LocalesFotos (LocalID) VALUES
(1), (2), (3);

-- Insertar Favoritos
INSERT INTO Favoritos (UsuarioNickname, LocalID) VALUES
('juan23', 1),
('maria89', 2),
('carlos_m', 3),
('juan23', 3);

-- Insertar Opiniones
INSERT INTO Opiniones (UsuarioNickname, LocalID, ResenaTexto, Ecosostenible, InclusionSocial, Accesibilidad) VALUES
('juan23', 1, 'Excelente lugar con opciones saludables.', 5, 4, 5),
('maria89', 2, 'Un ambiente perfecto para leer y tomar algo.', 3, 5, 4),
('carlos_m', 3, 'Muy buen sitio para pasar el día.', 5, 5, 5);

-- Insertar OpinionesFotos (Ejemplo sin datos binarios reales)
INSERT INTO OpinionesFotos (OpinionID) VALUES
(1), (2), (3);

