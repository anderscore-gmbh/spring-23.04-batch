@echo off
cd C:\Tools\prometheus-2.37.0.windows-amd64
prometheus.exe --config.file=%~dp0prometheus.yml