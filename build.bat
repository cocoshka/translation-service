@echo off
./mvnw clean package jib:dockerBuild
