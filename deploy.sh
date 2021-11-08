#!/bin/sh
./mvnw clean package jib:dockerBuild
docker compose up -d