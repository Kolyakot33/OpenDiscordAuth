# ~ OpenDiscordAuth ~
## Защитите свою учётную запись майнкрафт, учётной записью дискорда

[Инструкция по установке](https://github.com/FazziCLAY/OpenDiscordAuth/wiki/Setup)
[Демонстрация рыботы плагина](https://youtu.be/hQIfBeJ0X0o)

## Файл config.yml:
```yml
bot_token: ''          # Сюда надо ввести токен бота дискорд.
accounts_file_path: "./plugins/OpenDiscordAuth/accounts.json" # Путь до файла, где будут хранится аккаунты.
ip_saving_type: 0      # Тип сохранения ip. 0 - сохранять при выходе. 1 - сохранять при входе
ip_expired_time: 120   # На сколько секунд ip игрока будет сохранено, 0 для выключения.
update_checker: true

register_add_role:     # Добавление роли при регистрации
  enable: false             # Включено ли
  guild: 7000000000000000   # ID Дискорд сервера на котором надо выдать роль
  role: 7000000000000000    # ID Роли которую надо выдать
  obligatorily: true        # Обязательная ли эта процедура. Если это включено то в случае ошибки выдачи роли игрок не будет зарегистрирован

generator:             # Генератор одноразовых кодов
  login_minimum: 0          # Минимальное рандомное число кода входа
  login_maximum: 9999       # Максимальное рандомное число кода входа
  register_minimum: 9999    # Минимальное рандомное число кода регистрации
  register_maximum: 999999  # Максимальное рандомное число кода регистрации
  code_expired_time: 60     # Время сколько код активен в секундах. После истечения игрока кикает а код удаляется.


bungeecord:            # BungeeCord
  enable: false            # Включёно ли
  server: lobby            # bungeecord сервер куда после авторизации надо переместить игрока

command:               # Комманды
  register_confirm: "confirm" # Команда подтверждения регистрации
  register_cancel: "cancel"   # Команда отмены регистрации

message:
  KICK_PLUGIN_DISABLED: "Вы были кикнуты так как плагин был перезагружен, а вы не залогинены."
  KICK_AUTH_TIMEOUT: "Время авторизации вышло вышло."
  HELLO: "Для регистрации зайдите сюда - https://discord.gg/cool-invite и напишите в Личные Сообщения боту BOT#0001 полученный код"
  LOGIN_SECCU: "Успешная авторизация."
  LOGIN_GIVE_CODE: "Код для входа - $code."
  REGISTER_GIVE_CODE: "Код для регистрации - $code."
  REGISTER_CONFIRM: "Успешная регистрация."
  REGISTER_CANCEL: "Регистрация отменена."
  REGISTER_WARN: "Вы хотите привязать к этому нику аккаунт $discord? Напишите в чат 'confirm' для подтверждения, 'cancel' для отмены."
  REGISTER_CHECK_GAME: "Подтвердите регистрацию в игре."
  REGISTER_ADD_ROLE_MEMBER_NOT_FOUND: "Не удалось обраружить ваш дискорд аккаунт на дискорд сервере для выдачи роли."
  CODE_NOT_FOUND: "Такого кода не существует."
  CODE_USING_E1: "Данный код предназначен не для вашего дискорд аккаунта."
```

## Удачи!
