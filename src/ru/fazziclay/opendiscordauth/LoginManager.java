package ru.fazziclay.opendiscordauth;

import net.minecraft.server.v1_13_R2.Vec3D;
import net.minecraft.server.v1_13_R2.Vector3f;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static org.bukkit.GameMode.*;
import static ru.fazziclay.opendiscordauth.Config.*;
import static ru.fazziclay.opendiscordauth.Main.*;



public class LoginManager {
    static String data_string_path = ("./plugins/OpenDiscordAuth/accounts.json");


    public static Map<String, Code> codes = new HashMap<>();
    public static Map<String, Map>      temp_accounts = new HashMap<>();                 // key type minecraft nickname
    public static Map<String, String>   ips = new HashMap<>();                           // key type minecraft nickname

    public static JSONArray accounts;
    public static List<String> noLoginList = new ArrayList<>();


    public static String getCode(int minimum, int maximum) {
        Integer a = getRandom(minimum, maximum);

        Integer iteration = 0;
        while (codes.containsKey(String.valueOf(a))) {
            a = getRandom(minimum, maximum);

            if (iteration >= 100) {
                return "null";
            }
            iteration++;
        }
        return String.valueOf(a);
    }


    public static boolean isLogin (String uuid) {
        return (!noLoginList.contains(uuid));
    }


    public static void login(Player player) {
        sendMessage(player, CONFIG_MESSAGE_LOGIN_SECCU);      // Отправить сообщение о успешной авторизации.
        String      ip = player.getAddress().getHostName();   // Пременная АйПи игрока.
        String      nickname = player.getName();              // Пременная никнейма игрока.
        GameMode    gamemode = player.getGameMode();          // Переменная режима игры игрока.

        if (gamemode==SURVIVAL || gamemode==ADVENTURE) {         // Если режим игры игрока SURVIVAL или ADVENTURE
            player.setAllowFlight(false);                            //  Выключить ему разрешение летать.
        }

        if (CONFIG_IP_SAVING_TYPE == 1) {                       // Если режим сохранения АйПи это 1
            saveSession(nickname, ip);                              // Сохранить айпи.
        }

        if (CONFIG_BUNGEECORD_ENABLE) {                         // Если BungeeCord включён
            Main.connectToServer(player, CONFIG_BUNGEECORD_SERVER); // Подключить игрока к серверу.
        }

        noLoginList.remove(player.getUniqueId().toString());
    }

    public static void addAccount(String nick, String discord) {
        LoginManager.accounts.put(new JSONObject("{'nickname':'"+nick+"', 'discord':'"+discord+"'}"));
        FileUtil.writeFile(data_string_path, LoginManager.accounts.toString(4));
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