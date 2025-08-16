package com.example.demo.Repo;

import com.example.demo.Entity.Boutique;
import com.example.demo.Entity.HeuresTravail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HeuresTravailRepo extends JpaRepository<HeuresTravail, Long> {
    List<HeuresTravail> findByBoutique(Boutique boutique);
    void deleteByBoutique(Boutique boutique);
}
