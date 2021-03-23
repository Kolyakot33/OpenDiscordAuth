package ru.fazziclay.opendiscordauth.objects;

import ru.fazziclay.opendiscordauth.cogs.Utils;

public class Session {
    public String nickname;
    public String ip;
    public long expiredAt;

    public Session(String nickname, String ip, long expiredAt) {
        Utils.debug("[Session] new Session object: nickname="+nickname+"; ip="+ip+"; expiredAt="+expiredAt+"");

        this.nickname = nickname;
        this.ip = ip;
        this.expiredAt = expiredAt;
    }

    public void delete() {
        Utils.debug("[Session] delete() in Session object: nickname="+nickname+"; ip="+ip+"; expiredAt="+expiredAt+"");

        nickname = "";
        ip = "";
        expiredAt = -1;
    }
}
