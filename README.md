# OpenDiscordAuth
Авторизация игроков майнкрафт на сервере, путём привязки дискорд аккаунтов.

**Для работы обязательно нужен bungeecord.**

## config.yml
```yml
redirect_server: 'lobby'
bot_token: ''
ip_expired_delay: 7200 # Через данное кол-во секунд ip игрока будет удалён, и ему заного придётся подтвердить вход. 

code_generator:
  login:
    expired_delay: 30
    min: 0
    max: 999

  register:
    expired_delay: 60
    min: 100000
    max: 999999

messages:
  hello_message: null
  register_confirm: "&aВы хотите привязать аккаунт &b%DISCORD%&a? Что-бы подтвердить это действие напишите в чат confirm. Для отмены cancel. Это действие перестанет работать через 1 минуту или после отключения от сервера."
  confirm_register_in_game: 'Подтвердите регистрацию в игре.'
  login_confirm: '&aВход разрешён.'
  start_register_message: "&eНапишите в лс боту код %CODE% для регистрации. Код будет удалён через 60 секунд."
  start_login_message: "&eНапишите в лс боту код %CODE% для входа. Код будет удалён через 30 секунд."
  code_not_found: 'Такой команды или кода подтверждения не существует.'
  connecting_to_server: '&aУспешный вход. Подключение...'
  register_cancel: '&cРегистрация аккаунта &b%DISCORD%&c к этому нику отменена.'
  register_successfully: '&aРегистрация аккаунта &b%DISCORD%&a к этому нику успешна.'
```

**Пожалуйста если при запуске вам выдаёт ошибку, а в config.yml в messages нету hello_message, то добавте это в ручную!**
