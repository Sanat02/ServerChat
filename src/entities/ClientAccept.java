package entities;

import utilities.Colors;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ClientAccept {

    public static void run(Socket socket) {

        String username = UsersList.getSockets().get(socket);
        System.out.printf(Colors.GREEN_BOLD+"Client connected: %s%n",Colors.YELLOW+username);

        try (socket;
             Scanner reader = getReader(socket);
             PrintWriter writer = getWriter(socket)) {

            sendResponse("Yor username:" +username, writer);
            sendResponseToAll(String.format("user %s connected", username), socket);

            while (true) {
                String message = reader.nextLine().strip();
                System.out.printf(Colors.CYAN+"Got message: < %s > from %s %n", message,Colors.YELLOW+username);

                if (isQuitMsg(message)) {
                    break;
                } else if (isEmptyMsg(message)) {
                    sendResponse("You can't send an empty message ", writer);
                } else {
                    ArrayList<String> list = new ArrayList<>(Arrays.asList(message.split("\\s")));
                    String command = list.get(0).toLowerCase(Locale.ROOT);
                    String oldName = UsersList.getSockets().get(socket);

                    switch (command) {
                        case "name":
                            changeName(socket, list, writer, oldName);
                            break;
                        case "list":
                            sendResponse(UsersList.getListOfUsers(), writer);
                            break;
                        case "whisper":
                            if (list.size() >= 2) {
                                whisperUser(socket, list, writer);
                            } else {
                                sendResponse("Invalid whisper!Add user name and message!", writer);
                            }
                            break;
                        default:
                            String text = UsersList.getSockets().get(socket) + ": " + message;
                            if (UsersList.getSockets().size() != 1) {
                                sendResponseToAll(text, socket);
                            } else {
                                sendResponse("ONLY YOU IN THE CHAT!", writer);
                            }
                            break;
                    }
                }
            }

        } catch (NoSuchElementException ex) {
            System.out.println(Colors.RED+"Client dropped connection!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf(Colors.RED+"Client is closed: %s%n", Colors.YELLOW+username);
        try {
            sendResponseToAll(String.format("Client %s left chat",username),socket);
        } catch (IOException e) {
            System.out.println(Colors.RED+"Error!");
        }
        UsersList.getSockets().remove(socket);

    }

    private static PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream());
    }

    private static Scanner getReader(Socket socket) throws IOException {
        return new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"));
    }

    private static boolean isQuitMsg(String message) {
        return "bye".equalsIgnoreCase(message);
    }

    private static boolean isEmptyMsg(String message) {
        return message == null || message.isBlank();
    }

    private static void sendResponse(String response, PrintWriter writer) {
        System.out.println(Colors.GREEN+"You sent response successfully!");
        writer.println(response);
        writer.flush();

    }

    private static void sendResponseToAll(String text, Socket socket) throws IOException {
        Map<Socket, String> sockets = UsersList.getSockets();
        for (Map.Entry<Socket, String> entry : sockets.entrySet()) {
            Socket targetSocket = entry.getKey();
            if (!targetSocket.equals(socket)) {
                PrintWriter writer = getWriter(targetSocket);
                sendResponse(text, writer);

            }
        }
    }

    private static void changeName(Socket socket, ArrayList<String> list, PrintWriter writer, String oldName) throws IOException {
        String newName = list.get(1);
        boolean isChanged = UsersList.changeName(socket, newName);
        if (isChanged) {
            sendResponse("You are now <" + newName + ">", writer);
            String text = String.format("User <%s> was changed to <%s>", oldName, UsersList.getName(socket));
            sendResponseToAll(text, socket);
        } else {
            sendResponse("Print unique name! You did not change the name", writer);
        }
    }

    private static void whisperUser(Socket socket, ArrayList<String> list, PrintWriter writer) throws IOException {
        if (list.size() < 3) {
            sendResponse("You can't send an empty message ", writer);
            return;
        }

        String senderName = UsersList.getSockets().get(socket);
        String recipientName = list.get(1);
        String text = String.join(" ", list.subList(2, list.size()));

        Socket recipientSocket = UsersList.getKeyByValue(recipientName);
        sendResponse(recipientSocket != null ? senderName + ": " + text : "User not found: " + recipientName,
                recipientSocket != null ? getWriter(recipientSocket) : writer);
    }


}
