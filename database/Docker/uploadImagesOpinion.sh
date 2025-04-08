#!/bin/bash

curl -X POST http://osbby.servegame.com:8080/opiniones/uploadImage/1 -F "imagen=@./etse1.jpg"
curl -X POST http://osbby.servegame.com:8080/opiniones/uploadImage/2 -F "imagen=@./etse2.jpg"
curl -X POST http://osbby.servegame.com:8080/opiniones/uploadImage/3 -F "imagen=@./etse3.jpg"
curl -X POST http://osbby.servegame.com:8080/opiniones/uploadImage/4 -F "imagen=@./coimbra2.jpg"
curl -X POST http://osbby.servegame.com:8080/opiniones/uploadImage/5 -F "imagen=@./coimbra1.jpg"

echo "Imagenes de opiniones enviadas"

