package mobiarmy.server;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public final class WebApiClient {
    public static final class RegisterResult {
        public boolean success;
        public String message;
    }

    private static final String API_BASE_URL = ServerConfig.get("web.apiBaseUrl", "MOBIARMY_WEB_API_BASE_URL", "");
    private static final String API_SECRET = ServerConfig.get("web.apiSecret", "MOBIARMY_WEB_API_SECRET", "NguyenVuKhanhEni");

    private WebApiClient() {
    }

    public static boolean isEnabled() {
        return API_BASE_URL != null && !API_BASE_URL.trim().isEmpty();
    }

    public static RegisterResult register(String username, String password) throws IOException {
        String endpoint = API_BASE_URL.replaceAll("/+$", "") + "/register.php";
        String body = "api_secret=" + encode(API_SECRET)
                + "&username=" + encode(username)
                + "&password=" + encode(password);

        HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        try (OutputStream output = connection.getOutputStream()) {
            output.write(body.getBytes("UTF-8"));
        }

        StringBuilder response = new StringBuilder();
        InputStream input = connection.getResponseCode() >= 400 ? connection.getErrorStream() : connection.getInputStream();
        if (input == null) {
            RegisterResult result = new RegisterResult();
            result.success = false;
            result.message = "Web API không phản hồi.";
            return result;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        Map<?, ?> parsed = new Gson().fromJson(response.toString(), Map.class);
        RegisterResult result = new RegisterResult();
        result.success = Boolean.TRUE.equals(parsed.get("success"));
        Object message = parsed.get("message");
        result.message = message == null ? "" : String.valueOf(message);
        return result;
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
}
