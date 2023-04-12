@echo off
title prometheus
cd C:\Tools\prometheus-2.8.0.windows-amd64
prometheus.exe --config.file=%~dp0\prometheus.yml