#!/bin/bash
set -e

# Определение версии и времени сборки
VERSION="0.0"
TIMESTAMP=$(date +"%Y%m%d%H%M")

# Сборка и тегирование образа
docker build -t glaptev8/lets-to-gether-eureka:$VERSION .
docker tag glaptev8/lets-to-gether-eureka:$VERSION glaptev8/lets-to-gether-eureka:${VERSION}-$TIMESTAMP

# Публикация образов
docker push glaptev8/lets-to-gether-eureka:$VERSION
docker push glaptev8/lets-to-gether-eureka:${VERSION}-$TIMESTAMP

echo "Образы успешно собраны и отправлены на Docker Hub."
