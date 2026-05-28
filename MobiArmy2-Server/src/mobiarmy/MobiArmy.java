package mobiarmy;
import mobiarmy.server.Server;
import mobiarmy.server.ServerConfig;
public class MobiArmy {
    public static void main(String[] args) throws InterruptedException {
        int port = ServerConfig.getInt("server.port", "MOBIARMY_PORT", 8122);
        Server server = new Server(port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping MobiArmy server...");
            server.stop();
        }, "mobiarmy-shutdown"));
        System.out.println("Starting MobiArmy server in headless mode.");
        System.out.println("Port: " + port);
        server.start();
    }
}