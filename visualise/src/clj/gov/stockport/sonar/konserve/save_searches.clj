(ns gov.stockport.sonar.visualise.save-searches
  (:require [konserve.indexeddb :refer [[new-fs-store]]]
            [clj-time.core :as time]
            [konserve.core :as k]
            [cljs.core.async.macros :refer [go go-loop]]))


(def store (go (new-fs-store  "c:\\users\\simon.estill")))

(def stored-query {})

(defn store-query[query username]
    (assoc stored-query :query query)
    (assoc stored-query :username name)
    (assoc stored-query :time time/now)
    (go (k/assoc-in :stored-query store stored-query)))


(defn get-previous-query[user-name]
  (let [results (go (k/get-in store :username))]
    (take 5 (sort-by #(:time %) results))))





