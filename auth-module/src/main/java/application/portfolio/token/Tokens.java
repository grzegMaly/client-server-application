package application.portfolio.token;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Tokens {

    private static final Map<UUID, AuthToken> tokens = new HashMap<>();

    public static AuthToken createToken(UUID personId) {

        if (tokens.containsKey(personId)) {
            AuthToken token = tokens.get(personId);
            if (token.isValid()) {
                token.refreshToken();
                return token;
            }
        }

        AuthToken token = new AuthToken(personId);
        tokens.put(personId, token);
        return token;
    }

    public static void deleteToken(UUID personId) {
        tokens.remove(personId);
    }

    public static AuthToken getToken(UUID personId) {
        return tokens.get(personId);
    }
}
