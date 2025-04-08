#!/bin/bash

# Directorio donde se encuentran las imágenes
IMAGES_DIR="./fotosPerfil"

# Iteramos sobre cada archivo .jpg en el directorio
for file in "${IMAGES_DIR}"/*.jpg; do
    # Extraemos el nombre del archivo sin extensión
    filename=$(basename "$file")
    name="${filename%.*}"
    
    # Ejecutamos el comando curl con la imagen correspondiente
    curl -X POST "http://osbby.servegame.com:8080/user/upload/${name}" \
         -H "Content-Type: multipart/form-data" \
         -F "imagen=@${file}"
    
    echo "Imagen ${filename} enviada al endpoint ${name}"
done

