(ns gov.stockport.sonar.visualise.prod
  (:require [gov.stockport.sonar.visualise.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
