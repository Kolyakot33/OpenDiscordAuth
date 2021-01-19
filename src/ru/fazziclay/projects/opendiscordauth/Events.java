package ru.fazziclay.projects.opendiscordauth;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.Timer;
import java.util.TimerTask;


import static ru.fazziclay.projects.opendiscordauth.Account.*;
import static ru.fazziclay.projects.opendiscordauth.LoginManager.*;
import static ru.fazziclay.projects.opendiscordauth.Main.*;



public class Events implements Listener {
    int CONFIG_GENERATOR_LOGIN_MINIMUM          = config.getInt("generator.login_minimum");
    int CONFIG_GENERATOR_LOGIN_MAXIMUM          = config.getInt("generator.login_maximum");
    int CONFIG_GENERATOR_REGISTER_MINIMUM       = config.getInt("generator.register_minimum");
    int CONFIG_GENERATOR_REGISTER_MAXIMUM       = config.getInt("generator.register_maximum");
    int CONFIG_GENERATOR_CODE_EXPIRED_TIME      = config.getInt("generator.code_expired_time");
    String CONFIG_MESSAGE_HELLO                 = config.getString("message.hello");
    String CONFIG_MESSAGE_LOGIN_GIVE_CODE       = config.getString("message.LOGIN_GIVE_CODE");
    String CONFIG_MESSAGE_REGISTER_GIVE_CODE    = config.getString("message.REGISTER_GIVE_CODE");
    String CONFIG_MESSAGE_REGISTER_CONFIRM      = config.getString("message.REGISTER_CONFIRM");
    String CONFIG_MESSAGE_REGISTER_CANCEL       = config.getString("message.REGISTER_CANCEL");
    String CONFIG_MESSAGE_KICK_AUTH_TIMEOUT     = config.getString("message.KICK_AUTH_TIMEOUT");
    static int CONFIG_IP_SAVING_TYPE            = config.getInt("ip_saving_type");
    boolean CONFIG_SEND_IP_LOGIN_WARN           = true;
    String CONFIG_MESSAGE_IP_LOGIN_WARN         = "Вход на ваш аккаунт с айпи $ip.";//config.getString("message.KICK_AUTH_TIMEOUT");


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Переменные для удобного использования
        Player player = event.getPlayer();
        String nickname = player.getName();
        String uuid = player.getUniqueId().toString();
        String ip = player.getAddress().getHostName();

        player.setAllowFlight(true);                                         // Рарзершить полёт для того если человек появится в воздухе.
        addNoLogin(uuid);
        if (!ips.containsKey(nickname)) { ips.put(nickname, "none"); }       // Если в ips нету ключа с ником игрока то добавить его со значением none

        if (ips.get(nickname).equals(ip)) {                                  // Если ips[nickname] == текущий айпи
            login(player);                                                        // Залогинить игрока
            if (CONFIG_SEND_IP_LOGIN_WARN) {
                Account account = new Account(TYPE_NICKNAME, nickname);
                User user = bot.getUserById(account.getDiscord());
                try {
                    user.openPrivateChannel().queue((channel) -> {
                        sendMessage(channel, CONFIG_MESSAGE_IP_LOGIN_WARN.replace("$ip", ip));
                    });
                } catch (Exception ignored) {}
            }
            return;                                                               // Остановить выполнение дальнейшего кода
        }


        Account account = new Account(TYPE_NICKNAME, nickname);              // Создать экземплёр аккаунта
        sendMessage(player, CONFIG_MESSAGE_HELLO);                           // Отправить приветственное сообщение

        // Генерация кодов
        int login_code     =  getRandom(CONFIG_GENERATOR_LOGIN_MINIMUM   , CONFIG_GENERATOR_LOGIN_MAXIMUM   );  // Сгенерировать код входа
        int register_code  =  getRandom(CONFIG_GENERATOR_REGISTER_MINIMUM, CONFIG_GENERATOR_REGISTER_MAXIMUM);  // Сгенерировать код регистрации

        // Логика
        if (account.isExist()) {                                                                                    // Если аккаунт уже зарегистрирован.
            temp_login_codes.put(login_code+"", player);                                                                // Добавить код в 'временное хранилеще кодов входа'
            sendMessage(player, CONFIG_MESSAGE_LOGIN_GIVE_CODE.replace("$code", login_code+""));       // Отправить сообщение выдачи кода CONFIG_MESSAGE_LOGIN_GIVE_CODE
        }
        else {                                                                                                      // Иначе
            temp_register_codes.put(register_code+"", player);                                                          // Добавить код в 'Временное хранилеще кодов регистрации'
            sendMessage(player, CONFIG_MESSAGE_REGISTER_GIVE_CODE.replace("$code", register_code+"")); //
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               if (account.isExist()) {
                                   temp_login_codes.remove(login_code+"");
                               } else {
                                   temp_register_codes.remove(register_code+"");
                               }

                               if (!LoginManager.isLogin(uuid)) { // Если игрок после истечения времени досихпор не залогинен то кикнуть его.
                                   Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                                       player.kickPlayer(CONFIG_MESSAGE_KICK_AUTH_TIMEOUT);
                                   });
                               }
                           }
                       },
                CONFIG_GENERATOR_CODE_EXPIRED_TIME * 1000L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        String message = event.getMessage();
        Player player = event.getPlayer();
        String nickname = player.getName();
        String uuid = event.getPlayer().getUniqueId().toString();

        if (  temp_accounts.containsKey(nickname)    &&   (message.equalsIgnoreCase("confirm")    ||    message.equalsIgnoreCase("cancel"))  ) {
            MessageChannel  channel = (MessageChannel) (temp_accounts.get(nickname).get("channel"));
            User            user    = (User)           (temp_accounts.get(nickname).get("user"));

            if (message.equalsIgnoreCase("confirm")) {
                addAccount(nickname, user.getId());
                sendMessage(channel, CONFIG_MESSAGE_REGISTER_CONFIRM);
                sendMessage(player, CONFIG_MESSAGE_REGISTER_CONFIRM);
                login(player);
            } else {
                sendMessage(channel, CONFIG_MESSAGE_REGISTER_CANCEL);
                sendMessage(player, CONFIG_MESSAGE_REGISTER_CANCEL);
                Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
                    player.kickPlayer(CONFIG_MESSAGE_REGISTER_CANCEL);
                });
            }

            temp_accounts.remove(nickname);
            event.setCancelled(true);
        }


        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String nickname = event.getPlayer().getName();
        String ip = event.getPlayer().getAddress().getHostName();
        temp_accounts.remove(event.getPlayer().getName());

        if (CONFIG_BUNGEECORD_ENABLE) {
            event.setQuitMessage(null);
        }

        if (isLogin(uuid)) {
            if (CONFIG_IP_SAVING_TYPE == 0) {
                saveSession(nickname, ip);
            }
        }

        removeNoLogin(uuid);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnHealthRegain(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        String uuid = event.getEntity().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        String uuid = event.getEntity().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        String uuid = event.getEntity().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        String uuid = event.getWhoClicked().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

}
