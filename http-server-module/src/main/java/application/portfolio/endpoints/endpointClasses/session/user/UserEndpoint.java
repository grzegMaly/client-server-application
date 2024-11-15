package application.portfolio.endpoints.endpointClasses.session.user;

import application.portfolio.clientServer.ClientHolder;
import application.portfolio.clientServer.ServerHolder;
import application.portfolio.endpoints.EndpointHandler;
import application.portfolio.endpoints.EndpointInfo;
import application.portfolio.utils.AutoInvoke;
import application.portfolio.utils.Infrastructure;
import application.portfolio.utils.ResponseHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import static java.net.HttpURLConnection.*;

@EndpointInfo(path = "/user")
public class UserEndpoint implements EndpointHandler, HttpHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        EndpointInfo basePathInfo = UserEndpoint.class.getAnnotation(EndpointInfo.class);
        String basePath = basePathInfo.path();
        HttpServer server = ServerHolder.getServer();

        for (Method method : UserEndpoint.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AutoInvoke.class)) {
                try {
                    if (Modifier.isStatic(method.getModifiers())) {
                        Object[] args = new Object[]{server, basePath};
                        method.invoke(null, args);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public HttpHandler endpoint() {
        return this;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ResponseHandler.handleError(exchange, "Bad Request", HTTP_BAD_REQUEST);
    }

    @AutoInvoke
    private static void getUser(HttpServer server, String basePath) {

        String path = basePath + "/get";
        server.createContext(path, exchange -> {
            try (exchange) {
                if ("GET".equals(exchange.getRequestMethod())) {

                    Map<String, String> paramsMap = getParams(exchange.getRequestURI());
                    String params = paramsString(paramsMap);

                    if (params == null) {
                        ResponseHandler.handleError(exchange, "Forbidden", HTTP_FORBIDDEN);
                        return;
                    }

                    HttpClient client = ClientHolder.getClient();
                    Map<String, String> dbData = Infrastructure.getDatabaseData();
                    String spec = uriSpecificPart(dbData, "user", params);
                    URI baseUri = Infrastructure.getBaseUri(dbData).resolve(spec);

                    HttpRequest request = HttpRequest.newBuilder(baseUri)
                            .GET()
                            .timeout(Duration.ofSeconds(10))
                            .header("Accept", "application/json")
                            .build();

                    HttpResponse<byte[]> response;
                    try {
                        response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                        ResponseHandler.sendResponse(response, exchange);
                    } catch (Exception e) {
                        ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
                    }
                } else {
                    ResponseHandler.handleError(exchange, "Bad Gateway", HTTP_BAD_GATEWAY);
                }
                throw new IOException();
            } catch (IOException e) {
                ResponseHandler.handleError(exchange, "Unknown Error", HTTP_INTERNAL_ERROR);
            }
        });
    }

    private static Map<String, String> getParams(URI uri) {

        String params = uri.getQuery();
        Map<String, String> paramsMap;

        if (params == null) {
            return new LinkedHashMap<>(Map.of("limit", "10", "offset", "0"));
        }


        String[] splitParams = params.split("&");
        paramsMap = new LinkedHashMap<>();

        for (String s : splitParams) {
            String[] keyVal = s.split("=", 2);
            if (keyVal.length == 2) {
                paramsMap.put(keyVal[0], keyVal[1]);
            }
        }
        return paramsMap;
    }

    private static String paramsString(Map<String, String> map) {

        StringBuilder sb = new StringBuilder();
        StringJoiner sj = new StringJoiner("&", "?", "");
        if (map.containsKey("id")) {
            sb.append("id=").append(map.get("id"));
            sj.add(sb.toString());
            return sj.toString();
        }

        for (Map.Entry<String, String> m : map.entrySet()) {
            if (m.getKey().equals("limit") || m.getKey().equals("offset")) {
                sb.append(m.getKey())
                        .append("=")
                        .append(m.getValue());

                sj.add(sb.toString());
                sb.setLength(0);
            } else {
                return null;
            }
        }
        return sj.toString();
    }

    private static String uriSpecificPart(Map<String, String> data, String endpoint, String params) {

        String point = data.get(endpoint);
        if (point == null) {
            return "";
        }
        return point.concat(params);
    }
}
