//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth.cogs;


import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fazziclay.opendiscordauth.Main;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Objects;
import java.util.Random;

import static ru.fazziclay.opendiscordauth.cogs.LoginManager.*;



public class Utils {
    public static String getCode(int minimum, int maximum) {
        Integer a = getRandom(minimum, maximum);

        int iteration = 0;
        while (tempCodes.containsKey(String.valueOf(a))) {
            a = getRandom(minimum, maximum);

            if (iteration >= 100) {
                return "null";
            }
            iteration++;
        }
        return String.valueOf(a);
    }

    public static void connectToServer(Player player, String server) { // Подключение к серверу BungeeCord
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
            player.sendPluginMessage(Main.getPlugin(Main.class), "BungeeCord", b.toByteArray());

        } catch (Exception e1) {
            sendMessage(player, "&c"+e1.toString());
        }
    }

    public static int getRandom(int minimum, int maximum) { // Получение случайного числа в диапозоне
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(maximum - minimum + 1) + minimum;
    }

    public static void sendMessage(MessageChannel channel, String message) { // Отправка сообщения в Discord
        if (channel == null || message == null || message.equals("none") || message.equals("-1") || message.equals("null")) {
            return;
        }

        String[] replacements = {"&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&0", "&a", "&e", "&d", "&f", "&r", "&l", "&k", "&c", "&b", "&n", "&m"};

        int i = 0;
        while (i < replacements.length) {
            message = message.replace(replacements[i], "");
            i++;
        }

        channel.sendMessage(message).queue();
    }

    public static void sendMessage(Player channel, String message) { // Отправка сообщенимя игроку Minecraft
        if (channel == null || message == null || message.equals("none") || message.equals("-1") || message.equals("null")) {
            return;
        }

        channel.sendMessage(message.replace("&", "§"));
    }

    public static void kickPlayer(Player player, String reason) { // Кик игрока
        if (reason == null) {
            reason = "[OpenDiscordAuth] kicked no reason.§n§b https://github.com/fazziclay/opendiscordauth/";
        }

        if (player == null) {
            return;
        }

        String finalReason = reason;
        Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> player.kickPlayer(finalReason));
    }

    public static String getIp(Player player) {
        return Objects.requireNonNull(player.getAddress()).getHostName();
    }
}
