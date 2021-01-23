//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.projects.opendiscordauth;

import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



public class UpdateChecker {
    String GITHUB_API_LINK = "https://api.github.com/repos/fazziclay/opendiscordauth/releases";

    Integer version_num  = null;
    String version_name  = "-";
    String version_link  = "-";
    Integer lastVersion  = -1;
    String version_description = "-";

    JSONArray json;
    JSONObject json_current;

    public UpdateChecker() {
        try {
            InputStream inputStream = new URL(GITHUB_API_LINK).openStream();
            Scanner scanner = new Scanner(inputStream);

            json = new JSONArray(scanner.nextLine());
        } catch (Exception e) {
            return;
        }


        if (Main._VERSION_RELEASE) {
            int i = 0;
            while (i < json.length()) {
                json_current = new JSONObject(json.getJSONObject(i));
                if (!json_current.getBoolean("prerelease")) {
                    break;
                }
                i++;
            }
        } else {
            json_current = json.getJSONObject(0);
        }

        version_name        = json_current.getString("name");
        version_num         = Integer.parseInt(json_current.getString("tag_name"));
        version_link        = json_current.getString("html_url");
        version_description = json_current.getString("body");

        if (Main._VERSION_NUM < version_num) {
            lastVersion = 0;
        } else if (Main._VERSION_NUM.equals(version_num)) {
            lastVersion = 1;
        } else {
            lastVersion = 2;
        }
    }

}
