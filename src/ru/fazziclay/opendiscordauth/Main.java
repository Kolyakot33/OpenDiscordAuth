package ru.fazziclay.opendiscordauth;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fazziclay.opendiscordauth.cogs.Config;
import ru.fazziclay.opendiscordauth.cogs.Utils;



public class Main extends JavaPlugin  {
    public static FileConfiguration pluginConfig;


    @Override
    public void onEnable() {
        Utils.debug("[Main] onEnable()");

        Utils.print("#########################");
        Utils.print("## Website:§b https://github.com/fazziclay/OpenDiscordAuth/");
        Utils.print("## Author:§b 'https://github.com/fazziclay/");
        Utils.print("## ");
        Utils.print("## Current version: (-1) (-1)");
        Utils.print("## ");
        Utils.print("## §a(Starting...)");
        Utils.print("## ");
        try {
            loadConfig();
            loadDiscordBot();

            Bukkit.getPluginManager().registerEvents(new ServerEvents(), this);

        } catch (Exception e) {
            Utils.print("##");
            Utils.print("## (Starting error.)");
            Utils.print("## JavaError: " + e.toString());
            Utils.print("##");
            Utils.print("#########################");

            Bukkit.getPluginManager().disablePlugins();
            return;
        }

        Utils.print("## ");
        Utils.print("## §a(Started!)");
        Utils.print("## ");
        Utils.print("#########################");
    }


    @Override
    public void onDisable() {
        Utils.debug("[Main] onDisable()");
    }


    private void loadConfig() { // Загрузка конфига
        Utils.debug("[Main] loadConfig()");
        getConfig().options().copyDefaults(true);
        saveConfig();
        pluginConfig = getConfig();
    }


    private void loadDiscordBot() throws Exception { // Загрузка бота
        Utils.debug("[Main] loadDiscordBot()");

        DiscordBot.bot = JDABuilder.createDefault(Config.discordBotToken)
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new DiscordBot())
                .build();

        DiscordBot.bot.awaitReady();

        getLogger().info("## Bot §a'" + DiscordBot.bot.getSelfUser().getName() + "#" + DiscordBot.bot.getSelfUser().getDiscriminator() + "'§r started!");
    }


}
