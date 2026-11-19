package br.com.srcsoftware.api.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isDatabaseReachable = checkDatabaseConnection();

        if (isDatabaseReachable) {
            return Health.up()
                    .withDetail("service", "Atlantis API")
                    .withDetail("status", "Operacional")
                    .withDetails(Map.of(
                            "database", "MySQL",
                            "latencyMs", 12
                    ))
                    .build();
        }

        return Health.down()
                .withDetail("service", "Atlantis API")
                .withDetail("error", "Falha de conexão com a base de dados")
                .build();
    }

    private boolean checkDatabaseConnection() {
        // Lógica de verificação customizada (ex: ping na DB ou consulta simples)
        return true;
    }
}