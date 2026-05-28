package mobiarmy.server;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
public final class ServerConfig {
    private static final Properties PROPERTIES = loadProperties();
    private ServerConfig() {
    }
    public static String get(String propertyName, String envName, String defaultValue) {
        String systemValue = System.getProperty(propertyName);
        if (hasText(systemValue)) {
            return systemValue;
        }
        String envValue = System.getenv(envName);
        if (hasText(envValue)) {
            return envValue;
        }
        String fileValue = PROPERTIES.getProperty(propertyName);
        if (hasText(fileValue)) {
            return fileValue.trim();
        }
        return defaultValue;
    }
    public static int getInt(String propertyName, String envName, int defaultValue) {
        String value = get(propertyName, envName, Integer.toString(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            System.err.println("Invalid integer config " + propertyName + "=" + value + ", using " + defaultValue);
            return defaultValue;
        }
    }
    private static Properties loadProperties() {
        Properties properties = new Properties();
        Path path = Path.of("server.properties");
        if (!Files.isRegularFile(path)) {
            return properties;
        }
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
        } catch (IOException ex) {
            System.err.println("Cannot read server.properties: " + ex.getMessage());
        }
        return properties;
    }
    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}