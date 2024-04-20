# Использование более нового или соответствующего вашим требованиям базового образа
FROM openjdk:21-jdk-slim-bullseye

# Установка рабочей директории и копирование файла в одном слое
WORKDIR /lets_to_gether_chat/build
COPY build/libs/chat-0.0.1-SNAPSHOT.jar .

# Объявление порта, который будет использоваться приложением
EXPOSE 8080

# Запуск Eureka сервера
ENTRYPOINT ["java", "-jar", "chat-0.0.1-SNAPSHOT.jar"]