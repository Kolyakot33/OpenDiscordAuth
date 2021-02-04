package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static ru.fazziclay.opendiscordauth.Account.*;
import static ru.fazziclay.opendiscordauth.LoginManager.*;
import static ru.fazziclay.opendiscordauth.Main.*;
import static ru.fazziclay.opendiscordauth.Config.*;

// Класс дискорд бота.

public class DiscordBot extends ListenerAdapter {
    // Рыбка. При любом сообщении
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Переменныен для удобного использования.
        Message message = event.getMessage();           // Обьект сообщения
        MessageChannel channel = message.getChannel();  // Обьект канала сообщения
        String content = message.getContentRaw();       // (String) Содержимое сообщения
        User author = message.getAuthor();              // Обьект автора сообщения
        String author_name = author.getName()+"#"+author.getDiscriminator();    // (String) Имя автора сообщения NickName#0001

        // Если автор сообщения это текущий бот или если тип канала гильдия, а не личные сообщения то остановить выполнение кода.
        if (author.getId().equals(Main.bot.getSelfUser().getId()) || channel.getType().isGuild()) { return; }

        if (codes.containsKey(content)) { // Если такой код существует.
            Code code = codes.get(content);
            Player player = code.player;
            Account account = new Account(TYPE_NICKNAME, player.getName());

            if (code.type.equals(Code.TYPE_LOGIN_CODE)) {        // Если тип кода это код для входа.
                if (!author.getId().equals(account.discord)) {          // Если автор сообщения не является владельцем аккаунта.
                    sendMessage(channel, CONFIG_MESSAGE_CODE_USING_E1);     // Отправить сообщения об ошибке.
                    return;                                                 // Остановить выполнение кода далее.
                }
                login(player);                                          // Залогинить игрока
            }

            if (code.type.equals(Code.TYPE_REGISTRATION_CODE)) { // Если тип кода это код для регистрации
                Timer timer = new Timer();                              // Обьект таймера

                Map<String, Object> temp = new HashMap<>();             /// | Добавить временную переменную, в неё засунуть
                temp.put("channel", channel);                           /// | все нужные данные и добавить в 'временное хранилеще аккаунтов для регистрации'
                temp.put("user", author);                               /// |
                temp.put("timer", timer);                               /// |
                temp_accounts.put(player.getName(), temp);              /// |

                sendMessage(player, CONFIG_MESSAGE_REGISTER_WARN.replace("$discord", author_name)); // Отправить в майнкрафт сообщение о попытке привязать аккаунт.
                sendMessage(channel, CONFIG_MESSAGE_REGISTER_CHECK_GAME);                                  // Отправить в дискорд то что надо продолжить в игре.

                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       temp_accounts.remove(player.getName());
                                   }
                               },
                        60 * 1000L);
            }


            codes.remove(content); // Удалить код входа.

        } else {
            sendMessage(channel, CONFIG_MESSAGE_CODE_NOT_FOUND);            // Отправить сообщение о том что код не обраружен.
        }
    }

}