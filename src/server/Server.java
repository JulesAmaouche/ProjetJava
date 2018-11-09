package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static String serverIp = "127.0.0.1";
    private static int serverPort = 8000;
    private static int backlog = 50;
    private static ServerVariables variables = new ServerVariables();
    
    public static void main(String arg[]) throws IOException {      

        ServerSocket s = new ServerSocket(Server.serverPort, Server.backlog, InetAddress.getByName(Server.serverIp));
        while(true) {
            Socket socket = s.accept();
            ClientHandler c = new ClientHandler(socket, variables);
            new Thread(c).start();
            System.out.println("Nouveau client inconnu connecté");
        }

    }
}
