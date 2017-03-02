(ns gov.stockport.sonar.visualise.query.client
  (:require [ajax.core :refer [GET POST]]
            [goog.crypt.base64 :as b64]
            [cljs.core.async :refer [put! chan <! >! timeout]]
            [reagent.core :as r]
            [gov.stockport.sonar.visualise.common.query.base :as qb]
            [gov.stockport.sonar.visualise.common.query.aggregate :as qa]
            [clojure.string :as str])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn ->json [x]
  (.stringify js/JSON (clj->js x)))

(def !creds (atom {:username "elastic" :password "changeme"}))

(defn authorisation-header []
  {"Authorization" (str "Basic " (b64/encodeString (str (:username @!creds) ":" (:password @!creds))))})

(defn query
  ([url handler] (query url nil handler))
  ([url query handler]
   (POST (str "http://localhost:9200" url)
         {:headers         (authorisation-header)
          :format          :json
          :response-format :json
          :keywords?       true
          :handler         (fn [response] (handler response))
          :body            (->json query)})))

(defn search [aquery handler]
  (query "/events-*/_search" aquery handler))

(defn fetch [url]
  (let [chan (timeout 2000)]
    (GET (str "" url)
         {:headers         (authorisation-header)
          :format          :json
          :response-format :json
          :keywords?       true
          :handler         (fn [response] (go (>! chan response)))})
  chan))


(defonce !mappings (r/atom {}))

(defn event-source-and-type []
  (query "/events-*/_search"
         (-> (qb/query)
             (qb/with-no-results)
             (qa/with-term-aggregation :event-source.keyword :event-type.keyword))
         #(reset! !mappings %)))

(defn event-source []
  (query "/events-*/_search"
         {:size 0
          :aggs {:event-sources {:terms {:field :event-source.keyword :size 100}}}}
         #(reset! !mappings %)))


(defn properties [event-type m]
  (let [props (get-in m [:mappings event-type :properties])]
    (map
      (fn [[k v]]
        (str (name k) " [" (:type v) "] "))
      props)))

(defn fields [response]
  (map
    (fn [[idx m]]
      (let [[_ event-source] (str/split (name idx) #"-")
            event-types (keys (:mappings m))]
        (map
          (fn [event-type]
            {:event-source event-source
             :event-type   (name event-type)
             :properties   (properties event-type m)})
          event-types)))
    response))

(defn available-fields []
  (go (fields (<! (fetch "/events-*/_mapping")))))






