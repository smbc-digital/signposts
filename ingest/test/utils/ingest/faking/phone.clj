(ns ingest.faking.phone
  (:require [ingest.faking.helpers :as h]))

(defn phone-number []
  (h/make "0161 ### ####"))