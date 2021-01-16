package ru.fazziclay.projects.discordauth;

import org.json.JSONObject;

public class Data {
    String str;
    String type;
    JSONObject json;


    public Data(String str, String type) {
        this.str = str;
        this.type = type;

        this.json = getJSONObject(type);
    }


    public boolean isExist() {
        return (json != null);
    }

    public String getName() {
        return json.getString("name");
    }

    public String getDiscord() {
        return json.getString("discord");
    }


    private JSONObject getJSONObject(String type) {
        int i = 0;
        while (i < Main.data.length()) {
            if (Main.data.getJSONObject(i).getString(type).equals(str)) {
                return Main.data.getJSONObject(i);
            }
            i++;
        }
        return null;
    }
}
