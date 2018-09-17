(ns gov.stockport.sonar.esproxy.es-search-history
  (:require
    [clojure.java.jdbc :refer :all])
  (:import (sun.font TrueTypeFont$DirectoryEntry)))

  (def db
       {:classname   "org.sqlite.JDBC"
        :subprotocol "sqlite"
        :subname     "db\\visualise.db"})


(defn get-last-query [user]
  (query db ["SELECT  Query FROM QueryLog WHERE User = ? ORDER BY Timestamp DESC LIMIT 1"  user]))


(defn are-not-the-same [query user]
  (if (not= (get-last-query user) query)
     true
     false))

  (defn log-query [user query]
    (if (are-not-the-same user query)
    (insert! db :querylog {:user user :query query})))



  (defn query-search-history [user]
     (query db ["SELECT  Query FROM QueryLog WHERE User = ? ORDER BY Timestamp DESC LIMIT 10"  user]))


  (defn parse-dob[query-field]
    (if (= (:field-type query-field ) "date-of-birth")
      {:query-type "dob" :search-term (:query query-field)}
      {:query-type (:field-type query-field)  :search-term (:query query-field)}))

  (defn extract-query-field[query-field]
    (if(= (:field-name query-field) "dob")
        (parse-dob query-field)
        {:query-type (:field-name query-field) :search-term (:query query-field)}))


 (defn extract-query-fields[query]
    (let [query-fields (read-string (:query query))]
      (map extract-query-field query-fields)))

  (defn get-search-history [user]
    {:search-history (map extract-query-fields (query-search-history user))})