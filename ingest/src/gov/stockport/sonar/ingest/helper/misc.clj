(ns gov.stockport.sonar.ingest.helper.misc
  (:require [gov.stockport.sonar.ingest.helper.logging :refer [log]]))

(defmacro quietly [form]
  `(try
     ~form
     (catch Exception _# nil)))