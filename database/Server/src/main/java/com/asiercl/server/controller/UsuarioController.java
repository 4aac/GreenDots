package com.asiercl.server.controller;

import com.asiercl.server.dao.UsuarioDAO;
import com.asiercl.server.models.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UsuarioController {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @GetMapping("/")
    public String index() {
        return "Hello World!";
    }

    @GetMapping("/get/{nickname}")
    public Usuario getUser(@PathVariable String nickname) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorNickname(nickname);
        return  usuario;
    }

    @GetMapping("/getall")
    public List<Usuario> getAllUser() {
        return usuarioDAO.obtenerTodosLosUsuarios();
    }

    @PostMapping("/new")
    public ResponseEntity<String> createUser(@RequestBody Usuario usuario) {
        try {
            usuarioDAO.insertarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el usuario");
        }
    }

    @DeleteMapping("/delete/{nickname}")
    public ResponseEntity<String> deleteUser(@PathVariable String nickname) {
        try {
            usuarioDAO.eliminarUsuarioPorNickname(nickname);
            return ResponseEntity.status(HttpStatus.OK).body("Usuario eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario");
        }
    }
}
