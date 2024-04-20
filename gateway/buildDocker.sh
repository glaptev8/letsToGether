#!/bin/bash
set -e

# Определение версии и времени сборки
VERSION="0.0"
TIMESTAMP=$(date +"%Y%m%d%H%M")

# Сборка и тегирование образа
docker build -t glaptev8/lets-to-gether-gateway:$VERSION .
docker tag glaptev8/lets-to-gether-gateway:$VERSION glaptev8/lets-to-gether-gateway:${VERSION}-$TIMESTAMP

# Публикация образов
docker push glaptev8/lets-to-gether-gateway:$VERSION
docker push glaptev8/lets-to-gether-gateway:${VERSION}-$TIMESTAMP

echo "Образы успешно собраны и отправлены на Docker Hub."
