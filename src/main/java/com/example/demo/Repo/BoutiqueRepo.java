package com.example.demo.Repo;

import com.example.demo.Entity.Boutique;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoutiqueRepo extends JpaRepository<Boutique, Long> {
}
