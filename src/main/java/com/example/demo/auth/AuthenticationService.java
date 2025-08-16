package com.example.demo.auth;

import com.example.demo.Entity.*;
import com.example.demo.Repo.*;
import com.example.demo.config.JwtService;
import com.example.demo.kafka.KafkaPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
        private final UserRepo userRepo;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final BoutiqueRepo boutiqueRepo;
        private final HeuresTravailRepo heuresTravailRepo;
        private final JourRepo jourRepo;
        private final CommercentRepo commercentRepo;
        private final KafkaPublisher kafkaPublisher;
        private final ObjectMapper objectMapper;

        public AuthenticationResponse register(RegisterRequest request) {
                var user = User.builder()
                                .firstName(request.getFirstname())
                                .lastName(request.getLastname())
                                .email(request.getEmail())
                                .phone(request.getPhone())
                                .active(true)
                                .address(null)
                                .password(passwordEncoder.encode(request.getPassword()))
                                .roles(Set.of(Role.CLIENT)) // Default role is CLIENT
                                .build();
                userRepo.save(user);
                Long userId = userRepo.findByEmail(user.getEmail()).get().getId();
                var jwtToken = jwtService.generateToken(user);
                var refreshToken = jwtService.generateRefreshToken(user); // Generate refresh token
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .refreshToken(refreshToken).userId(userId) // Include refresh token in the response
                                .build();
        }

        public AuthenticationResponse authenticate(AuthenticationRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                System.out.println("before");
                var user = userRepo.findByEmail(request.getEmail())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                System.out.println("after");

                var jwtToken = jwtService.generateToken(user);
                var refreshToken = jwtService.generateRefreshToken(user); // Generate refresh token
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .refreshToken(refreshToken).userId(user.getId()) // Include refresh token in the
                                                                                 // response
                                .build();
        }


        public User updateUser(Long userId, UpdateUserRequest updates) {
                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (updates.getFirstname() != null) {
                        user.setFirstName(updates.getFirstname());
                }
                if (updates.getLastname() != null) {
                        user.setLastName(updates.getLastname());
                }

                if (updates.getPhone() != null) {
                        user.setPhone(updates.getPhone());
                }

                return userRepo.save(user);
        }

        public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
                String refreshToken = request.getRefreshToken();
                String username = jwtService.extractUsername(refreshToken);

                if (username != null && jwtService.isTokenValid(refreshToken,
                                userRepo.findByEmail(username).orElseThrow())) {
                        var user = userRepo.findByEmail(username).orElseThrow();
                        var newToken = jwtService.generateToken(user);
                        var newRefreshToken = jwtService.generateRefreshToken(user); // Generate new refresh token
                        return AuthenticationResponse.builder()
                                        .token(newToken)
                                        .refreshToken(newRefreshToken) // Include new refresh token in the response
                                        .build();
                }
                throw new RuntimeException("Invalid refresh token");
        }

        public void upgradeToLivreur(Long userId, LivreurRequest livreurRequest) {
                var user = userRepo.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                if (user.getCommercant() != null)
                        throw new RuntimeException("you're commercant");
                var livreur = Livreur.builder()
                                .cartNationalId(livreurRequest.getCartNationalId())
                                .vehiclePapiers(livreurRequest.getVehiclePapiers())
                                .user(user)
                                .build();

        user.setLivreur(livreur);
        user.getRoles().add(Role.LIVREUR);
        user.setActive(false);// Add LIVREUR role
        userRepo.save(user);
       // kafkaPublisher.sendMessage("livreur", "{\"livreurId\":" + user.getId() + "}");
    }
    public void downgradeFromLivreur(Long userId) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        if (user.getLivreur() == null) {
            throw new RuntimeException("User is not a Livreur");
        }
    
        // Remove LIVREUR role
        user.getRoles().remove(Role.LIVREUR);
    
        // Remove the Livreur entity
        user.setLivreur(null); // orphanRemoval + cascade will delete the Livreur
    
        // Optional: make user active again (or keep false if still under review)
        user.setActive(true);
    
        // Save changes
        userRepo.save(user);
    
        // Optionally send Kafka event
        // kafkaPublisher.sendMessage("livreur-removed", "{\"userId\":" + user.getId() + "}");
    }
    
    
    public void upgradeToCommercant(Long userId, CommercantRequest commercantRequest) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

                if (user.getLivreur() != null)
                        throw new RuntimeException("you're Livreur");
                var commercant = Commercant.builder()
                                .cartNationalId(commercantRequest.getCartNationalId())
                                .type(commercantRequest.getType())
                                .user(user)
                                .build();

                user.setCommercant(commercant);
                user.setActive(false);
                user.getRoles().add(Role.COMMERCANT); // Add COMMERCANT role
                userRepo.save(user);

                // kafkaPublisher.sendMessage("commercent", "{\"commercentId\":" + user.getId()
                // + "}");

    }
    
    public void downgradeFromCommercant(Long userId) {
        var user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        if (user.getCommercant() == null) {
            throw new RuntimeException("User is not a Commercant");
        }
    
        // Remove COMMERCANT role
        user.getRoles().remove(Role.COMMERCANT);
    
        // Remove Commercant entity via JPA orphanRemoval
        user.setCommercant(null);
    
        // Optional: Reactivate user
        user.setActive(true);
    
        // Save changes
        userRepo.save(user);
    
        // Optional: notify via Kafka
        // kafkaPublisher.sendMessage("commercant-removed", "{\"userId\":" + user.getId() + "}");
    }
    
    public void setWorkingHours(Long userId, Long boutiqueId, List<HeuresTravailRequest> horaires) {
        Boutique boutique = boutiqueRepo.findById(boutiqueId)
                .orElseThrow(() -> new RuntimeException("Boutique not found"));

                if (!boutique.getCommercant().getUser().getId().equals(userId)) {
                        throw new RuntimeException("You are not the owner of this boutique");
                }

                // Remove old horaires
                heuresTravailRepo.deleteByBoutique(boutique);

                // Add new horaires
                for (HeuresTravailRequest req : horaires) {
                        // Jour jour = jourRepo.findByNomJour(req.getJour())
                        // .orElseThrow(() -> new RuntimeException("Invalid jour: " + req.getJour()));
                        Jour jour = new Jour(null, req.getJour(), null);
                        jourRepo.save(jour);
                        HeuresTravail h = new HeuresTravail();
                        h.setBoutique(boutique);
                        h.setJour(jour);
                        h.setHeureDebut(LocalTime.parse(req.getHeureDebut()));
                        h.setHeureFin(LocalTime.parse(req.getHeureFin()));
                        heuresTravailRepo.save(h);
                }
        }

        public Boutique createBoutique(Long userId, BoutiqueRequest boutiqueRequest) {
                // Fetch the user by userId
                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                Commercant commercant = commercentRepo.findByUser_Id(user.getId());
                // Create the boutique from the request
                Boutique boutique = new Boutique();
                boutique.setNom(boutiqueRequest.getNom());
                boutique.setAdresse(boutiqueRequest.getAdresse());

                // Link the boutique to the user (assuming a user has a list of boutiques)
                boutique.setCommercant(commercant);

        // Save the boutique
        return boutiqueRepo.save(boutique);
    }
    public  void active(Long userId)
    {
        User user=userRepo.findById(userId).get();
        user.setActive(true);
        userRepo.save(user);
    }
    public List<UserDTO> getUsersByRole(Role role) {
        List<User> users = userRepo.findUsersByRole(role);
        return users.stream()
                .map(this::mapToDto)
                .toList();
    }
    
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToDto(user);
    }
    public UserDTO mapToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .active(user.getActive())
                .roles(user.getRoles())
                .build();
    }
}
