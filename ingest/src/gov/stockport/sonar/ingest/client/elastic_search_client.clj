(ns gov.stockport.sonar.ingest.client.elastic-search-client
  (:require [clj-http.client :as http]
            [gov.stockport.sonar.spec.event-spec :as es]
            [gov.stockport.sonar.ingest.config :refer [!config]]
            [base64-clj.core :as b64]
            [cheshire.core :refer [generate-string]]
            [clojure.string :as str]
            [gov.stockport.sonar.ingest.util.logging :refer [log]]
            [me.raynes.fs :as fs]
            [clj-time.core :as t]
            [gov.stockport.sonar.ingest.clock :as clock]))

(defn esname [keyword]
  (str/lower-case (name keyword)))

(defn auth-header []
  (let [{:keys [username password]} (:elastic-search @!config)]
    {:Authorization (str "Basic " (b64/encode (str username ":" password)))}))

(defn es-url-for [path]
  (let [{url :url} (:elastic-search @!config)]
    (str url path)))


(defn strip-namespaces [event]
  (reduce merge {} (map (fn [[k v]] {(keyword (name k)) v}) event)))

(defn event-in-bulk-format [index-naming-fn {:keys [::es/event-type] :as event}]
  (str
    (generate-string {:index {:_index (index-naming-fn event) :_type (esname event-type)}})
    "\n"
    (generate-string (strip-namespaces event))
    "\n"))

(defn events-in-bulk-format [index-naming-fn list-of-events]
  (str (apply str (map (partial event-in-bulk-format index-naming-fn) list-of-events)) "\n"))

(defn post-bulk-data-to-es [bulk-data]
  (http/post (es-url-for "/_bulk")
             {:headers (auth-header)
              :body    bulk-data}))

(defn bulk-index-list [index-naming-fn list-of-events]
  (let [payload (events-in-bulk-format index-naming-fn list-of-events)]
    (let [start-time (clock/now)]
      (post-bulk-data-to-es payload)
      (log "processed chunk [" (count list-of-events) " in " (t/in-millis (t/interval start-time (clock/now))) " ms]"))))

(defn bulk-index-old
  ([index-naming-fn events]
   (let [batch-size (:batch-size @!config)]
     (doall
       (map
         (partial bulk-index-list index-naming-fn)
         (partition batch-size batch-size nil events))))))

(defn ->elastic-search [{:keys [feed-hash valid-events] :as feed}]
  (if (first valid-events)
    (let [index-name
          (str/join "-" ["events" (esname (::es/event-source (first valid-events))) feed-hash])]
      (bulk-index-old (fn [_] index-name) valid-events)
      (assoc feed :index-name index-name))))



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