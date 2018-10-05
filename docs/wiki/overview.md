# OverView

Signpost is a system for keeping track of children and vulnerable adults. Councils store large amounts of data on these people however it is stored on different systems.  The point of Signposts is to get this data on a single system and make it searchable.

## Ingest

Ingest loads the data into [ElasticSearch](https://www.elasticsearch.com). There are '.csv' files which are extracts from the relevant applications. These a parsed into json and posted to an instance of elastic search.  

## Elastic Search

This is essentially a HTTP/Rest interface to the Lucene indexing engine. The data is indexed in this application and is searchable. Authentication is supplied by Active Directory and can fine tuned to only allow access to certain indexes by AD Group

## Visualise Back End.

This is the proxy/ circuit breaker for the application. It takes Http/Rest  requests from the front end and translates them to Http/Rest Get requests for the Elastic search engine. It then takes the results and passes them to the front end. It also keeps logs of searches in a Sqlite database. This retrieves the last 10 user searches. It also handles authentication.

It is written in Clojure  which runs on Java VM and compiles to Java bytecode. It requires Java 8.

## Visualise

The front end is written in ClojureScript. This is Clojure that compiles to javaScript. The front end uses Reagent. This is React in ClojureScript syntax. This sends ajax requests to the Visualise Back end and generates the respective HTML from the results.






