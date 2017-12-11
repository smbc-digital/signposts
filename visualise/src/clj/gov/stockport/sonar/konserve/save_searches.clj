(ns gov.stockport.sonar.visualise.save-searches
  (:require [konserve.indexeddb :refer [[new-fs-store]]]
            [clj-time.core :as time]
            [konserve.core :as k]
            [cljs.core.async.macros :refer [go go-loop]]))


(def store (go (new-fs-store  "c:\\users\\simon.estill")))

(def stored-query {})

(defn store-query[query username]
    (-> store-query
        (assoc  :query query)
        (assoc  :username name)
        (assoc  :time time/now))
    (go (k/assoc-in :stored-query store )))


(defn get-previous-query[user-name]
  (let [results (go (k/get-in store :username))]
    (take 10 (sort-by #(:time %) results))))





