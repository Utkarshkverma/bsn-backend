package com.vermau2k01.bsn;

import com.vermau2k01.bsn.user.RoleRepository;
import com.vermau2k01.bsn.user.Roles;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class BsnApplication {

    public static void main(String[] args) {

        SpringApplication.run(BsnApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByRole("USER").isEmpty()) {
                roleRepository.save(Roles.builder().role("USER").build());
            }
        };
    }

}
