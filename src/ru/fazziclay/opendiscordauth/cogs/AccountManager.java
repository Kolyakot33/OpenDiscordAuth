//#
//# Author https://fazziclay.ru/ | https://github.com/fazziclay/
//#

package ru.fazziclay.opendiscordauth.cogs;

import org.json.JSONObject;
import ru.fazziclay.opendiscordauth.objects.Account;

import static ru.fazziclay.opendiscordauth.cogs.Utils.*;

import static ru.fazziclay.opendiscordauth.cogs.Config.CONFIG_ACCOUNTS_FILE_PATH;
import static ru.fazziclay.opendiscordauth.cogs.FileUtil.writeFile;
import static ru.fazziclay.opendiscordauth.cogs.LoginManager.*;



public class AccountManager {
    public static final int SEARCH_TYPE_ID          = 0;
    public static final int SEARCH_TYPE_DISCORD     = 1;
    public static final int SEARCH_TYPE_NICKNAME    = 2;


    public static void addAccount(String nickname, String discord) {
        String id = String.valueOf(getRandom(-999999999, 999999999));

        JSONObject jsonObjectAccount = new JSONObject("{'id': '"+id+"', 'discord': '"+discord+"', 'nickname':'"+nickname+"'}");
        Account account              = new Account(id, discord, nickname);
        accountsJson .put(jsonObjectAccount);
        accounts     .add(account);

        writeFile(CONFIG_ACCOUNTS_FILE_PATH, accountsJson.toString(4));
    }

    public static void removeAccount(Account account) {
        // dev.
    }

    public static void changeNickname(Account account, String newNickname) {
        // dev.
    }

    public static boolean isAccountExist(int SEARCH_TYPE, String SEARCH_VALUE) {
        Account a = getAccountByValue(SEARCH_TYPE, SEARCH_VALUE);
        return (a != null);
    }

    public static Account getAccountByValue(int SEARCH_TYPE, String SEARCH_VALUE) {
        Account account = null;

        int i = 0;
        while (i < accounts.size()) {
            Account currentAccount = accounts.get(i);
            String  currentValue   = "";

            if (SEARCH_TYPE == SEARCH_TYPE_ID) {
                currentValue = currentAccount.id;
            }
            if (SEARCH_TYPE == SEARCH_TYPE_DISCORD) {
                currentValue = currentAccount.discord;
            }
            if (SEARCH_TYPE == SEARCH_TYPE_NICKNAME) {
                currentValue = currentAccount.nickname;
            }


            if (currentValue.equals(SEARCH_VALUE)) {
                account = currentAccount;
                break;
            }

            i++;
        }

        return account;
    }
}

