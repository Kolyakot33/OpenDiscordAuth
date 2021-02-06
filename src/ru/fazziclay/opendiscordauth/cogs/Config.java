//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth.cogs;

import static ru.fazziclay.opendiscordauth.Main.config;



public class Config {
    public static String       CONFIG_MESSAGE_KICK_PLUGIN_DISABLED         = config.getString("message.KICK_PLUGIN_DISABLED");
    public static String       CONFIG_BOT_TOKEN                            = config.getString("bot_token");
    public static Boolean      CONFIG_UPDATE_CHECKER                       = config.getBoolean("update_checker");

    public static int          CONFIG_GENERATOR_LOGIN_MINIMUM              = config.getInt("generator.login_minimum");
    public static int          CONFIG_GENERATOR_LOGIN_MAXIMUM              = config.getInt("generator.login_maximum");
    public static int          CONFIG_GENERATOR_REGISTER_MINIMUM           = config.getInt("generator.register_minimum");
    public static int          CONFIG_GENERATOR_REGISTER_MAXIMUM           = config.getInt("generator.register_maximum");
    public static int          CONFIG_GENERATOR_CODE_EXPIRED_TIME          = config.getInt("generator.code_expired_time");
    public static String       CONFIG_MESSAGE_HELLO                        = config.getString("message.HELLO");
    public static String       CONFIG_MESSAGE_LOGIN_GIVE_CODE              = config.getString("message.LOGIN_GIVE_CODE");
    public static String       CONFIG_MESSAGE_REGISTER_GIVE_CODE           = config.getString("message.REGISTER_GIVE_CODE");
    public static String       CONFIG_MESSAGE_REGISTER_CONFIRM             = config.getString("message.REGISTER_CONFIRM");
    public static String       CONFIG_MESSAGE_REGISTER_CANCEL              = config.getString("message.REGISTER_CANCEL");
    public static String       CONFIG_MESSAGE_KICK_AUTH_TIMEOUT            = config.getString("message.KICK_AUTH_TIMEOUT");
    public static int          CONFIG_IP_SAVING_TYPE                       = config.getInt("ip_saving_type");

    public static boolean      CONFIG_REGISTER_ADD_ROLE_ENABLE                      = config.getBoolean("register_add_role.enable");
    public static String       CONFIG_REGISTER_ADD_ROLE_GUILD                       = config.getString("register_add_role.guild");
    public static String       CONFIG_REGISTER_ADD_ROLE_ROLE                        = config.getString("register_add_role.role");
    public static boolean      CONFIG_REGISTER_ADD_ROLE_OBLIGATORILY                = config.getBoolean("register_add_role.obligatorily");
    public static String       CONFIG_MESSAGE_REGISTER_ADD_ROLE_MEMBER_NOT_FOUND    = config.getString("message.REGISTER_ADD_ROLE_MEMBER_NOT_FOUND");

    public static boolean      CONFIG_BUNGEECORD_ENABLE                    = config.getBoolean("bungeecord.enable");
    public static String       CONFIG_BUNGEECORD_SERVER                    = config.getString("bungeecord.server");
    public static String       CONFIG_MESSAGE_LOGIN_SECCU                  = config.getString("message.LOGIN_SECCU");
    public static int          CONFIG_IP_EXPIRED_TIME                      = config.getInt("ip_expired_time");


    public static String       CONFIG_MESSAGE_REGISTER_WARN                 = config.getString("message.REGISTER_WARN");
    public static String       CONFIG_MESSAGE_REGISTER_CHECK_GAME           = config.getString("message.REGISTER_CHECK_GAME");
    public static String       CONFIG_MESSAGE_CODE_NOT_FOUND                = config.getString("message.CODE_NOT_FOUND");
    public static String       CONFIG_MESSAGE_CODE_USING_E1                 = config.getString("message.CODE_USING_E1");

    public static String       CONFIG_MESSAGE_CODE_GENERATOR_E1             = config.getString("message.CODE_GENERATOR_E1");
    public static String       CONFIG_ACCOUNTS_FILE_PATH                    = config.getString("accounts_file_path");
    public static String       CONFIG_COMMAND_REGISTER_CONFIRM              = config.getString("command.register_confirm");
    public static String       CONFIG_COMMAND_REGISTER_CANCEL               = config.getString("command.register_cancel");

    public static String       CONFIG_MESSAGE_KICK_ACCOUNT_CREATE_TIMEOUT   = config.getString("message.KICK_ACCOUNT_CREATE_TIMEOUT");
}