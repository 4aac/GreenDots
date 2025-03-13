package com.asiercl.server.controller;

import com.asiercl.server.dao.UsuarioDAO;
import com.asiercl.server.models.Usuario;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UsuarioController {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Endpoints //

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


    /////////////////////////////////////////////////////////7
    // Subir imagen de perfil
    @PostMapping("/upload/{nickname}")
    public ResponseEntity<String> subirImagen(@PathVariable String nickname, @RequestParam("imagen") MultipartFile file) {
        try {
            Usuario usuario = usuarioDAO.obtenerUsuarioPorNickname(nickname);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            byte[] imagenBytes = file.getBytes();

            // Verificar que la imagen no esté vacía
            System.out.println("Tamaño de la imagen recibida: " + imagenBytes.length + " bytes");

            if (imagenBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Archivo vacío.");
            }

            usuario.setFotoPerfil(file.getBytes());
            usuarioDAO.actualizarUsuario(usuario);
            return ResponseEntity.ok("Imagen subida con éxito.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen.");
        }
    }

    // Obtener imagen de perfil
    @GetMapping("/image/{nickname}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable String nickname) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorNickname(nickname);
        if (usuario == null || usuario.getFotoPerfil() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nickname + "_profile.jpg\"")
                .body(usuario.getFotoPerfil());
    }

    // Actualizar imagen de perfil
    @PutMapping("/update-image/{nickname}")
    public ResponseEntity<String> actualizarImagen(@PathVariable String nickname, @RequestParam("imagen") MultipartFile file) {
        try {
            Usuario usuario = usuarioDAO.obtenerUsuarioPorNickname(nickname);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }

            usuario.setFotoPerfil(file.getBytes());
            usuarioDAO.actualizarUsuario(usuario);
            return ResponseEntity.ok("Imagen actualizada con éxito.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la imagen.");
        }
    }

    // Eliminar imagen de perfil
    @DeleteMapping("/delete-image/{nickname}")
    public ResponseEntity<String> eliminarImagen(@PathVariable String nickname) {
        Usuario usuario = usuarioDAO.obtenerUsuarioPorNickname(nickname);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        usuario.setFotoPerfil(null);
        usuarioDAO.actualizarUsuario(usuario);
        return ResponseEntity.ok("Imagen eliminada con éxito.");
    }
}
