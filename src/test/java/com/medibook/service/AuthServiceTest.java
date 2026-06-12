package com.medibook.service;

import com.medibook.dto.LoginRequest;
import com.medibook.dto.LoginResponse;
import com.medibook.dto.RegisterRequest;
import com.medibook.entity.Rol;
import com.medibook.entity.Usuario;
import com.medibook.exception.BadRequestException;
import com.medibook.repository.RolRepository;
import com.medibook.repository.UsuarioRepository;
import com.medibook.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setId(1L);
        rol.setNombre("PACIENTE");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("juan@mail.com");
        usuario.setPassword("encoded_password");
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);
    }

    @Test
    void login_exitoso() {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@mail.com");
        request.setPassword("123456");

        UserDetails userDetails = User.withUsername("juan@mail.com")
                .password("encoded_password")
                .roles("PACIENTE")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(anyString())).thenReturn("token_jwt");
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("token_jwt", response.getToken());
        assertEquals("juan@mail.com", response.getEmail());
        assertEquals("Juan", response.getNombre());
    }

    @Test
    void register_exitoso() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Maria");
        request.setApellido("Lopez");
        request.setEmail("maria@mail.com");
        request.setPassword("123456");

        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(rolRepository.findByNombre("PACIENTE")).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        String resultado = authService.register(request);

        assertEquals("Usuario registrado exitosamente", resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void register_emailDuplicado_lanzaExcepcion() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("juan@mail.com");
        request.setPassword("123456");

        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}