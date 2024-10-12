package application.portfolio.clientmodule.HttpClient;

import java.net.http.HttpClient;

public class HttpClientAgent {

    private static final HttpClient INSTANCE = HttpClient.newHttpClient();

    private HttpClientAgent() {}

    public static HttpClient getInstance() {
        return INSTANCE;
    }
}