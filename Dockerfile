FROM rabbitmq:3-management

# Установка утилиты wget для загрузки плагина
RUN apt-get update && apt-get install -y wget

# Загрузка плагина отложенных сообщений
RUN wget https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.13.0/rabbitmq_delayed_message_exchange-3.13.0.ez -O /opt/rabbitmq/plugins/rabbitmq_delayed_message_exchange-3.13.0.ez

# Перемещение плагина в директорию плагинов и его активация
RUN rabbitmq-plugins enable rabbitmq_delayed_message_exchange
