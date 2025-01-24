package application.portfolio.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FilesManager {

    private static final String PROPS = "/config/configFiles.properties";
    private static final Map<String, Map.Entry<Path, List<String>>> configPaths = new ConcurrentHashMap<>();
    private static final Map<String, Path> sourcePaths = new ConcurrentHashMap<>();

    static {
        loadConfigFiles();
    }

    private static void loadConfigFiles() {
        Map<String, Map<String, String>> configPathsMap = PropertiesLoader.getProperties(PROPS)
                .entrySet().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().toString().split("\\.")[0],
                        Collectors.toMap(
                                entry -> entry.getKey().toString().split("\\.")[1],
                                entry -> entry.getValue().toString()
                        )
                ));

        String currentPath = Path.of("").toAbsolutePath().toString();
        for (Map.Entry<String, Map<String, String>> entry : configPathsMap.entrySet()) {
            String file = entry.getKey();

            Map<String, String> subMap = entry.getValue();
            String dir = subMap.get("dir");
            String fileName = subMap.get("fileName");
            List<String> requiredKeys =
                    Arrays.asList(subMap.getOrDefault("requiredKeys", "").split(","));

            Path filePath = Paths.get(currentPath, dir, fileName);
            configPaths.put(file, Map.entry(filePath, requiredKeys));
        }
    }

    private static boolean validateFileExistence(Path resourcePath, List<String> info) {
        if (!Files.exists(resourcePath)) {
            info.add(resourcePath + " doesn't exist.");
            return false;
        }
        return true;
    }

    private static Properties loadProperties(Path resourcePath, List<String> info) {
        Properties properties = new Properties();
        try (InputStream is = Files.newInputStream(resourcePath)) {
            properties.load(is);
        } catch (IOException e) {
            info.add("Failed to load properties from " + resourcePath + ": " + e.getMessage());
            return null;
        }
        return properties;
    }

    private static void validateKeysAndPaths(Path resourcePath, Properties properties,
                                             Set<String> requiredKeys, List<String> info) {

        Set<Object> actualKeys = properties.keySet();
        actualKeys.retainAll(requiredKeys);
        if (actualKeys.size() < requiredKeys.size()) {
            List<Object> missingKeys = new ArrayList<>(requiredKeys);
            missingKeys.removeAll(actualKeys);
            info.add("Missing keys in file " + resourcePath + ": " + missingKeys);
        }

        for (Object key : actualKeys) {
            String val = properties.getProperty((String) key);
            if (val == null || val.isBlank()) {
                info.add("Key " + key + " in file " + resourcePath + " has an empty or null value.");
                continue;
            }

            String k = (String) key;
            if (k.endsWith("Path")) {
                validatePaths(resourcePath, k, val, info);
            }
        }
    }

    private static void validatePaths(Path resourcePath, String key, String value, List<String> info) {
        Path path = getPath(value);
        if (path == null || !Files.exists(path)) {
            info.add("Invalid path for key " + key + " in " + resourcePath + ": " + value);
        } else {
            sourcePaths.put(key, path);
        }
    }

    public static boolean validateConfigFiles() {

        List<String> info = new ArrayList<>();

        for (Map.Entry<String, Map.Entry<Path, List<String>>> entry : configPaths.entrySet()) {
            Path resourcePath = entry.getValue().getKey();

            if (!validateFileExistence(resourcePath, info)) {
                continue;
            }

            Properties properties = loadProperties(resourcePath, info);
            if (properties == null) {
                continue;
            }

            Set<String> requiredKeys = new HashSet<>(entry.getValue().getValue());
            validateKeysAndPaths(resourcePath, properties, requiredKeys, info);
        }

        if (!info.isEmpty()) {
            info.forEach(System.err::println);
            return false;
        }
        return true;
    }

    private static Path getPath(String sPath) {
        try {
            return Path.of(sPath);
        } catch (InvalidPathException e) {
            return null;
        }
    }

    public static Path  getUserResourcePath() {
        return sourcePaths.get("userResourcePath");
    }

    public static String getResourceDriveName() {
        return "Drive";
    }

    public static String getResourceThumbnailName() {
        return "Thumbnail";
    }
}
