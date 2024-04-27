#!/bin/bash
set -e

# Определение версии и времени сборки
VERSION="0.0"
TIMESTAMP=$(date +"%Y%m%d%H%M")

# Сборка и тегирование образа
docker build --platform linux/amd64 -t glaptev8/lets-to-gether-ui:$VERSION .
docker tag glaptev8/lets-to-gether-ui:$VERSION glaptev8/lets-to-gether-ui:${VERSION}-$TIMESTAMP

# Публикация образов
docker push glaptev8/lets-to-gether-ui:$VERSION
docker push glaptev8/lets-to-gether-ui:${VERSION}-$TIMESTAMP

echo "Образы успешно собраны и отправлены на Docker Hub."
