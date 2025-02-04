package application.portfolio.clientServer.webSocket;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WebSocketResourceInfo {
    String currentResource();
}
