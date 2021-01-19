package ru.fazziclay.projects.opendiscordauth;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static ru.fazziclay.projects.opendiscordauth.Main.config;
import static ru.fazziclay.projects.opendiscordauth.Main.sendMessage;



public class LoginManager {
    public static boolean CONFIG_BUNGEECORD_ENABLE      = config.getBoolean("bungeecord.enable");
    public static String CONFIG_BUNGEECORD_SERVER       = config.getString("bungeecord.server");
    public static String CONFIG_MESSAGE_LOGIN_SECCU     = config.getString("message.LOGIN_SECCU");
    public static int CONFIG_IP_EXPIRED_TIME            = config.getInt("ip_expired_time");



    public static String data_string_path = ("./plugins/OpenDiscordAuth/accounts.json");

    public static Map<String, Map> temp_accounts = new HashMap<>();                 // key type minecraft nickname
    public static Map<String, Player> temp_register_codes = new HashMap<>();        // key type register_code
    public static Map<String, Player> temp_login_codes = new HashMap<>();           // key type login_code
    public static Map<String, String> ips = new HashMap<>();                        // key type minecraft nickname

    public static JSONArray accounts;
    public static List<String> noLoginList = new ArrayList<>();

    public static boolean isLogin (String uuid) {
        return (!noLoginList.contains(uuid));
    }

    public static void addNoLogin(String uuid) {
        noLoginList.add(uuid);
    }

    public static void removeNoLogin(String uuid) {
        noLoginList.remove(uuid);
    }

    public static void addAccount(String nick, String discord) {
        accounts.put(new JSONObject("{'nickname':'"+nick+"', 'discord':'"+discord+"'}"));
        FileUtil.writeFile(data_string_path, accounts.toString(4));
    }


    public static void login(Player player) {
        sendMessage(player, CONFIG_MESSAGE_LOGIN_SECCU);
        String ip = player.getAddress().getHostName();
        String nickname = player.getName();

        player.setAllowFlight(false);

        if (Events.CONFIG_IP_SAVING_TYPE == 1) {
            saveSession(nickname, ip);
        }

        if (CONFIG_BUNGEECORD_ENABLE) {
            Main.connectToServer(player, CONFIG_BUNGEECORD_SERVER);

        }
        removeNoLogin(player.getUniqueId().toString());

    }

    public static void saveSession(String nickname, String ip) {
        if ((nickname == null) || (ip == null) || (CONFIG_IP_EXPIRED_TIME <= 0)) {
            return;
        }

        ips.put(nickname, ip);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ips.remove(nickname);
            }
        }, CONFIG_IP_EXPIRED_TIME * 1000L);
    }

}