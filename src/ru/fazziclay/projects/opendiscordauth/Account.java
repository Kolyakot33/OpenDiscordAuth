package ru.fazziclay.projects.opendiscordauth;

import org.json.JSONObject;



public class Account {
    public static int TYPE_DISCORD = 0;
    public static int TYPE_NICKNAME = 1;

    private int search_type;
    private String search_value;
    private JSONObject json;

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


    private JSONObject getAccountJSONObject() {
        String search_type = "";
        if (this.search_type == TYPE_DISCORD) {
            search_type = "discord";
        }

        if (this.search_type == TYPE_NICKNAME) {
            search_type = "nickname";
        }

        int i = 0;
        while (i < LoginManager.accounts.length()) {
            if (LoginManager.accounts.getJSONObject(i).getString(search_type).equals(search_value)) {
                return LoginManager.accounts.getJSONObject(i);
            }
            i++;
        }
        return null;
    }
}
