//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.*;
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
import ru.fazziclay.opendiscordauth.cogs.LoginManager;
import ru.fazziclay.opendiscordauth.objects.Account;
import ru.fazziclay.opendiscordauth.objects.Code;
import ru.fazziclay.opendiscordauth.objects.TempAccount;

import java.util.Timer;
import java.util.TimerTask;


import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_BUNGEECORD_ENABLE;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_GENERATOR_CODE_EXPIRED_TIME;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_GENERATOR_LOGIN_MAXIMUM;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_GENERATOR_LOGIN_MINIMUM;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_GENERATOR_REGISTER_MAXIMUM;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_GENERATOR_REGISTER_MINIMUM;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_IP_SAVING_TYPE;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_MESSAGE_CODE_GENERATOR_E1;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_MESSAGE_HELLO;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_MESSAGE_KICK_AUTH_TIMEOUT;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_MESSAGE_LOGIN_GIVE_CODE;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_MESSAGE_REGISTER_ADD_ROLE_MEMBER_NOT_FOUND;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_MESSAGE_REGISTER_CANCEL;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_MESSAGE_REGISTER_CONFIRM;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_MESSAGE_REGISTER_GIVE_CODE;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_REGISTER_ADD_ROLE_ENABLE;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_REGISTER_ADD_ROLE_GUILD;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_REGISTER_ADD_ROLE_OBLIGATORILY;
import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_REGISTER_ADD_ROLE_ROLE;

import static ru.fazziclay.opendiscordauth.cogs.LoginManager.addAccount;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.isLogin;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.login;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.getCode;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.saveSession;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.codes;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.ips;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.noLoginList;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.tempAccounts;

import static ru.fazziclay.opendiscordauth.cogs.Utils.getIp;
import static ru.fazziclay.opendiscordauth.cogs.Utils.kickPlayer;
import static ru.fazziclay.opendiscordauth.cogs.Utils.sendMessage;

import static ru.fazziclay.opendiscordauth.objects.Account.TYPE_NICKNAME;

import static ru.fazziclay.opendiscordauth.Main.bot;



public class Events implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Timer codeExpiredTimer = new Timer();


        Player player = event.getPlayer();
        String nickname = player.getName();
        String uuid = player.getUniqueId().toString();
        String ip = getIp(player);

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

        String give_code_message    = CONFIG_MESSAGE_REGISTER_GIVE_CODE;
        int code_type               = Code.TYPE_REGISTRATION_CODE;
        int code_generator_minimum  = CONFIG_GENERATOR_REGISTER_MINIMUM;
        int code_generator_maximum  = CONFIG_GENERATOR_REGISTER_MAXIMUM;
        String code = "(-1 error)";
        String finalCode = code;

        if (account.isExist) {
            give_code_message      = CONFIG_MESSAGE_LOGIN_GIVE_CODE;
            code_generator_minimum = CONFIG_GENERATOR_LOGIN_MINIMUM;
            code_generator_maximum = CONFIG_GENERATOR_LOGIN_MAXIMUM;
            code_type              = Code.TYPE_LOGIN_CODE;
        }

        code = getCode(code_generator_minimum, code_generator_maximum);
        if (code.equals("null")) {
            kickPlayer(player, CONFIG_MESSAGE_CODE_GENERATOR_E1);
            return;
        }

        codes.put(code, new Code(code, code_type, player));                  // Добавить код
        sendMessage(player, give_code_message.replace("$code", code)); // Отправить сообщение выдачи кода


        codeExpiredTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                codes.remove(finalCode);

                if (!LoginManager.isLogin(uuid)) { // Если игрок после истечения времени досихпор не залогинен то кикнуть его.
                    kickPlayer(player, CONFIG_MESSAGE_KICK_AUTH_TIMEOUT);
                }
            }
            }, CONFIG_GENERATOR_CODE_EXPIRED_TIME * 1000L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String nickname = event.getPlayer().getName();
        String ip = getIp(event.getPlayer());
        tempAccounts.remove(event.getPlayer().getName());

        if (CONFIG_BUNGEECORD_ENABLE) {
            //noinspection ConstantConditions
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
    public void onPlayerChat(AsyncPlayerChatEvent event){
        String message  = event.getMessage();
        Player player   = event.getPlayer();
        String nickname = player.getName();
        String uuid     = event.getPlayer().getUniqueId().toString();

        if (  tempAccounts.containsKey(nickname)    &&   (message.equalsIgnoreCase("confirm")    ||    message.equalsIgnoreCase("cancel"))  ) {
            TempAccount tempAccount = tempAccounts.get(nickname);

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
                        assert guild != null;
                        Member member = guild.getMember(tempAccount.user);
                        assert member != null;
                        assert role != null;
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


                addAccount(nickname, tempAccount.user.getId());
                sendMessage(tempAccount.messageChannel, CONFIG_MESSAGE_REGISTER_CONFIRM);
                sendMessage(player, CONFIG_MESSAGE_REGISTER_CONFIRM);
                login(player);
            } else {
                sendMessage(tempAccount.messageChannel, CONFIG_MESSAGE_REGISTER_CANCEL);
                kickPlayer(player, CONFIG_MESSAGE_REGISTER_CANCEL);
            }

            tempAccounts.remove(nickname);
            tempAccount.timer.cancel();
            event.setCancelled(true);
        }


        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    // Отмена событий если игрок не залогинен.

    // - Если плагин не отменяет какой то ивент, то вы
    // - можете мне написать и я добавлю нужный вам эвент.
    // - https://fazziclay.ru/

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){ // Отмена движения игрока
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) { // Отмена выбрасывания предметов игроком
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupItem(@SuppressWarnings("deprecation") PlayerPickupItemEvent event) { // Отмена ещё чего то
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) { // Отмена ломания блока игроком
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) { // Отмена установки блока игроком
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) { // Отмена набирания ведра жидкостью игроком
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) { // Отмена опустошения ведра игроком
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) { // Отмена использывания предметов игроком
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) { // Отмена выполнения команд игроком
        String uuid = event.getPlayer().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnHealthRegain(EntityRegainHealthEvent event) { // Отмена регенерации игрока
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        String uuid = event.getEntity().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void OnFoodLevelChange(FoodLevelChangeEvent event) { // Отмена голода игрока
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        String uuid = event.getEntity().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) { // Отмена урока игрока
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        String uuid = event.getEntity().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) { // Отмена клика по предмету в инвентаре игром
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        String uuid = event.getWhoClicked().getUniqueId().toString();
        if (!isLogin(uuid)) {
            event.setCancelled(true);
        }
    }

}
