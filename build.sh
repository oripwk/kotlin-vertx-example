#!/bin/sh

./gradlew clean installDist
docker build -t $(basename $PWD) .