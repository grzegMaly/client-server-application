package application.portfolio.endpoints;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.util.Objects;

public interface EndpointHandler {

    default void registerEndpoint(HttpServer httpServer) {

        HttpHandler endpointClass = endpoint();
        EndpointInfo info = endpointClass.getClass().getAnnotation(EndpointInfo.class);

        if (Objects.nonNull(info)) {
            String path = info.path();
            httpServer.createContext(path, endpointClass);
        } else {
            throw new RuntimeException("Endpoint doesn't exist");
        }
    }

    HttpHandler endpoint();
}
