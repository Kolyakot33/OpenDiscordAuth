//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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
import ru.fazziclay.opendiscordauth.objects.TempAccount;

import static ru.fazziclay.opendiscordauth.Main.bot;
import static ru.fazziclay.opendiscordauth.cogs.AccountManager.addAccount;
import static ru.fazziclay.opendiscordauth.cogs.Config.*;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.*;
import static ru.fazziclay.opendiscordauth.cogs.Utils.*;


public class Events implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();

        if (!noLoginList.contains(uuid)) {
            noLoginList.add(uuid); // Добавить игрока в список не залогиненых.
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("opendiscordauth.auth.bypass")) {
            return;
        }

        logout(player, false);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        String nickname = event.getPlayer().getName();
        String ip = getIp(event.getPlayer());

        tempAccounts.remove(event.getPlayer().getName());           // Удалить временный аккаунт
        tempCodes.remove(nicknameACodeAssociation.get(nickname));   // Удалить код
        nicknameACodeAssociation.remove(nickname);                  // Удалить ассоциацию кода с никнеймом

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

        if ( tempAccounts.containsKey(nickname)   &&  (message.equalsIgnoreCase(CONFIG_COMMAND_REGISTER_CONFIRM)   ||   message.equalsIgnoreCase(CONFIG_COMMAND_REGISTER_CANCEL)) ) {
            TempAccount tempAccount = tempAccounts.get(nickname);

            if (message.equalsIgnoreCase(CONFIG_COMMAND_REGISTER_CONFIRM)) {
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
    // - можете мне написать и я добавлю нужный вам ивент.
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
