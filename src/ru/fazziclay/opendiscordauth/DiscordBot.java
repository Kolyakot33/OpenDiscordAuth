//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;
import ru.fazziclay.opendiscordauth.objects.Account;
import ru.fazziclay.opendiscordauth.objects.Code;
import ru.fazziclay.opendiscordauth.objects.TempAccount;

import java.util.Timer;
import java.util.TimerTask;

import static ru.fazziclay.opendiscordauth.cogs.LoginManager.*;
import static ru.fazziclay.opendiscordauth.cogs.Utils.*;
import static ru.fazziclay.opendiscordauth.cogs.Config.*;
import static ru.fazziclay.opendiscordauth.cogs.AccountManager.*;



public class DiscordBot extends ListenerAdapter {

    @Override // Рыбка - При любом сообщении
    public void onMessageReceived(MessageReceivedEvent event) {
        Message         message      = event.getMessage();                                  // Обьект сообщения
        MessageChannel  channel      = message.getChannel();                                // Обьект канала сообщения
        String          content      = message.getContentRaw();                             // Содержимое сообщения (String)
        User            author       = message.getAuthor();                                 // Обьект автора сообщения
        String          author_name  = author.getName()+"#"+author.getDiscriminator();      // Имя автора сообщения NickName#0001 (String)


        if (author.getId().equals(Main.bot.getSelfUser().getId()) || channel.getType().isGuild()) { // Если автор сообщения == SelfBot или канал сообщения это канал в гильдии
            return;
        }

        if (tempCodes.containsKey(content)) { // Если такой код существует.
            Code    code    = tempCodes.get(content);                                    // Код
            Player  player  = code.player;                                               // Игрок
            Account account = getAccountByValue(SEARCH_TYPE_NICKNAME, player.getName()); // Аккаунт

            if (code.type.equals(Code.TYPE_LOGIN_CODE)) {                           // Если тип кода это код для входа.
                if (!author.getId().equals(account.discord)) {                          // Если автор сообщения не является владельцем аккаунта.
                    sendMessage(channel, CONFIG_MESSAGE_CODE_USING_E1);                     // Отправить сообщения об ошибке.
                    return;                                                                 // Остановить выполнение кода далее.
                }
                login(player);                                                          // Залогинить игрока
            }

            if (code.type.equals(Code.TYPE_REGISTRATION_CODE)) {                    // Если тип кода это код для регистрации
                Timer tempAccountExpiredTimer = new Timer();                                                    // Обьект таймера
                tempAccounts.put(player.getName(), new TempAccount(channel, author, tempAccountExpiredTimer));  // Добавить временный аккаунт

                sendMessage(player, CONFIG_MESSAGE_REGISTER_WARN.replace("$discord", author_name));      // Отправить в майнкрафт сообщение о попытке привязать аккаунт.
                sendMessage(channel, CONFIG_MESSAGE_REGISTER_CHECK_GAME);                                       // Отправить в дискорд то что надо продолжить в игре.

                tempAccountExpiredTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        tempAccounts.remove(player.getName());
                    }
                }, 60 * 1000L);
            }

            code.timer.cancel();       // Отменить таймер на удаление кода.
            tempCodes.remove(content); // Удалить код входа.

        } else {
            sendMessage(channel, CONFIG_MESSAGE_CODE_NOT_FOUND);            // Отправить сообщение о том что код не обраружен.
        }
    }

}