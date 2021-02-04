package ru.fazziclay.opendiscordauth;

import static ru.fazziclay.opendiscordauth.Main.config;



public class Config {
    static String       CONFIG_MESSAGE_KICK_PLUGIN_DISABLED         = config.getString("message.KICK_PLUGIN_DISABLED");
    static String       CONFIG_BOT_TOKEN                            = config.getString("bot_token");
    static Boolean      CONFIG_UPDATE_CHECKER                       = config.getBoolean("update_checker");

    static int          CONFIG_GENERATOR_LOGIN_MINIMUM              = config.getInt("generator.login_minimum");
    static int          CONFIG_GENERATOR_LOGIN_MAXIMUM              = config.getInt("generator.login_maximum");
    static int          CONFIG_GENERATOR_REGISTER_MINIMUM           = config.getInt("generator.register_minimum");
    static int          CONFIG_GENERATOR_REGISTER_MAXIMUM           = config.getInt("generator.register_maximum");
    static int          CONFIG_GENERATOR_CODE_EXPIRED_TIME          = config.getInt("generator.code_expired_time");
    static String       CONFIG_MESSAGE_HELLO                        = config.getString("message.HELLO");
    static String       CONFIG_MESSAGE_LOGIN_GIVE_CODE              = config.getString("message.LOGIN_GIVE_CODE");
    static String       CONFIG_MESSAGE_REGISTER_GIVE_CODE           = config.getString("message.REGISTER_GIVE_CODE");
    static String       CONFIG_MESSAGE_REGISTER_CONFIRM             = config.getString("message.REGISTER_CONFIRM");
    static String       CONFIG_MESSAGE_REGISTER_CANCEL              = config.getString("message.REGISTER_CANCEL");
    static String       CONFIG_MESSAGE_KICK_AUTH_TIMEOUT            = config.getString("message.KICK_AUTH_TIMEOUT");
    static int          CONFIG_IP_SAVING_TYPE                       = config.getInt("ip_saving_type");

    static boolean      CONFIG_REGISTER_ADD_ROLE_ENABLE                      = config.getBoolean("register_add_role.enable");
    static String       CONFIG_REGISTER_ADD_ROLE_GUILD                       = config.getString("register_add_role.guild");
    static String       CONFIG_REGISTER_ADD_ROLE_ROLE                        = config.getString("register_add_role.role");
    static boolean      CONFIG_REGISTER_ADD_ROLE_OBLIGATORILY                = config.getBoolean("register_add_role.obligatorily");
    static String       CONFIG_MESSAGE_REGISTER_ADD_ROLE_MEMBER_NOT_FOUND    = config.getString("message.REGISTER_ADD_ROLE_MEMBER_NOT_FOUND");

    static boolean      CONFIG_BUNGEECORD_ENABLE                    = config.getBoolean("bungeecord.enable");
    static String       CONFIG_BUNGEECORD_SERVER                    = config.getString("bungeecord.server");
    static String       CONFIG_MESSAGE_LOGIN_SECCU                  = config.getString("message.LOGIN_SECCU");
    static int          CONFIG_IP_EXPIRED_TIME                      = config.getInt("ip_expired_time");


    static String       CONFIG_MESSAGE_REGISTER_WARN                 = config.getString("message.REGISTER_WARN");
    static String       CONFIG_MESSAGE_REGISTER_CHECK_GAME           = config.getString("message.REGISTER_CHECK_GAME");
    static String       CONFIG_MESSAGE_CODE_NOT_FOUND                = config.getString("message.CODE_NOT_FOUND");
    static String       CONFIG_MESSAGE_CODE_USING_E1                 = config.getString("message.CODE_USING_E1");
}