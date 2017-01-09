@echo off
echo This will drop and recreate your local docker environment and rebuild with fake data.  Are you sure?
set INPUT=
set /P INPUT=Type 'yes': %=%
If /I "%INPUT%"=="yes" goto meat
echo Cancelled
goto end

:meat

cd infra
docker-compose down
docker-compose up -d
cd ../ingest
lein run -m ingest.utils.bootstrap-demo
cd ..


:end