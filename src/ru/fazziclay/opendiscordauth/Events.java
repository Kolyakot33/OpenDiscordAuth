//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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


import static ru.fazziclay.opendiscordauth.Account.*;
import static ru.fazziclay.opendiscordauth.LoginManager.*;
import static ru.fazziclay.opendiscordauth.Main.*;
import static ru.fazziclay.opendiscordauth.Config.*;



public class Events implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Переменные для удобного использования
        Player player = event.getPlayer();
        String nickname = player.getName();
        String uuid = player.getUniqueId().toString();
        String ip = player.getAddress().getHostName();

        if (player.hasPermission("opendiscordauth.auth.bypass")) {
            return;
        }

        Account account = new Account(TYPE_NICKNAME, nickname);              // Создать экземплёр аккаунта

        player.setAllowFlight(true);                                         // Рарзершить полёт для того если человек появится в воздухе.
        noLoginList.add(uuid);                                               // Добавить игрока в список не залогиненых.
        if (!ips.containsKey(nickname)) { ips.put(nickname, "none"); }       // Если в ips нету ключа с ником игрока то добавить его со значением none

        if (ips.get(nickname).equals(ip) && account.isExist) {                                  // Если ips[nickname] == текущий айпи && аккаунт данного игрока существует
            login(player);                                                                        // Залогинить игрока
            return;                                                                               // Остановить выполнение кода.
        }

        sendMessage(player, CONFIG_MESSAGE_HELLO);                           // Отправить приветственное сообщение

        // Генерация кодов
        String login_code     =  getCode(CONFIG_GENERATOR_LOGIN_MINIMUM   , CONFIG_GENERATOR_LOGIN_MAXIMUM   );  // Сгенерировать код входа
        String register_code  =  getCode(CONFIG_GENERATOR_REGISTER_MINIMUM, CONFIG_GENERATOR_REGISTER_MAXIMUM);  // Сгенерировать код регистрации

        // Логика
        if (account.isExist) {                                                                                    // Если аккаунт уже зарегистрирован.
            codes.put(login_code, new Code(login_code, Code.TYPE_LOGIN_CODE, player));                               // Добавить код в 'временное хранилеще кодов входа'
            sendMessage(player, CONFIG_MESSAGE_LOGIN_GIVE_CODE.replace("$code", login_code));       // Отправить сообщение выдачи кода CONFIG_MESSAGE_LOGIN_GIVE_CODE
        }
        else {                                                                                                    // Иначе
            codes.put(register_code, new Code(register_code, Code.TYPE_REGISTRATION_CODE, player));                  // Добавить код в 'Временное хранилеще кодов регистрации'
            sendMessage(player, CONFIG_MESSAGE_REGISTER_GIVE_CODE.replace("$code", register_code));            // Отправить сообщение выдачи кода CONFIG_MESSAGE_REGISTER_GIVE_CODE
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               codes.remove(login_code);

                               if (!LoginManager.isLogin(uuid)) { // Если игрок после истечения времени досихпор не залогинен то кикнуть его.
                                   kickPlayer(player, CONFIG_MESSAGE_KICK_AUTH_TIMEOUT);
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
            Timer           timer   = (Timer)          (temp_accounts.get(nickname).get("timer"));

            if (message.equalsIgnoreCase("confirm")) {
                if (CONFIG_REGISTER_ADD_ROLE_ENABLE) {
                    Guild guild = null;
                    Role role = null;
                    try {
                        guild = bot.getGuildById(CONFIG_REGISTER_ADD_ROLE_GUILD);
                        role = bot.getRoleById(CONFIG_REGISTER_ADD_ROLE_ROLE);

                    } catch (Exception e) {
                        Bukkit.getLogger().info("## ");
                        Bukkit.getLogger().info("## §c[ERROR] " + e.toString());
                        Bukkit.getLogger().info("## ");
                    }

                    try {
                        Member member = guild.getMember(user);
                        guild.addRoleToMember(member, role).queue();


                    } catch (IllegalArgumentException e) {
                        if (CONFIG_REGISTER_ADD_ROLE_OBLIGATORILY) {
                            kickPlayer(player, CONFIG_MESSAGE_REGISTER_ADD_ROLE_MEMBER_NOT_FOUND);
                            event.setCancelled(true);
                            return;
                        }

                    } catch (Exception e) {
                        if (CONFIG_REGISTER_ADD_ROLE_OBLIGATORILY) {
                            sendMessage(player, "&cERROR: "+e.toString());
                            event.setCancelled(true);
                            return;
                        }
                    }
                }


                addAccount(nickname, user.getId());
                sendMessage(channel, CONFIG_MESSAGE_REGISTER_CONFIRM);
                sendMessage(player, CONFIG_MESSAGE_REGISTER_CONFIRM);
                login(player);
            } else {
                sendMessage(channel, CONFIG_MESSAGE_REGISTER_CANCEL);
                kickPlayer(player, CONFIG_MESSAGE_REGISTER_CANCEL);
            }

            temp_accounts.remove(nickname);
            timer.cancel();
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
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
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

        noLoginList.remove(uuid);
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
