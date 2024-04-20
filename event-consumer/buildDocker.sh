#!/bin/bash
set -e

# Определение версии и времени сборки
VERSION="0.0"
TIMESTAMP=$(date +"%Y%m%d%H%M")

# Сборка и тегирование образа
docker build -t glaptev8/lets-to-gether-event-consumer:$VERSION .
docker tag glaptev8/lets-to-gether-event-consumer:$VERSION glaptev8/lets-to-gether-event-consumer:${VERSION}-$TIMESTAMP

# Публикация образов
docker push glaptev8/lets-to-gether-event-consumer:$VERSION
docker push glaptev8/lets-to-gether-event-consumer:${VERSION}-$TIMESTAMP

echo "Образы успешно собраны и отправлены на Docker Hub."
