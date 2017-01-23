#!/bin/bash 

read -p "This will drop and recreate your local docker environment and rebuild with fake data.  Are you sure? " -n 1 -r
echo    # (optional) move to a new line
if [[ $REPLY =~ ^[Yy]$ ]]
then
  eval $(docker-machine env)
  cd infra
  docker-compose down
  docker-compose up -d
  cd ../ingest
  lein run -m gov.stockport.sonar.ingest.utils.bootstrap-demo
fi
