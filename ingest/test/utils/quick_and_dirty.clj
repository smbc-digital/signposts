(ns quick-and-dirty
  (:require [fake-data :as fd]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojure.string :as str]
            [ingest.core :as ic]
            [ingest.config :refer [!config]]))

(defn connect [{:keys [url username password]}]
  (esr/connect url {:basic-auth [username password]}))

(def !conn (atom (connect (:elastic-search @!config))))

(defn esname [keyword]
  (str/lower-case (name keyword)))

(defn write-to-index [{:keys [event-type event-source] :as event}]
  (esd/create @!conn (str "feed_" (esname event-source)) (esname event-type) event))

(defn push-some-data []
  (map write-to-index (ic/exclusion-events-in-es-format-with-event-source)))

(defn page-some-data []
  (map #(println %) (take 10 (ic/exclusion-events-in-es-format-with-event-source))))

(defn push-some-fake-data []
  (map write-to-index (take 5000 (fd/timelines))))