//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

// ~ LICENSE ~
//
//  MIT License
//
//  Copyright (c) 2021 Mironov Stas
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.
//
// ~ ----------------- ~


package ru.fazziclay.opendiscordauth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;



public class UpdateChecker {
    // ~ ~ ~ THIS VERSION ~ ~ ~ \\
    public static Integer THIS_VERSION_TAG  = 2;
    public static String  THIS_VERSION_NAME = "Indev 0.2";
    public static Boolean THIS_VERSION_RELEASE = false;


    // ~ ~ ~ Settings ~ ~ ~ \\
    String GITHUB_API_LINK = "https://api.github.com/repos/fazziclay/opendiscordauth/releases";


    Integer     version_tag             = -1;   // Тег последний версии.
    String      version_name            = "-1";  // Имя последний версии.
    String      version_description     = "-1";  // Описание последней версии с гитхаба.
    Boolean     version_prerelease      = true; // Пререлиз ли эта версия.
    String      version_link            = "-1";  // Ссылка на страницу последний версии.
    Integer     isLast                  = -1;   // 0 - Текущая версия устарела. 1 - Текущая версия самая последняя 2 - Текущая версия выше самой последней версии на github
    Boolean     isError                 = true; // Пошла ли ошибка при запросе.

    JSONArray json;
    JSONObject json_version;

    public UpdateChecker() {
        // Парсер страницы api
        try {
            InputStream inputStream = new URL(GITHUB_API_LINK).openStream();
            Scanner scanner = new Scanner(inputStream);

            json = new JSONArray(scanner.nextLine());
        } catch (Exception e) {
            return;
        }

        if (parseVersion()) {
            return;
        }


        version_name        = json_version.getString("name");
        version_description = json_version.getString("body");
        version_tag         = toInt(json_version.getString("tag_name").replace("#", "").replace("№", ""));
        version_link        = json_version.getString("html_url");
        version_prerelease  = json_version.getBoolean("prerelease");

        if (THIS_VERSION_TAG < version_tag) {
            isLast = 0;
        } else if (THIS_VERSION_TAG.equals(version_tag)) {
            isLast = 1;
        } else {
            isLast = 2;
        }

        isError = false;
    }

    private boolean parseVersion() {
        if (json.length() == 0) {
            return true;
        }

        if (THIS_VERSION_RELEASE) {
            int i = 0;
            while (i < json.length()) {
                json_version = json.getJSONObject(i);
                if (!json_version.getBoolean("prerelease")) {
                    break;
                }
                i++;
            }
        } else {
            json_version = json.getJSONObject(0);
        }
        return false;
    }

    private Integer toInt(String a) {
        return Integer.parseInt(a);
    }

}
