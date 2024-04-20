# Определение базового образа с Node.js
FROM node:lts as build-stage

# Установка рабочей директории в контейнере
WORKDIR /app

# Копирование файлов `package.json` и `yarn.lock`
COPY package.json yarn.lock ./

# Установка зависимостей с использованием yarn
RUN yarn install

# Копирование остальных файлов проекта
COPY . .

# Сборка приложения
RUN yarn build

# Финальный этап с Nginx, для раздачи статических файлов
FROM nginx:stable-alpine as production-stage

# Копирование собранного приложения из предыдущего этапа
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Открытие 80 порта для доступа к приложению
EXPOSE 80

# Запуск Nginx
CMD ["nginx", "-g", "daemon off;"]