package ru.fazziclay.opendiscordauth.cogs;

import java.util.ArrayList;
import java.util.List;



public class AuthorizationManager {
    public static List<String> notAuthorizedPlayers = new ArrayList<>();

    public static void login(String nickname) {
        AuthorizationManager.notAuthorizedPlayers.remove(nickname);
    }

    public static void logout(String nickname, String sessionIp) {
        if (!AuthorizationManager.notAuthorizedPlayers.contains(nickname)) {
            AuthorizationManager.notAuthorizedPlayers.add(nickname);
        }
    }
}
