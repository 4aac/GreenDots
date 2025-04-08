package com.asiercl.server.controller;

import com.asiercl.server.dao.OpinionDAO;
import com.asiercl.server.models.Local;
import com.asiercl.server.models.Opinion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.asiercl.server.utils.ImageUtils.redimensionarImagen;

@RestController
@RequestMapping("/opiniones")
public class OpinionController {
    private final OpinionDAO opinionDAO = new OpinionDAO();

    @PostMapping("/crear")
    public ResponseEntity<String> crearOpinion(@RequestBody Opinion opinion) {
        try {
            opinionDAO.insertarOpinion(opinion);
            return ResponseEntity.status(HttpStatus.CREATED).body("Opinión creada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la opinión");
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Opinion> obtenerOpinionesPorId(@PathVariable int id) {
        Opinion opinion = opinionDAO.obtenerOpinionesPorId(id);
        if (opinion == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(opinion);
        }
        return ResponseEntity.ok(opinion);
    }

    @GetMapping("/get/local/{id}")
    public ResponseEntity<List<Opinion>> obtenerOpinionesPorLocal(@PathVariable int id) {
        List<Opinion> opiniones = opinionDAO.obtenerOpinionesPorLocal(id);
        if (opiniones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(opiniones);
        }
        return ResponseEntity.ok(opiniones);
    }

    @GetMapping("/get/user/{nickname}")
    public ResponseEntity<List<Opinion>> obtenerOpinionesPorUsuario(@PathVariable String nickname) {
        List<Opinion> opiniones = opinionDAO.obtenerOpinionesPorUsuario(nickname);
        if (opiniones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(opiniones);
        }
        return ResponseEntity.ok(opiniones);
    }

    // Añadir imagen a opinion
    @PostMapping("/uploadImage/{id}")
    public ResponseEntity<String> subirImagen(@PathVariable int id, @RequestParam("imagen") MultipartFile file) {
        try {
            Opinion opinion = opinionDAO.obtenerOpinionesPorId(id);
            if (opinion == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Opinion no encontrada.");
            }

            byte[] imagenBytes = file.getBytes();

            // Verificar que la imagen no esté vacía
            System.out.println("Tamaño de la imagen recibida: " + imagenBytes.length + " bytes");

            if (imagenBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Archivo vacío.");
            }

            byte[] imagenRedimensionada = redimensionarImagen(imagenBytes, 512,512);

            opinion.setFoto(file.getBytes());
            opinionDAO.actualizarOpinion(opinion);
            return ResponseEntity.ok("Imagen subida con éxito.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen.");
        }
    }

    // Obtener imagen de una opinión por ID
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> obtenerImagenOpinion(@PathVariable int id) {
        Opinion opinion = opinionDAO.obtenerOpinionesPorId(id);
        if (opinion == null || opinion.getfoto() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // O IMAGE_PNG si usas PNGs
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"opinion_" + id + ".jpg\"")
                .body(opinion.getfoto());
    }
}
