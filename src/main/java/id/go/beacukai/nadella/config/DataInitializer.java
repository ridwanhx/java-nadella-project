package id.go.beacukai.nadella.config;

import id.go.beacukai.nadella.entity.User;
import id.go.beacukai.nadella.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing default users...");

            // Create Admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator Nadella");
            admin.setEmail("admin@nadella.beacukai.go.id");
            admin.setRole(User.Role.Admin);
            admin.setEnabled(true);
            userRepository.save(admin);
            log.info("Created admin user: username=admin, password=admin123");

            // Create Petugas
            User petugas = new User();
            petugas.setUsername("petugas");
            petugas.setPassword(passwordEncoder.encode("petugas123"));
            petugas.setFullName("Petugas Bea Cukai");
            petugas.setEmail("petugas@nadella.beacukai.go.id");
            petugas.setRole(User.Role.Petugas);
            petugas.setEnabled(true);
            userRepository.save(petugas);
            log.info("Created petugas user: username=petugas, password=petugas123");
        }
    }
}
