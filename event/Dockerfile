# Использование более нового или соответствующего вашим требованиям базового образа
FROM openjdk:21-jdk-slim-bullseye

# Установка рабочей директории и копирование файла в одном слое
WORKDIR /lets_to_gether_event/build

COPY build/libs/event-0.0.1-SNAPSHOT.jar .
COPY ./../wait-for-it.sh /usr/wait-for-it.sh

EXPOSE 8080

# Запуск Eureka сервера
ENTRYPOINT ["/bin/bash", "-c", "for i in {1..3}; do /usr/wait-for-it.sh lets-to-gether-authentication-service:8080 --timeout=45 --strict && break || echo 'Retrying ($i)...'; done && java -jar event-0.0.1-SNAPSHOT.jar"]