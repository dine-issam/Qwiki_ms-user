package com.example.demo.Repo;

import com.example.demo.Entity.Commercant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommercentRepo extends JpaRepository<Commercant,Long> {
    Commercant findByUser_Id(Long userId);
}
