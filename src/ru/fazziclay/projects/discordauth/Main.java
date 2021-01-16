package ru.fazziclay.projects.discordauth;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;


public class Main extends JavaPlugin {
    public static String data_string_path = ("./plugins/OpenDiscordAuth/accounts.json");

    String error;
    public JDA jda = null;
    public static JSONArray data;
    public static Config config;



    public static void main(String[] args) {
        System.out.println("Not sandbox started.");
    }


    // Рыбки
    @Override
    public void onEnable() {
        getLogger().info("########################");
        getLogger().info("## Starting...");
        getLogger().info("## Github - https://github.com/FazziClay/OpenDiscordAuth");
        getLogger().info("##");


        Bukkit.getPluginManager().registerEvents(new Events(), this); // ПОдключаем евенты майнкрафта
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord"); // Строка для того что бы работал тетепорт bungeecord
        loadConfig(); // Загружаем конфиг
        boolean a = loadBot(); // Загружаем бота
        if (!a) {
            getLogger().info("## [ERROR] Bot starting error.");
            getLogger().info("## [ERROR] JavaError: " + error);
        } else {
            getLogger().info("## Bot started!");
        }

        /// - Если файла data.json не существует то создать его с содержимым '[]'
        if (!FileUtil.isFile(data_string_path)) {
            FileUtil.writeFile(data_string_path, "[]");
        }
        data = new JSONArray(FileUtil.readFile(data_string_path));

        getLogger().info("## ");
        getLogger().info("## Starting!");
        getLogger().info("########################");
    }

    @Override
    public void onDisable() {
        getLogger().info("## Plugin disabled!");
        try {
            jda.shutdownNow(); // Выклюить бота
        } catch (Exception ignored) {}
    }

    // - Запустить бота
    private boolean loadBot() {
        try {
            jda = JDABuilder.createDefault(config.bot_token)
                    .addEventListeners(new Bot())
                    .build();

            jda.awaitReady();

            return true;

        } catch (Exception e) {
            error = e.toString();
            return false;
        }
    }

    // - Добавить аккаунт игрка в data.json
    public static void addAccount(String nick, String discord) {
        JSONObject temp = new JSONObject("{'name':'"+nick+"', 'discord':'"+discord+"'}");
        Main.data.put(temp);
        FileUtil.writeFile(Main.data_string_path, Main.data.toString(4));
    }

    // - Инициализировать конфиг
    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        config = new Config(getConfig());
    }

    // - Перенаправление игрока на другой сервер bungeecord (код взят и интернета, но работает.)
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
        } catch (org.bukkit.plugin.messaging.ChannelNotRegisteredException e) {
            Bukkit.getLogger().warning(" ERROR - Usage of bungeecord connect effects is not possible. Your server is not having bungeecord support (Bungeecord channel is not registered in your minecraft server)!\nJavaError: " + e.toString());
        }

    }


    public static void confirmLogin(Player player) {
        player.sendMessage(config.messages.connecting_to_server.replace("&", "§"));
        Main.connectToServer(player, config.redirect_server);
    }


    public static void discordSend(MessageChannel channel, String message) {
        String m = message;
        String[] replacements = {"&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&0", "&a", "&e", "&d", "&f", "&r", "&l", "&k", "&c", "&b", "&n", "&m"};

        if (m.equals("null")) {
            return;
        }

        int i = 0;
        while (i < replacements.length) {
            m = m.replace(replacements[i], "");
            i++;
        }

        channel.sendMessage(m).queue();
    }


    public static void minecraftSend(Player player, String message) {
        String m = message;

        if (m.equals("null")) {
            return;
        }

        m = m.replace("&", "§");
        player.sendMessage(m);
    }

}
