package ru.fazziclay.projects.discordauth;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    String bot_token;
    String redirect_server;
    int ip_expired_delay;

    Messages messages;
    CodeGenerator codeGenerator;

    public Config(FileConfiguration config) {
        redirect_server = config.getString("redirect_server");
        ip_expired_delay = config.getInt("ip_expired_delay");
        bot_token = config.getString("bot_token");

        messages = new Messages(config);
        codeGenerator = new CodeGenerator(config);
    }
}


class Messages {
    String hello_message;
    String code_not_found;
    String connecting_to_server;
    String register_cancel;
    String register_successfully;
    String start_register_message;
    String start_login_message;
    String login_confirm;
    String confirm_register_in_game;
    String register_confirm;

    public Messages(FileConfiguration config) {
        hello_message = config.getString("messages.hello_message");

        code_not_found = config.getString("messages.code_not_found");
        connecting_to_server = config.getString("messages.connecting_to_server");
        register_cancel = config.getString("messages.register_cancel");
        register_successfully = config.getString("messages.register_successfully");

        start_register_message = config.getString("messages.start_register_message");
        start_login_message = config.getString("messages.start_login_message");
        login_confirm = config.getString("messages.login_confirm");
        confirm_register_in_game = config.getString("messages.confirm_register_in_game");
        register_confirm = config.getString("messages.register_confirm");
    }
}


class CodeGenerator {
    Login login;
    Register register;

    public CodeGenerator(FileConfiguration config) {
        login = new Login(config);
        register = new Register(config);
    }

    static class Login {
        int expired_delay;
        int min;
        int max;

        public Login(FileConfiguration config) {
            expired_delay = config.getInt("code_generator.login.expired_delay");
            min = config.getInt("code_generator.login.min");
            max = config.getInt("code_generator.login.max");
        }
    }

    static class Register {
        int expired_delay;
        int min;
        int max;

        public Register(FileConfiguration config) {
            expired_delay = config.getInt("code_generator.register.expired_delay");
            min = config.getInt("code_generator.register.min");
            max = config.getInt("code_generator.register.max");
        }
    }
}