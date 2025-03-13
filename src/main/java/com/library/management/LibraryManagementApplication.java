package com.library.management;

import com.library.management.entity.User;
import com.library.management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class LibraryManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Create a automatic admin user
			if (!userRepository.existsByUsername("admin")) {
				User admin = User.builder()
						.username("admin")
						.email("admin1@example.com")
						.password(passwordEncoder.encode("admin123"))
						.role(User.Role.ADMIN)
						.build();
				userRepository.save(admin);
				System.out.println("Admin user created successfully!");
			}
		};
	}
}
