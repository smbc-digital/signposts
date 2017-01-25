(ns gov.stockport.sonar.ingest.util.misc
  (:require [gov.stockport.sonar.ingest.util.logging :refer [log]]))

(defmacro quietly [form]
  `(try
     ~form
     (catch Exception _# nil)))