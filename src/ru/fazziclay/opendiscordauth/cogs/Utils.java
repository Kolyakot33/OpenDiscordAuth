// https://github.com/fazziclay

package ru.fazziclay.opendiscordauth.cogs;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.fazziclay.opendiscordauth.Main;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;


public class Utils {

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static void print(String message) {
        if (!Config.debugStartedMode) {
            Bukkit.getLogger().info(message);
        } else {
            System.out.println(message);
        }
    }

    public static boolean isStringEmpty(String string) {
        return (string.equalsIgnoreCase("-1") || string.equalsIgnoreCase("null") || string.equalsIgnoreCase("none"));
    }

    public static void debug(String message) { // Используется для отладки кода
        if (Config.isDebugEnabled) {
            // Console
            Utils.print("[Debug] "+message);

            // File
            Date data = new Date();
            SimpleDateFormat dataFormat = new SimpleDateFormat(Config.debugTimeFormat);

            String old = Utils.readFile(Config.debugFilePath);
            Utils.writeFile(Config.debugFilePath, (old + "\n["+dataFormat.format(data)+"] " + message));
        }
    }


    public static int getRandom(int minimum, int maximum) { // Получение случайного числа в диапозоне
        Utils.debug("[Utils] getRandom(minimum="+minimum+", maximum="+maximum+")");
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(maximum - minimum + 1) + minimum;
    }


    public static void sendMessage(MessageChannel channel, String message) { // Отправка сообщения в Discord
        Utils.debug("[Utils] sendMessage(channel=..., message="+message+")");

        if (channel == null || message == null || Utils.isStringEmpty(message)) {
            Utils.debug("[Utils] sendMessage(channel=..., message="+message+"): Stopped! by '(channel==null||message==null||Utils.isStringEmpty(...))==true'");
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


    public static void sendMessage(Player player, String message) { // Отправка сообщенимя игроку Minecraft
        Utils.debug("[Utils] sendMessage(player=..., message="+message+")");

        if (player == null || message == null || Utils.isStringEmpty(message)) {
            Utils.debug("[Utils] sendMessage(player=..., message="+message+"): Stopped! by '(player==null||message==null||Utils.isStringEmpty(...))==true'");
            return;
        }

        player.sendMessage(message.replace("&", "§"));
    }


    public static void kickPlayer(Player player, String reason) { // Кик игрока
        Utils.debug("[Utils] kickPlayer(player=..., reason="+reason+")");

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



    // File Utils
    public static void createDirIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    private static void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            createDirIfNotExists(dirPath);
            File folder = new File(dirPath);
            folder.mkdirs();
        }

        File file = new File(path);

        try {
            if (!file.exists())
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readFile(String path) {
        Utils.createNewFile(path);

        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(path);

            char[] buff = new char[1024];
            int length;

            while ((length = fileReader.read(buff)) > 0) {
                stringBuilder.append(new String(buff, 0, length));
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder.toString();
    }


    public static void writeFile(String path, String content) {
        Utils.createNewFile(path);
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(path, false);
            fileWriter.write(content);
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static boolean isFile(String path) {
        File file = new File(path);
        return file.isFile();
    }
}
