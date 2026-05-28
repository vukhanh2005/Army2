package mobiarmy.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public final class WebServerProcess {
    private Process process;

    public void start() {
        boolean enabled = Boolean.parseBoolean(ServerConfig.get("web.enabled", "MOBIARMY_WEB_ENABLED", "true"));
        if (!enabled || isRunning()) {
            return;
        }

        String phpCommand = ServerConfig.get("web.phpCommand", "MOBIARMY_WEB_PHP_COMMAND", "php");
        String host = ServerConfig.get("web.host", "MOBIARMY_WEB_HOST", "127.0.0.1");
        int port = ServerConfig.getInt("web.port", "MOBIARMY_WEB_PORT", 8080);
        String documentRoot = ServerConfig.get("web.documentRoot", "MOBIARMY_WEB_DOCUMENT_ROOT", "../MobiArmy2-Web");
        File root = new File(documentRoot);
        if (!root.isAbsolute()) {
            root = root.getAbsoluteFile();
        }
        if (!root.isDirectory()) {
            System.err.println("Cannot start web server, document root not found: " + root.getAbsolutePath());
            return;
        }

        ProcessBuilder builder = new ProcessBuilder(
                phpCommand,
                "-S",
                host + ":" + port,
                "-t",
                root.getAbsolutePath()
        );
        builder.redirectErrorStream(true);
        builder.environment().putIfAbsent("MOBIARMY_DB_HOST", ServerConfig.get("web.dbHost", "MOBIARMY_DB_HOST", "127.0.0.1"));
        builder.environment().putIfAbsent("MOBIARMY_DB_NAME", ServerConfig.get("web.dbName", "MOBIARMY_DB_NAME", "army"));
        builder.environment().putIfAbsent("MOBIARMY_DB_USER", ServerConfig.get("db.user", "MOBIARMY_DB_USER", "root"));
        builder.environment().putIfAbsent("MOBIARMY_DB_PASSWORD", ServerConfig.get("db.password", "MOBIARMY_DB_PASSWORD", ""));

        try {
            this.process = builder.start();
            System.out.println("Web forum started: http://" + host + ":" + port);
            startLogThread();
        } catch (IOException ex) {
            System.err.println("Cannot start web forum. Configure web.phpCommand to your php executable. " + ex.getMessage());
        }
    }

    public void stop() {
        if (this.process != null && isRunning()) {
            this.process.destroy();
            System.out.println("Web forum stopped.");
        }
        this.process = null;
    }

    private boolean isRunning() {
        if (this.process == null) {
            return false;
        }
        try {
            this.process.exitValue();
            return false;
        } catch (IllegalThreadStateException ex) {
            return true;
        }
    }

    private void startLogThread() {
        Thread thread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[web] " + line);
                }
            } catch (IOException ex) {
            }
        }, "mobiarmy-web-log");
        thread.setDaemon(true);
        thread.start();
    }
}

