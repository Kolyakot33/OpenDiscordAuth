//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth.objects;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import java.util.Timer;



public class TempAccount {
    public MessageChannel messageChannel;
    public User           user;
    public Timer          timer;

    public TempAccount(MessageChannel messageChannel, User user, Timer timer) {
        this.messageChannel = messageChannel;
        this.user           = user;
        this.timer          = timer;
    }
}
