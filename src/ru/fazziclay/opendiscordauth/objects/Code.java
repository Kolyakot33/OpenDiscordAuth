//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth.objects;

import org.bukkit.entity.Player;

import java.util.Timer;


public class Code {
    public static Integer TYPE_REGISTRATION_CODE    = 0;
    public static Integer TYPE_LOGIN_CODE           = 1;

    public String  code;
    public Integer type;
    public Player  player;
    public Timer   timer;

    public Code(String code, Integer type, Player player, Timer timer) {
        this.code   = code;
        this.type   = type;
        this.player = player;
        this.timer  = timer;
    }
}
