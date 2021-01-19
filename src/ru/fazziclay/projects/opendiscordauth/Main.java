package ru.fazziclay.projects.opendiscordauth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;
import java.util.UUID;


public class Main extends JavaPlugin {
    String CONFIG_MESSAGE_KICK_PLUGIN_DISABLED;
    String CONFIG_BOT_TOKEN;

    public static JDA bot;                    //Пременная бота дискорд
    public static FileConfiguration config;   //Переменная конфигурации config.yml


    @Override
    public void onEnable() {
        // Стартовое сообщение
        getLogger().info("#########################");
        getLogger().info("## Website: https://github.com/fazziclay/OpenDiscordAuth/");
        getLogger().info("## Author: 'https://github.com/fazziclay/");
        getLogger().info("## ");
        getLogger().info("## (Starting...)");
        getLogger().info("## ");

        // Загрузка плагина
        loadConfig();                                                           // Загрузка конфигурации
        Bukkit.getPluginManager().registerEvents(new Events(), this);     // Регистрация класса для обработки событий
        loadAccounts();                                                         // Загрузка файла accounts.json
        loadBot();                                                              // Заргузка бота

        if (LoginManager.CONFIG_BUNGEECORD_ENABLE) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }


        // Сообщение о конце загрузки
        getLogger().info("## ");
        getLogger().info("## (Started!)");
        getLogger().info("## ");
        getLogger().info("#########################");
    }


    @Override
    public void onDisable() {
        for (String i : LoginManager.noLoginList) {
            Player player = Bukkit.getPlayer(UUID.fromString(i));
            if (!(player==null) && player.isOnline()) {
                player.kickPlayer(CONFIG_MESSAGE_KICK_PLUGIN_DISABLED);
            }
        }
        
        
        try {
            bot.shutdownNow();
        } catch (Exception ignored) {}
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        config = getConfig();

        CONFIG_MESSAGE_KICK_PLUGIN_DISABLED      = config.getString("message.KICK_PLUGIN_DISABLED");
        CONFIG_BOT_TOKEN                         = config.getString("bot_token");
    }

    private void loadAccounts() {
        if (!FileUtil.isFile(Account.data_string_path)) {
            FileUtil.writeFile(Account.data_string_path, "[]");
        }
        LoginManager.accounts = new JSONArray(FileUtil.readFile(Account.data_string_path));
    }

    private void loadBot() {
        try {
            bot = JDABuilder.createDefault(CONFIG_BOT_TOKEN)
                    .addEventListeners(new Bot())
                    .build();

            bot.awaitReady();

            getLogger().info("## Bot '" + bot.getSelfUser().getName() + "' started!");

        } catch (Exception e) {
            getLogger().info("##");
            getLogger().info("## [ERROR] " + e.toString());
            getLogger().info("##");
        }
    }


    // Утилиты
    public static void connectToServer(Player player, String server) {
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


    public static int getRandom(int minimum, int maximum) {
        Random random = new Random();
        return random.nextInt(maximum - minimum + 1) + minimum;
    }


    public static void sendMessage(MessageChannel channel, String message) {
        if (channel == null || message == null || message.equals("none")) {
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

    public static void sendMessage(Player channel, String message) {
        if (channel == null || message == null || message.equals("none")) {
            return;
        }

        channel.sendMessage(message.replace("&", "§"));
    }

}
