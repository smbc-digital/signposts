(ns gov.stockport.sonar.visualise.handler-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.visualise.handler :as h]))

(facts "about middleware to require no caching"
       (let [wrapped-handler (h/wrap-nocache (fn [_] {:some :response}))]
         (wrapped-handler irrelevant)) => {:some    :response
                                           :headers {"Cache-Control" "no-cache, no-store, must-revalidate"
                                                     "Pragma"        "no-cache"
                                                     "Expires"       "0"}})