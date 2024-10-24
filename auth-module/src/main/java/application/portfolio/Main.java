package application.portfolio;

import application.portfolio.clientServer.ServerHolder;
import application.portfolio.utils.Infrastructure;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        if (!setServer()) {
            throw new RuntimeException("Error loading server");
        }

        ServerHolder.start();
    }

    private static boolean setServer() {

        Map<String, String> data = Infrastructure.getCurrentServerData();
        String host;
        int port;
        if (data.size() == 2) {
            host = Infrastructure.getHost(data);
            port = Integer.parseInt(Infrastructure.getPort(data));
            ServerHolder.bind(host, port, 0);
            return true;
        } else {
            throw new RuntimeException("Error loading server data");
        }
    }
}