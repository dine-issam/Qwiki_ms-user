package com.example.demo.Entity;

import java.time.LocalTime;

import com.example.demo.Entity.Boutique;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeuresTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime heureDebut;
    private LocalTime heureFin;

    @ManyToOne
    @JoinColumn(name = "boutique_id")
    private Boutique boutique;

    @ManyToOne
    @JoinColumn(name = "jour_id")
    private Jour jour;
}
