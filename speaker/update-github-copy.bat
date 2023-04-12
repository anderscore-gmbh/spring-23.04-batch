@echo off
rem Erstellt eine Kopie dieses Repositories zur Veroeffentlichung auf GitHub

set CLONE_DIR=C:\Code\github\spring-batch-deep-dive
set SOURCE_DIR=..

xcopy /s /d /y /exclude:exclude.txt %SOURCE_DIR% %CLONE_DIR%\

xcopy /d /y %SOURCE_DIR%\slides\target\generated-docs\spring-batch-deep-dive-handout.pdf %CLONE_DIR%\Handout.pdf
xcopy /s /d /y %SOURCE_DIR%\slides\target\generated-slides %CLONE_DIR%\generated-slides\
