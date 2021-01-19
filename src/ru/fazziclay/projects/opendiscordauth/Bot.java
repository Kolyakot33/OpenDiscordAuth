package ru.fazziclay.projects.opendiscordauth;

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

import static ru.fazziclay.projects.opendiscordauth.LoginManager.*;
import static ru.fazziclay.projects.opendiscordauth.Main.config;
import static ru.fazziclay.projects.opendiscordauth.Main.sendMessage;


public class Bot extends ListenerAdapter {
    String CONFIG_MESSAGE_REGISTER_WARN             = config.getString("message.REGISTER_WARN");
    String CONFIG_MESSAGE_REGISTER_CHECK_GAME       = config.getString("message.REGISTER_CHECK_GAME");
    String CONFIG_MESSAGE_CODE_NOT_FOUND            = config.getString("message.CODE_NOT_FOUND");


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Переменныен для удобного использования.
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel();
        String content = message.getContentRaw();
        User author = message.getAuthor();
        String author_name = author.getName()+"#"+author.getDiscriminator();

        // Если автор сообщения это текущий бот или если тип канала гильдия, а не личные сообщения то остановить выполнение кода.
        if (author.getId().equals(Main.bot.getSelfUser().getId()) || channel.getType().isGuild()) { return; }

        if (temp_register_codes.containsKey(content)) {                 // Если в сообщении код регистрации
            Player player = temp_register_codes.get(content);               // Получить обьект игрока из 'временного хранилеща кодов регистрации'

            Map<String, Object> temp = new HashMap<>();                     /// | Добавить временную переменную, в неё засунуть
            temp.put("channel", channel);                                   /// | все нужные данные и добавить в 'временное хранилеще аккаунтов для регистрации'
            temp.put("user", author);                                       /// |
            temp_accounts.put(player.getName(), temp);                      /// |

            sendMessage(player, CONFIG_MESSAGE_REGISTER_WARN.replace("$discord", author_name)); // Отправить в майнкрафт сообщение о попытке привязать аккаунт.
            sendMessage(channel, CONFIG_MESSAGE_REGISTER_CHECK_GAME);                                  // Отправить в дискорд то что надо продолжить в игре.

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                               @Override
                               public void run() {
                                   temp_accounts.remove(player.getName());
                               }
                           },
                    60 * 1000L);

            temp_register_codes.remove(content);

        } else if (temp_login_codes.containsKey(content)) {             // Иначе Если в сообщении код для входа
            Player player = temp_login_codes.get(content);                  // Получить обьект игрока из 'времененного хранилеща кодов входа'
            login(player);                                                  // Залогинить игрока

            temp_login_codes.remove(content);                               // Удалить код входа.

        } else {                                                        // Иначе
            sendMessage(channel, CONFIG_MESSAGE_CODE_NOT_FOUND);            // Отправить сообщение о том что код не обраружен.
        }
    }
}
