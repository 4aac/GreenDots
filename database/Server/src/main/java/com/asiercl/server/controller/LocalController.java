package com.asiercl.server.controller;

import com.asiercl.server.dao.LocalDAO;
import com.asiercl.server.models.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/local")
public class LocalController {
    private LocalDAO localDAO = new LocalDAO();

    @GetMapping("/get/{id}")
    public ResponseEntity<Local> getLocal(@PathVariable int id) {
        Local local = localDAO.obtenerLocalPorId(id);
        if (local != null) {
            return ResponseEntity.ok(local);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/getall")
    public List<Local> getAllLocales() {
        return localDAO.obtenerTodosLosLocales();
    }

    @PostMapping("/new")
    public ResponseEntity<String> createLocal(@RequestBody Local local) {
        try {
            localDAO.insertarLocal(local);
            return ResponseEntity.status(HttpStatus.CREATED).body("Local creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el local");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLocal(@PathVariable int id) {
        try {
            localDAO.eliminarLocalPorId(id);
            return ResponseEntity.ok("Local eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el local");
        }
    }
}
