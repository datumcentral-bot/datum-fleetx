package com.datum.fleetx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Datum FleetX - Enterprise Truck Dispatch SaaS Platform
 * Main Application Entry Point
 */
@SpringBootApplication
@EnableJpaAuditing
public class FleetXApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FleetXApplication.class, args);
    }
}
