//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth.cogs;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import ru.fazziclay.opendiscordauth.objects.Account;
import ru.fazziclay.opendiscordauth.objects.Code;
import ru.fazziclay.opendiscordauth.objects.TempAccount;

import java.util.*;

import static org.bukkit.GameMode.*;
import static ru.fazziclay.opendiscordauth.cogs.AccountManager.SEARCH_TYPE_NICKNAME;
import static ru.fazziclay.opendiscordauth.cogs.AccountManager.isAccountExist;
import static ru.fazziclay.opendiscordauth.cogs.Config.*;
import static ru.fazziclay.opendiscordauth.cogs.Utils.*;



public class LoginManager {
    public static final Map<String, Code>   tempCodes     = new HashMap<>();
    public static Map<String, TempAccount>  tempAccounts  = new HashMap<>();
    public static List<Account>             accounts      = new ArrayList<>();
    public static JSONArray                 accountsJson  = new JSONArray();

    public static Map<String, String>   nicknameACodeAssociation = new HashMap<>();   // {"MINECRAFT_NICKNAME": "CODE"}
    public static Map<String, String>   ips                      = new HashMap<>();   // {"MINECRAFT_NICKNAME": "PLAYER_IP"}
    public static List<String>          noLoginList              = new ArrayList<>();


    public static boolean isLogin(String uuid) {
        return (!noLoginList.contains(uuid));
    }

    public static void login(Player player) {
        sendMessage(player, CONFIG_MESSAGE_LOGIN_SECCU);      // Отправить сообщение о успешной авторизации.
        String      ip = Objects.requireNonNull(player.getAddress()).getHostName();   // Пременная АйПи игрока.
        String      nickname = player.getName();              // Пременная никнейма игрока.
        GameMode    gamemode = player.getGameMode();          // Переменная режима игры игрока.

        if (gamemode==SURVIVAL || gamemode==ADVENTURE) {         // Если режим игры игрока SURVIVAL или ADVENTURE
            player.setAllowFlight(false);                            //  Выключить ему разрешение летать.
            player.setFlying(false);                                 //  Выключить ему полёт.
        }

        if (CONFIG_IP_SAVING_TYPE == 1) {                       // Если режим сохранения АйПи это 1
            saveSession(nickname, ip);                              // Сохранить айпи.
        }

        if (CONFIG_BUNGEECORD_ENABLE) {                         // Если BungeeCord включён
            connectToServer(player, CONFIG_BUNGEECORD_SERVER); // Подключить игрока к серверу.
        }

        noLoginList.remove(player.getUniqueId().toString());
    }

    public static void logout(Player player, Boolean manually) {
        String nickname = player.getName();
        String uuid     = player.getUniqueId().toString();
        String ip       = getIp(player);

        if (!noLoginList.contains(uuid)) {
            noLoginList.add(uuid); // Добавить игрока в список не залогиненых.
        }

        Timer codeExpiredTimer = new Timer();                                // Таймер истечения срока кода
        player.setAllowFlight(true);                                         // Рарзершить полёт для того если человек появится в воздухе.
        player.setFlying(true);                                              // Включить полёт что бы если человек в воздухе то камера не тряслась
        if (!ips.containsKey(nickname)) { ips.put(nickname, "none"); }       // Если в ips нету ключа с ником игрока то добавить его со значением none

        if (manually) {
            ips.put(nickname, "logout");

            if (CONFIG_BUNGEECORD_ENABLE) {
                kickPlayer(player, null);
                return;

            }

        } else {
            if (ips.get(nickname).equals(ip) && isAccountExist(SEARCH_TYPE_NICKNAME, nickname)) {   // Если ips[nickname] == текущий айпи && аккаунт данного игрока существует
                login(player);                                                                        // Залогинить игрока
                return;                                                                               // Остановить выполнение кода.
            }
        }

        sendMessage(player, CONFIG_MESSAGE_HELLO);                           // Отправить приветственное сообщение

        String give_code_message    = CONFIG_MESSAGE_REGISTER_GIVE_CODE;
        int code_type               = Code.TYPE_REGISTRATION_CODE;
        int code_generator_minimum  = CONFIG_GENERATOR_REGISTER_MINIMUM;
        int code_generator_maximum  = CONFIG_GENERATOR_REGISTER_MAXIMUM;
        String code = "(-1 error)";
        String finalCode = code;

        if (isAccountExist(SEARCH_TYPE_NICKNAME, nickname)) {
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

        tempCodes.put(code, new Code(code, code_type, player, codeExpiredTimer));   // Добавить код
        nicknameACodeAssociation.put(nickname, code);                               // Ассоциация кода с никнеймом для удаления при выходе
        sendMessage(player, give_code_message.replace("$code", code));        // Отправить сообщение выдачи кода


        codeExpiredTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                tempCodes.remove(finalCode);

                if (!LoginManager.isLogin(uuid)) { // Если игрок после истечения времени досихпор не залогинен то кикнуть его.
                    kickPlayer(player, CONFIG_MESSAGE_KICK_AUTH_TIMEOUT);
                }
            }
        }, CONFIG_GENERATOR_CODE_EXPIRED_TIME * 1000L);
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