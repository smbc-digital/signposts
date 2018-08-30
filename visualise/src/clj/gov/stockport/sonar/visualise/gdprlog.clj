(ns gov.stockport.sonar.visualise.gdprlog
  (:require
            [clojure.java.jdbc :refer :all]))

  (def db
       {:classname   "org.sqlite.JDBC"
        :subprotocol "sqlite"
        :subname     "db/query-log.db"})


  (defn log-query [user query ]
        (insert! db :query-log {:user user :query query}))

(defn query-log [user]
  (query db ["SELECT  User,Query,QueryDateTime FROM QueryLog WHERE user = ? order by QueryDateTime DESC LIMIT 10"  user]))