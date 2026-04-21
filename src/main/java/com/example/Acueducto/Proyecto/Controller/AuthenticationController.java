package com.example.Acueducto.Proyecto.Controller;

import com.example.Acueducto.Proyecto.Model.Authentication;
import com.example.Acueducto.Proyecto.Model.Usuario;
import com.example.Acueducto.Proyecto.Service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Permite que el frontend se conecte sin bloqueos de seguridad
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody Authentication auth) {
        Usuario usuario = authenticationService.login(auth.getUsername(), auth.getPassword());
        return ResponseEntity.ok(usuario);
    }
    
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = authenticationService.register(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        authenticationService.resetPassword(request.get("email"), request.get("newPassword"));
        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }
}
