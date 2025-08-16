package com.example.demo.auth;

import com.example.demo.Repo.UserRepo;
import com.example.demo.config.JwtService;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Entity.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final JwtService jwtService;
    private final UserRepo userRepo;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }


    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest updateUserRequest) {
        var updatedUser = service.updateUser(userId, updateUserRequest);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/upgrade-to-livreur/{userId}")
    public ResponseEntity<String> upgradeToLivreur(
            @PathVariable Long userId,
            @RequestBody LivreurRequest livreurRequest) {
        try {
            service.upgradeToLivreur(userId, livreurRequest);
            return ResponseEntity.ok("User upgraded to Livreur successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upgrade-to-commercant/{userId}")
    public ResponseEntity<String> upgradeToCommercant(
            @PathVariable Long userId,
            @RequestBody CommercantRequest commercantRequest) {
        try {
            service.upgradeToCommercant(userId, commercantRequest);
            return ResponseEntity.ok("User upgraded to Commerçant successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/downgrade-from-livreur/{userId}")
public ResponseEntity<String> downgradeFromLivreur(@PathVariable Long userId) {
    try {
        service.downgradeFromLivreur(userId);
        return ResponseEntity.ok("User downgraded from Livreur successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PostMapping("/downgrade-from-commercant/{userId}")
public ResponseEntity<String> downgradeFromCommercant(@PathVariable Long userId) {
    try {
        service.downgradeFromCommercant(userId);
        return ResponseEntity.ok("User downgraded from Commerçant successfully");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    @PostMapping("/create-boutique/{userId}")
    public ResponseEntity<?> createBoutique(@PathVariable Long userId, @RequestBody BoutiqueRequest boutiqueRequest) {
        Boutique boutique = service.createBoutique(userId, boutiqueRequest);
        return ResponseEntity.ok(boutique);
    }

    @PostMapping("/set-working-hours/{userId}/{boutiqueId}")
    public ResponseEntity<String> setWorkingHours(@PathVariable Long userId, @PathVariable Long boutiqueId,
            @RequestBody List<HeuresTravailRequest> heuresTravailRequest) {
        service.setWorkingHours(userId, boutiqueId, heuresTravailRequest);
        return ResponseEntity.ok("Set working hours successfully");
    }

    @PostMapping("/active/{userId}")
    public ResponseEntity<String> active(@PathVariable Long userId) {
        service.active(userId);
        return ResponseEntity.ok("Active successfully");
    }
    @GetMapping("/users-by-role")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@RequestParam Role role) {
        List<UserDTO> users = service.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    
@GetMapping("/users/{id}")
public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
    UserDTO user = service.getUserById(id);
    return ResponseEntity.ok(user);
}


    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        try {
            if (!jwtService.isTokenValid(token, new DummyUser(jwtService.extractUsername(token)))) {

                return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Invalid or expired token"));
            }
            String username = jwtService.extractUsername(token);
            User user = userRepo.findByEmail(username).get();
            Set<Role> roles = user.getRoles();
            if (!user.getActive()) {
                roles = new HashSet<>();
                Role role = Role.CLIENT;
                roles.add(role);
            }

            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", jwtService.extractUsername(token),
                    "roles", roles));
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Error verifying token"));
        }
    }

    static class DummyUser implements UserDetails {
        private final String username;

        public DummyUser(String username) {
            this.username = username;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
            return java.util.List.of();
        }

        @Override
        public String getPassword() {
            return "";
        }
    }
}
