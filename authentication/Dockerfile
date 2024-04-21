# Использование более нового или соответствующего вашим требованиям базового образа
FROM openjdk:21-jdk-slim-bullseye

# Установка рабочей директории в контейнере
WORKDIR /lets_to_gether_authentication

# Копирование исполняемого JAR файла и скрипта wait-for-it.sh в контейнер
COPY build/libs/authentication-0.0.1-SNAPSHOT.jar .
COPY wait-for-it.sh /usr/wait-for-it.sh

# Делаем скрипт wait-for-it.sh исполняемым
RUN chmod +x /usr/wait-for-it.sh

# Объявление порта, который будет использоваться приложением
EXPOSE 8080

# Запуск приложения после успешного ответа от необходимого сервиса
ENTRYPOINT ["/bin/bash", "-c", "for i in {1..3}; do /usr/wait-for-it.sh lets-to-gether-config:8080 --timeout=45 --strict && break || echo 'Retrying ($i)...'; done && java -jar authentication-0.0.1-SNAPSHOT.jar"]
