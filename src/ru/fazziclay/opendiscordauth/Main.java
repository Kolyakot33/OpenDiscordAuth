//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.*;

import static ru.fazziclay.opendiscordauth.Config.*;
import static ru.fazziclay.opendiscordauth.UpdateChecker.THIS_VERSION_NAME;
import static ru.fazziclay.opendiscordauth.UpdateChecker.THIS_VERSION_TAG;



public class Main extends JavaPlugin {
    // Переменные
    public static JDA bot;                    //Пременная бота дискорд
    public static FileConfiguration config;   //Переменная конфигурации config.yml


    @Override // Рыбка - При старте плагина
    public void onEnable() {
        // Стартовое сообщение
        getLogger().info("#########################");
        getLogger().info("## Website:§b https://github.com/fazziclay/OpenDiscordAuth/");
        getLogger().info("## Author:§b 'https://github.com/fazziclay/");
        getLogger().info("## ");
        getLogger().info("## Current version: ("+THIS_VERSION_NAME+") (#"+THIS_VERSION_TAG+")");
        getLogger().info("## ");
        getLogger().info("## §a(Starting...)");
        getLogger().info("## ");

        // Загрузка плагина
        loadConfig();                                                           // Загрузка конфигурации
        Bukkit.getPluginManager().registerEvents(new Events(), this);     // Регистрация класса для обработки событий
        loadAccounts();                                                         // Загрузка файла accounts.json
        loadBot();                                                              // Заргузка бота

        if (CONFIG_BUNGEECORD_ENABLE) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }


        // Сообщение о конце загрузки
        getLogger().info("## ");
        getLogger().info("## §a(Started!)");
        getLogger().info("## ");
        getLogger().info("#########################");

        loadUpdateChecker();
    }

    @Override // Рыблка - При выключении плагина
    public void onDisable() {
        getLogger().info("#########################");
        getLogger().info("## ");
        getLogger().info("## §c(Stopping...)");

        // Отсоеденить всех незахогиненых игроков от сервера.
        for (String i : LoginManager.noLoginList) {
            Player player = Bukkit.getPlayer(UUID.fromString(i));
            if (!(player==null) && player.isOnline()) {
                player.kickPlayer(CONFIG_MESSAGE_KICK_PLUGIN_DISABLED);
            }
        }
        getLogger().info("## Kicked all no login players.");

        // Выключит бота.
        try {
            getLogger().info("## Bot stopped.");
            bot.shutdownNow();
        } catch (Exception ignored) {}

        getLogger().info("## §c(Stopped!)");
        getLogger().info("## ");
        getLogger().info("#########################");
    }


    private void loadConfig() { // Загрузка конфига
        getConfig().options().copyDefaults(true);
        saveConfig();
        config = getConfig();
    }

    private void loadAccounts() { // Загрузка аккаунтов
        if (!FileUtil.isFile(LoginManager.data_string_path)) {
            FileUtil.writeFile(LoginManager.data_string_path, "[]");
        }
        LoginManager.accounts = new JSONArray(FileUtil.readFile(LoginManager.data_string_path));
    }

    private void loadBot() { // Загрузка бота
        try {
            bot = JDABuilder.createDefault(CONFIG_BOT_TOKEN)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new DiscordBot())
                    .build();

            bot.awaitReady();

            getLogger().info("## Bot §a'" + bot.getSelfUser().getName() + "#" + bot.getSelfUser().getDiscriminator() + "'§r started!");

        } catch (Exception e) {
            getLogger().info("##");
            getLogger().info("## §c[ERROR] " + e.toString());
            getLogger().info("##");
        }
    }

    private void loadUpdateChecker() { // Загрузка Апдейт чекера
        if (!CONFIG_UPDATE_CHECKER) {
            return;
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                UpdateChecker updateChecker = new UpdateChecker();
                if (updateChecker.isLast == 0) {
                    getLogger().info("## OpenDiscordAuth New Version! Update please!");
                    getLogger().info("## ");
                    getLogger().info("## Current version: (" + THIS_VERSION_NAME + ") (#" + THIS_VERSION_TAG + ")");
                    getLogger().info("## Last version:    (" + updateChecker.version_name + ") (#" + updateChecker.version_tag + ")");
                    getLogger().info("## Download page:§b " + updateChecker.version_link);
                }
            }
        }, 4000L);
    }


    // Утилиты
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
