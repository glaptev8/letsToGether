#!/bin/bash
set -e

# Определение версии и времени сборки
VERSION="0.0"
TIMESTAMP=$(date +"%Y%m%d%H%M")

# Сборка и тегирование образа
docker build -t glaptev8/lets-to-gether-event:$VERSION .
docker tag glaptev8/lets-to-gether-event:$VERSION glaptev8/lets-to-gether-event:${VERSION}-$TIMESTAMP

# Публикация образов
docker push glaptev8/lets-to-gether-event:$VERSION
docker push glaptev8/lets-to-gether-event:${VERSION}-$TIMESTAMP

echo "Образы успешно собраны и отправлены на Docker Hub."
