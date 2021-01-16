package ru.fazziclay.projects.discordauth;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static ru.fazziclay.projects.discordauth.Main.*;


public class Events implements Listener {
    public static Map<String, String> ips = new HashMap<String, String>();

    public static Map<String, Map> temp_accounts = new HashMap<String, Map>();
    public static Map<String, Map> temp_reg_codes = new HashMap<String, Map>();
    public static Map<String, Map> temp_auth_codes = new HashMap<String, Map>();


    @EventHandler
    public void onPlayerConnect(PlayerJoinEvent e){
        e.getPlayer().hidePlayer(Main.getPlugin(Main.class), e.getPlayer());
        e.setJoinMessage(null); // Удалить сообщение о входе игрока
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 50, false, false, false)); // Добавить эффект слепоты

        Main.minecraftSend(e.getPlayer(), config.messages.hello_message);

        // Переменные
        Player player = e.getPlayer();
        int auth_code = Utils.getRandom(config.codeGenerator.login.min, config.codeGenerator.login.max);
        int reg_code = Utils.getRandom(config.codeGenerator.register.min, config.codeGenerator.register.max);

        // Получение обьекта даты аккаунтов по нику из майнкрафта
        Data data = new Data(player.getName(), "name");

        // - Если аккаунта ещё нету
        if (!data.isExist()) {
            // Добавить в temp_reg_codes
            Map<String, Object> temp = new HashMap<String, Object>(); // Временный обьект для добавления в другой обьект
            temp.put("nick", player.getName()); // настройка временного обьекта
            temp.put("player", player); // настройка временного обьекта
            temp_reg_codes.put(""+reg_code, temp); // Добавление временного обьекта в index кодов

            Main.minecraftSend(player, Main.config.messages.start_register_message.replace("%CODE%", reg_code+""));

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    temp_reg_codes.remove(reg_code+"");
                }
            }, config.codeGenerator.register.expired_delay * 1000L);

        } else {

            // - Добавление ника в ips если его там нету
            if (!ips.containsKey(e.getPlayer().getName())) {
                ips.put(e.getPlayer().getName(), "none");
            }

            // - Если ip соответствет то сразу пустить игрока
            if (ips.get(e.getPlayer().getName()).equals( e.getPlayer().getAddress().getHostName())) {

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        confirmLogin(player);
                    }
                }, 900L);



            } else {

                Map<String, Object> temp = new HashMap<String, Object>(); // Временный обьект для добавления в другой обьект
                temp.put("nick", player.getName()); // настройка временного обьекта
                temp.put("player", player); // настройка временного обьекта
                temp_auth_codes.put("" + auth_code, temp); // Добавление временного обьекта в index кодов

                Main.minecraftSend(player, Main.config.messages.start_login_message.replace("%CODE%", "" + auth_code));

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        temp_auth_codes.remove("" + auth_code);
                    }
                }, config.codeGenerator.login.expired_delay * 1000L);
            }
        }
    }


    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent e){
        e.setQuitMessage(null); //Удалить сообщение о выходе игрока
        String nick = e.getPlayer().getName();

        temp_accounts.remove(nick);
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        e.setCancelled(true);
    }


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // Пременные
        String msg = event.getMessage();
        String nick = event.getPlayer().getName();

        // Settings
        String confirm_command = "confirm";
        String cancel_command = "cancel";

        // temp
        String m;

        if ((msg.equals(confirm_command) || msg.equals(cancel_command)) && temp_accounts.containsKey(nick)) {
            // - Парсин данных из temp_accounts
            Map temp_account_data = temp_accounts.get(nick);
            MessageChannel channel = (MessageChannel) temp_account_data.get("a");
            Player player = (Player) temp_account_data.get("b");
            String discordId = (String) temp_account_data.get("c");
            String discordNick = (String) temp_account_data.get("d");

            if (msg.equals(confirm_command)){
                Main.addAccount(nick, discordId);
                m = Main.config.messages.register_successfully.replace("%DISCORD%", discordNick);

                // Send messages
                Main.discordSend(channel, m);
                Main.minecraftSend(player, m);


                confirmLogin(player);

            } else {
                m = Main.config.messages.register_cancel.replace("%DISCORD%", discordNick);

                // Send messages
                Main.discordSend(channel, m);
                Main.minecraftSend(player, m);
            }



            temp_accounts.remove(nick);
        }

        event.setCancelled(true);
    }

}
