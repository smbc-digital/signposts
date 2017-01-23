(ns gov.stockport.sonar.ingest.faking.phone
  (:require [gov.stockport.sonar.ingest.faking.helpers :as h]))

(defn phone-number []
  (h/make "0161 ### ####"))