package ru.fazziclay.opendiscordauth;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.fazziclay.opendiscordauth.cogs.AuthorizationManager;
import ru.fazziclay.opendiscordauth.cogs.Config;
import ru.fazziclay.opendiscordauth.cogs.SessionManager;
import ru.fazziclay.opendiscordauth.cogs.Utils;


public class ServerEvents implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String nickname = player.getName();
        String ip = Utils.getIp(player);

        Utils.debug("[ServerEvents] onLogin(): nickname="+nickname+"; ip="+ip);

        // Если текущая сессия не активна => logout игрока
        if (!SessionManager.isSessionActive(nickname, ip)) {
            Utils.debug("[ServerEvents] onLogin(): nickname="+nickname+"; ip="+ip+": (isSessionActive == false)");
            AuthorizationManager.logout(nickname, ip);
        }

    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String nickname = player.getName();
        String ip = Utils.getIp(player);

        Utils.debug("[ServerEvents] onPlayerQuit(): nickname="+nickname+"; ip="+ip);

        // Если сессии включены => Сгенерировать дату окончания сессии и обновить сессию
        if (Config.isSessionsEnabled) {
            long sessionExpiredAt = Utils.getCurrentTime() + (Config.sessionsExpiredDelay * 1000);
            SessionManager.updateSession(nickname, ip, sessionExpiredAt);
        }
    }
}
