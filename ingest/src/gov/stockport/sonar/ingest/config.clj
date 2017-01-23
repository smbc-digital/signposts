(ns gov.stockport.sonar.ingest.config
  (:require [immuconf.config :as cfg]
            [clojure.java.io :as io])
  (:import (java.io File)))

(defn common-config []
  "config/common.edn")

(defn current-user-name []
  (System/getProperty "user.name"))

(defn user-specific-config []
  (str "config/per-user/" (current-user-name) ".edn"))

(defn available? [file]
  (.exists ^File (io/as-file file)))

(defn prioritised-config-sources []
  (filter available? [(common-config)
                      (user-specific-config)]))

(defn load-config []
  (apply cfg/load (prioritised-config-sources)))

(def !config (atom (load-config)))

(defn reload-config! []
  (reset! !config (load-config)))


