package ru.fazziclay.projects.opendiscordauth;

import org.json.JSONObject;



public class Account {
    public static String data_string_path = ("./plugins/OpenDiscordAuth/accounts.json");


    public static int TYPE_DISCORD = 0;
    public static int TYPE_NICKNAME = 1;

    private int search_type;
    private String search_value;
    private JSONObject json;
    private int index = 0;

    public Account(int search_type, String search_value) {
        this.search_type = search_type;
        this.search_value = search_value;

        this.json = getAccountJSONObject();
    }

    public boolean isExist() {
        return (json != null);
    }

    public String getName() {
        return json.getString("nickname");
    }

    public String getDiscord() {
        return json.getString("discord");
    }

    public static void addAccount(String nick, String discord) {
        LoginManager.accounts.put(new JSONObject("{'nickname':'"+nick+"', 'discord':'"+discord+"'}"));
        FileUtil.writeFile(data_string_path, LoginManager.accounts.toString(4));
    }

    public void removeAccount() {
        LoginManager.accounts.remove(index);
        FileUtil.writeFile(data_string_path, LoginManager.accounts.toString(4));
    }


    private JSONObject getAccountJSONObject() {
        String search_type = "";
        if (this.search_type == TYPE_DISCORD) {
            search_type = "discord";
        }

        if (this.search_type == TYPE_NICKNAME) {
            search_type = "nickname";
        }

        index = 0;
        while (index < LoginManager.accounts.length()) {
            if (LoginManager.accounts.getJSONObject(index).getString(search_type).equals(search_value)) {
                return LoginManager.accounts.getJSONObject(index);
            }
            index++;
        }
        return null;
    }
}
