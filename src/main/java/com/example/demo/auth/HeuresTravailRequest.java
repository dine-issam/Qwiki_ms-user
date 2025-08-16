package com.example.demo.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeuresTravailRequest {
    private String jour;      // Day of the week (e.g., "Monday")
    private String heureDebut;  // Start time (e.g., "09:00")
    private String heureFin;    // End time (e.g., "18:00")

    // Getters and Setters
}
