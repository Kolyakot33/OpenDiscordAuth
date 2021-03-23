package ru.fazziclay.opendiscordauth.cogs;

import ru.fazziclay.opendiscordauth.objects.Session;

import java.util.ArrayList;
import java.util.List;



public class SessionManager {
    public static List<Session> sessions = new ArrayList<>();

    public static void updateSession(String nickname, String ip, long expiredAt) {
        Utils.debug("[SessionManager] updateSession(nickname="+nickname+", ip="+ip+", expiredAt(long)="+expiredAt+")");

        Session session = getSessionByValue(0, nickname);

        if (session != null) {
            Utils.debug("[SessionManager] updateSession(nickname="+nickname+", ip="+ip+", expiredAt(long)="+expiredAt+"): session object != null");

            session.nickname = nickname;
            session.ip = ip;
            session.expiredAt = expiredAt;
        } else {
            Utils.debug("[SessionManager] updateSession(nickname="+nickname+", ip="+ip+", expiredAt(long)="+expiredAt+"): session object == null");

            Session newSession = new Session(nickname, ip, expiredAt);
            sessions.add(newSession);
        }
    }




    public static boolean isSessionExist(int searchType, Object searchValue) {
        return (getSessionByValue(searchType, searchValue) != null);
    }

    public static boolean isSessionActive(String nickname, String ip) {
        return false;
    }


    public static void deleteAllOverdue() {
        int i = 0;
        while (i < sessions.size()) {
            Session session = sessions.get(i);
            if (session.expiredAt < Utils.getCurrentTime()) {
                sessions.remove(i);
            }

            i++;
        }
    }


    public static Session getSessionByValue(int searchType, Object searchValue) {
        Utils.debug("[SessionManager] getSessionByValue(searchType(int)="+searchType+", searchValue(Object)="+searchValue+")");

        // 0 - nickname; 1 - ip; 2 - expiredAt

        int i = 0;
        while (i < sessions.size()) {
            Session session = sessions.get(i);
            if (searchType == 0 && session.nickname.equals(searchValue)) {
                return session;

            } else if (searchType == 1 && session.ip.equals(searchValue)) {
                return session;

            } else if (searchType == 2 && session.expiredAt == (long) searchValue) {
                return session;
            }

            i++;
        }

        Utils.debug("[SessionManager] getSessionByValue(searchType(int)="+searchType+", searchValue(Object)="+searchValue+"): return null;");
        return null;
    }
}
