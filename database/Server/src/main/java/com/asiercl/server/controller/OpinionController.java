package com.asiercl.server.controller;

import com.asiercl.server.dao.OpinionDAO;
import com.asiercl.server.models.Opinion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/local/{id}")
    public ResponseEntity<List<Opinion>> obtenerOpinionesPorLocal(@PathVariable int id) {
        List<Opinion> opiniones = opinionDAO.obtenerOpinionesPorLocal(id);
        if (opiniones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(opiniones);
        }
        return ResponseEntity.ok(opiniones);
    }
}
