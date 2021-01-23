# OpenDiscordAuth
## Protect your account with discord account

[Setup](https://github.com/FazziCLAY/OpenDiscordAuth/wiki/Setup)

## config.yml:
```yml
bot_token: ''          // Сюда надо ввести токен бота дискорд.
ip_saving_type: 0      // Тип сохранения ip. 0 - сохранять при выходе. 1 - сохранять при входе
ip_expired_time: 120   // На сколько секунд ip игрока будет сохранено, 0 для выключения.

generator:
  login_minimum: 0          // Минимальное рандомное число кода входа
  login_maximum: 9999       // Максимальное рандомное число кода входа
  register_minimum: 9999    // Минимальное рандомное число кода регистрации
  register_maximum: 999999   // Максимальное рандомное число кода регистрации
  code_expired_time: 60     // Время сколько код активен в секундах. После истечения игрока кикает а код удаляется.


bungeecord:
  enable: false   // Включён ли bungeecord
  server: lobby   // bungeecord сервер куда после авторизации надо переместить игрока


message:
  KICK_PLUGIN_DISABLED: "Вы были кикнуты так как плагин был перезагружен, а вы не залогинены."
  KICK_AUTH_TIMEOUT: "Время авторизации вышло вышло."
  HELLO: "Приветственное сообщение."
  LOGIN_SECCU: "Успешная авторизация."
  LOGIN_GIVE_CODE: "Код для входа - $code."
  REGISTER_GIVE_CODE: "Код для регистрации - $code."
  REGISTER_CONFIRM: "Успешная регистрация."
  REGISTER_CANCEL: "Регистрация отменена."
  REGISTER_WARN: "Вы хотите привязать к этому нику аккаунт $discord? Напишите в чат 'confirm' для подтверждения, 'cancel' для отмены."
  REGISTER_CHECK_GAME: "Подтвердите регистрацию в игре."
  CODE_NOT_FOUND: "Такого кода не существует."
  CODE_USING_E1: "Данный код предназначен не для вашего дискорд аккаунта."
```

## Thank you!
