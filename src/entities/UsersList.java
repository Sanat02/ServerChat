package entities;

import java.net.Socket;
import java.util.*;

import static utilities.Rnd.rnd;

public class UsersList {
    private static List<Integer> ints = new ArrayList<>();
    private static List<String> listOfNames;
    private final static Map<Socket, String> users = new HashMap<>();

    public UsersList() {
        listOfNames = List.of("Sanat", "Seide", "Uluk", "Nora", "Groy", "Mendy", "Arya", "Sansa", "Dany", "Greogory");
    }


    public static void addUser(Socket socket) {
        int index = rnd(listOfNames.size());
        if (ints.isEmpty()) {
            ints.add(index);
        } else {
            while (ints.contains(index)) {
                index = rnd(listOfNames.size());
            }
            ints.add(index);
        }
        users.put(socket, listOfNames.get(index).toLowerCase(Locale.ROOT));
    }

    public static String getName(Socket socket) {
        return users.get(socket);
    }

    public static boolean changeName(Socket socket, String name) {
        name = name.toLowerCase(Locale.ROOT);
        if (users.containsValue(name)) {
            return false;
        }

        users.put(socket, name);
        return true;
    }

    public static Map<Socket, String> getSockets() {
        return users;
    }

    public static String getListOfUsers() {
        StringBuilder list = new StringBuilder("Users in the chat: ");
        for (Map.Entry<Socket, String> mp : users.entrySet()) {
            list.append(mp.getValue()).append(" ");
        }
        return list.toString();
    }

    public static Socket getKeyByValue( String value) {
        for (Map.Entry<Socket, String> entry : users.entrySet()) {
            if (Objects.equals(value.toLowerCase(Locale.ROOT), entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }


}
