package com.example.demo;

import com.example.demo.Entity.Role;
import com.example.demo.Entity.User;
import com.example.demo.Repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableFeignClients
@RequiredArgsConstructor
public class DemoApplication {
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner run() {
		return args -> {
			Set<Role> roles = new HashSet<>();
			roles.add(Role.ADMIN); // default role is ADMIN

			User user = User.builder()
					.firstName("admin")
					.lastName("admin")
					.email("admin@gmail.com")
					.phone(null)
					.active(true)
					.address(null)
					.password(passwordEncoder.encode("admin"))
					.roles(roles)
					.build();

			userRepo.save(user);
		};
	}
}
