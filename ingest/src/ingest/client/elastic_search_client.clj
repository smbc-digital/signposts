(ns ingest.client.elastic-search-client
  (:require [clj-http.client :as http]
            [ingest.config :refer [!config]]
            [base64-clj.core :as b64]
            [cheshire.core :refer [generate-string]]
            [clojure.string :as str]))

(defn esname [keyword]
  (str/lower-case (name keyword)))

(defn auth-header []
  (let [{:keys [username password]} (:elastic-search @!config)]
    {:Authorization (str "Basic " (b64/encode (str username ":" password)))}))

(defn es-url-for [path]
  (let [{url :url} (:elastic-search @!config)]
    (str url path)))

(defn event-in-bulk-format [{:keys [event-source event-type] :as event}]
  (str
    (generate-string {:index {:_index (str "feed_" (esname event-source)) :_type (esname event-type)}})
    "\n"
    (generate-string event)
    "\n"))

(defn events-in-bulk-format [list-of-events]
  (str (apply str (map event-in-bulk-format list-of-events)) "\n"))

(defn bulk-index-list [list-of-events]
  (let [payload (events-in-bulk-format list-of-events)]
    (println "adding records " (count list-of-events))
    (http/post (es-url-for "/_bulk")
               {:headers (auth-header)
                :body    payload})))

(defn bulk-index [events]
  (let [batch-size 10000]
    (doall (map bulk-index-list (partition batch-size batch-size nil events)))))

(defn post-json-to-es [{:keys [path payload]}]
  (http/post (es-url-for path)
             {:content-type :json
              :headers      (auth-header)
              :body         (generate-string payload)}))

(defn delete-to-es [{:keys [path]}]
  (http/delete (es-url-for path)
               {:headers (auth-header)}))

(defn ping []
  (try
    (println "ping es to see if it is awake yet...")
    (:status (http/get (es-url-for "/")
                       {:headers (auth-header)}))
    (catch Exception _)))

