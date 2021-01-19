```yml
bot_token: ''         // Сюда надо ввести токен бота дискорд.
ip_saving_type: 0     // 0 - Сохранить IP после выхода игрока; 1 - Сохранить IP при входе.
ip_expired_time: 60   // На сколько секунд сохранять ip игрока, 0 для выключения.
generator:
  login_minimum: 0
  login_maximum: 1000
  register_minimum: 1000
  register_maximum: 10000
  code_expired_time: 35
bungeecord:
  enable: false
  server: lobby
message:
  KICK_PLUGIN_DISABLED: Вы были кикнуты так как плагин был перезагружен, а вы не залогинены.
  KICK_AUTH_TIMEOUT: Время регистрации вышло.
  HELLO: Приветственное сообщение.
  LOGIN_SECCU: Успешная авторизация.
  LOGIN_GIVE_CODE: Код для входа - $code.
  REGISTER_GIVE_CODE: Код для регистрации - $code.
  REGISTER_CONFIRM: Успешная регистрация.
  REGISTER_CANCEL: Регистрация отменена.
  REGISTER_WARN: Вы хотите привязать к этому нику аккаунт $discord? Напишите в чат
    'confirm' для подтверждения, 'cancel' для отмены.
  REGISTER_CHECK_GAME: Подтвердите регистрацию в игре.
  CODE_NOT_FOUND: Такого кода не существует.
```
