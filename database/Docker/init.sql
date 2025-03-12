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
