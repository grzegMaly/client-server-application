package application.portfolio.token;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class AuthToken {

    private final UUID token;
    private final UUID userId;
    private LocalDateTime lastUpdated;

    public AuthToken(UUID userId) {
        this.userId = userId;
        token = UUID.randomUUID();
        lastUpdated = LocalDateTime.now();
    }

    public UUID getToken() {
        return token;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isValid() {
        return ChronoUnit.DAYS.between(lastUpdated, LocalDateTime.now()) < 30;
    }

    public void refreshToken() {
        this.lastUpdated = LocalDateTime.now();
    }
}
