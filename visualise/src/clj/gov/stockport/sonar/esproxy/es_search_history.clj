(ns gov.stockport.sonar.esproxy.es-search-history
  (:require
            [clojure.java.jdbc :refer :all]))

  (def db
       {:classname   "org.sqlite.JDBC"
        :subprotocol "sqlite"
        :subname     "db\\visualise.db"})


  (defn log-query [user query ]
        (insert! db :querylog {:user user :query query}))

  (defn query-search-history [user]
     (query db ["SELECT  Query FROM QueryLog WHERE User = ? ORDER BY Timestamp DESC LIMIT 10"  user]))

  (defn extract-query-field[query-field]
    {:query-type (:field-name query-field) :search-term (:query query-field)})

 (defn extract-query-fields[query]
    (let [query-fields (read-string (:query query))]
      (map extract-query-field query-fields)))

  (defn get-search-history [user]
    {:search-history (map extract-query-fields (query-search-history user))})