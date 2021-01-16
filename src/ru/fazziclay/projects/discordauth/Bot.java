package ru.fazziclay.projects.discordauth;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static ru.fazziclay.projects.discordauth.Main.confirmLogin;


public class Bot extends ListenerAdapter {
    // Рыбки (При сообщении)
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage(); // Сообщение
        String content = msg.getContentRaw(); // Содержимое сообщения
        MessageChannel channel = event.getChannel(); // Канал сообщения

        // Обьект аккаунта, получаеться путём дискорд айди
        Data data = new Data(msg.getAuthor().getId(), "discord");

        if (msg.getAuthor().isBot() || channel.getType().isGuild()) {
            return;
        }

        // - Если аккаунта нету && если в reg_codes есть код из сообщения
        if (!data.isExist() && Events.temp_reg_codes.containsKey(content)) {
            // Получение информации из temp_reg_codes
            Player player = (Player) Events.temp_reg_codes.get(content).get("player");
            String playerNick = (String) Events.temp_reg_codes.get(content).get("nick");
            String discordNick = msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator();
            String discordId = msg.getAuthor().getId();

            // Удаление кода из reg_codes
            Events.temp_reg_codes.remove(content);

            // Собирание, упакивка и добавление данных в temp_add_account что бы обеспечить работу управлению из чата майнкрафта
            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("a", channel);
            temp.put("b", player);
            temp.put("c", discordId);
            temp.put("d", discordNick);
            Events.temp_accounts.put(playerNick, temp);

            // Оповещение
            Main.discordSend(channel, Main.config.messages.confirm_register_in_game);
            Main.minecraftSend(player, Main.config.messages.register_confirm.replace("%DISCORD%", discordNick));

            // Таймер для последующего удаления задачи из temp_add_account
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Events.temp_accounts.remove(playerNick);
                }
            }, 60 * 1000L);


        } else if (data.isExist() && Events.temp_auth_codes.containsKey(content)) { // Если аккаунт есть && если в auth_codes есть код из сообщения
            // Получение информации из temp_auth_codes
            Player player = (Player) Events.temp_auth_codes.get(content).get("player");
            String playerNick = (String) Events.temp_auth_codes.get(content).get("nick");
            String discordNick = msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator();
            String discordId = msg.getAuthor().getId();


            Events.temp_auth_codes.remove(content); // удалить код из auth_codes

            Main.discordSend(channel, Main.config.messages.login_confirm);
            Main.minecraftSend(player, Main.config.messages.login_confirm);

            Events.ips.put(player.getName(), player.getAddress().getHostName());
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Events.ips.put(player.getName(), "none");
                }
            }, Main.config.ip_expired_delay * 1000L);

            Main.confirmLogin(player);

        } else {
            channel.sendMessage(Main.config.messages.code_not_found).queue();
        }

    }

}