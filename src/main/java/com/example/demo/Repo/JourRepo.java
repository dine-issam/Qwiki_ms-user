package com.example.demo.Repo;

import com.example.demo.Entity.Jour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JourRepo extends JpaRepository<Jour, Long> {
    Optional<Jour> findByNomJour(String nomJour);
}
