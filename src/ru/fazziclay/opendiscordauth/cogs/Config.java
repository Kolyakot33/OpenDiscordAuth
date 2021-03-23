package ru.fazziclay.opendiscordauth.cogs;

public class Config {
    public static Boolean   isDebugEnabled = true;
    public static String    debugTimeFormat = "yyyy-MM-dd HH:mm:ss:SSS";
    public static String    debugFilePath = "./plugins/OpenDiscordAuth/debugging.txt";
    public static Boolean   debugStartedMode = true;

    public static String    discordBotToken = "";

    public static Boolean   isSessionsEnabled = true;
    public static long      sessionsExpiredDelay = 59;
}
