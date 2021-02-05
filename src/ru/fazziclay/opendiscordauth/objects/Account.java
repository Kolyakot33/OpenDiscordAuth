//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth.objects;



public class Account {
    public String id;
    public String discord;
    public String nickname;

    public Account(String id, String discord, String nickname) {
        this.id         = id;
        this.discord    = discord;
        this.nickname   = nickname;
    }
}
