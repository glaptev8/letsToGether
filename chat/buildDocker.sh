#!/bin/bash
set -e

# Определение версии и времени сборки
VERSION="0.0"
TIMESTAMP=$(date +"%Y%m%d%H%M")

# Сборка и тегирование образа
docker build -t glaptev8/lets-to-gether-chat:$VERSION .
docker tag glaptev8/lets-to-gether-chat:$VERSION glaptev8/lets-to-gether-chat:${VERSION}-$TIMESTAMP

# Публикация образов
docker push glaptev8/lets-to-gether-chat:$VERSION
docker push glaptev8/lets-to-gether-chat:${VERSION}-$TIMESTAMP

echo "Образы успешно собраны и отправлены на Docker Hub."
