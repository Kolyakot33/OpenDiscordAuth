package ru.fazziclay.opendiscordauth;

import org.json.JSONObject;



public class Account {
    public static int TYPE_DISCORD  = 0;
    public static int TYPE_NICKNAME = 1;

    Integer     search_type;
    String      search_value;
    JSONObject  json;
    Integer     index;

    String   discord;
    String   nickname;
    Boolean  isExist;


    public Account(int search_type, String search_value) {
        this.search_type = search_type;
        this.search_value = search_value;
        this.json = getAccountJSONObject();

        isExist  = (json != null);
        if (isExist) {
            nickname = json.getString("nickname");
            discord = json.getString("discord");
        }

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
