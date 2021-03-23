package ru.fazziclay.opendiscordauth;

import ru.fazziclay.opendiscordauth.cogs.SessionManager;
import ru.fazziclay.opendiscordauth.objects.Session;

import java.util.ArrayList;
import java.util.Scanner;


public class DebugMain {
    public static void main(String[] args) {
        System.out.println("Hello!");


        while (true) {
            String command = input("[DebugMain] -> ");

            if (command.equals("exit")) {
                break;

            } else if (command.equals("sessions")) {
                print(SessionManager.sessions);

                ArrayList<String> a = new ArrayList<>();

                int i = 0;
                while (i < SessionManager.sessions.size()) {
                    Session session = SessionManager.sessions.get(i);

                    String b = "(Nickname="+session.nickname+"; Ip="+session.ip+"; ExpiredTime="+session.expiredAt+")";
                    a.add(b);

                    i++;
                }
                print(a);

            } else if (command.equals("updateSession")) {
                String nickname = input("nick-> ");
                String ip = input("ip-> ");
                long expiredAt = Long.parseLong(input("expiredAt(INTEGER!!!)-> "));

                SessionManager.updateSession(nickname, ip, expiredAt);

            } else {
                print("Command not found.");
            }
        }
    }

    public static void print(Object message) {
        System.out.println(message);
    }

    public static String input(String message) {
        System.out.print(message);
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

}
