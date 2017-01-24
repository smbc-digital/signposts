(ns gov.stockport.sonar.ingest.client.elastic-search-client
  (:require [clj-http.client :as http]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [base64-clj.core :as b64]
            [cheshire.core :refer [generate-string]]
            [clojure.string :as str]
            [me.raynes.fs :as fs]))

(defn esname [keyword]
  (str/lower-case (name keyword)))

(defn auth-header []
  (let [{:keys [username password]} (:elastic-search @!config)]
    {:Authorization (str "Basic " (b64/encode (str username ":" password)))}))

(defn es-url-for [path]
  (let [{url :url} (:elastic-search @!config)]
    (str url path)))

(defn event-in-bulk-format [index-naming-fn {:keys [event-type] :as event}]
  (str
    (generate-string {:index {:_index (index-naming-fn event) :_type (esname event-type)}})
    "\n"
    (generate-string event)
    "\n"))

(defn events-in-bulk-format [index-naming-fn list-of-events]
  (str (apply str (map (partial event-in-bulk-format index-naming-fn) list-of-events)) "\n"))

(defn bulk-index-list [index-naming-fn list-of-events]
  (let [payload (events-in-bulk-format index-naming-fn list-of-events)]
    (println "adding records " (count list-of-events))
    (http/post (es-url-for "/_bulk")
               {:headers (auth-header)
                :body    payload})))

(defn bulk-index
  ([events] (bulk-index (fn [event] (str "feed_" (esname (:event-source event)))) events))
  ([index-naming-fn events]
   (let [batch-size 100000]
     (doall (map (partial bulk-index-list index-naming-fn) (partition batch-size batch-size nil events))))))

(defn bulk-index-new [{:keys [file event-list] :as feed}]
  (let [index-name (str/join "-" ["events" (esname (:event-source (first event-list))) (fs/mod-time file)])]
    (bulk-index (fn [_] index-name) event-list)
    (assoc feed :index-name index-name)))

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

(defn query [query-url]
  (cheshire.core/parse-string
    (:body (http/get (es-url-for query-url)
                     {:content-type :json
                      :headers      (auth-header)}))
    true))

(defn delete [query-url]
  (http/delete (es-url-for query-url)
               {:headers (auth-header)}))

(defn try-delete [query-url]
  (try
    (http/delete (es-url-for query-url)
                 {:headers (auth-header)})
    (catch Exception _)))
