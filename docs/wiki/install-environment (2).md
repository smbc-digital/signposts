#Running Development Environment

## Setting up elastic search
From the Sonar console run go.sh or go.cmd this will get the docker images for elastic search and Kibana build them and run them. One problem is that the elastic search image will not run so type `docker ps` to see if the Elastic Search container is running. If not type `docker run infra_elasticsearch` this will start the container. Gor to the ingest folder and type `ingest` this will import dummy data into elastic search. 

##Running the application

In the visualis folder type

`lein figwheel`

This will compile and run the application

type

`localhost:3449 `

in you web browser and the application shouls appear. If there are compilation errors in both the clojure and clojurescipt these will show up here

#Coomon problems


