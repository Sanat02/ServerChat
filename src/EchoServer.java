import entities.ClientAccept;
import entities.UsersList;
import utilities.Colors;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private EchoServer(int port) {
        this.port = port;

    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        System.out.println(Colors.BLUE+"Connecting..........");
        try (ServerSocket server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                Socket clientSocket = server.accept();
                new UsersList();
                UsersList.addUser(clientSocket);
                pool.submit(() -> ClientAccept.run(clientSocket));
            }
        } catch (IOException e) {
            System.out.printf("Probably the port %s is busy!", port);
            e.printStackTrace();
        }

    }

}
