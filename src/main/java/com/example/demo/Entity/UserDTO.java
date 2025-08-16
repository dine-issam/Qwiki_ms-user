package com.example.demo.Entity;

import com.example.demo.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer phone;
    private String address;
    private Boolean active;
    private Set<Role> roles;

}
