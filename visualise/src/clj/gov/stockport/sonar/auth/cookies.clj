(ns gov.stockport.sonar.auth.cookies
  (:require [gov.stockport.sonar.visualise.middleware :refer [secure-cookies]]))

(defn add-cookie [response name value]
  (assoc-in response [:cookies name] {:value     value
                                      :secure    secure-cookies}))

(defn add-http-cookie [response name value]
  (assoc-in response [:cookies name] {:value     value
                                      :secure    secure-cookies
                                      :http-only true}))
