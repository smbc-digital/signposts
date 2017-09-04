(ns gov.stockport.sonar.auth.cookies-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.visualise.middleware :as middleware]
            [gov.stockport.sonar.auth.cookies :as c]))

(facts "about adding a cookie"

       (with-redefs [middleware/secure-cookies true]
         (c/add-cookie {:some :response} "name" "value") => {:some    :response
                                                             :cookies {"name" {:value  "value"
                                                                               :secure true}}})

       (with-redefs [middleware/secure-cookies false]
         (c/add-cookie {:some :response} "name" "value") => {:some    :response
                                                             :cookies {"name" {:value  "value"
                                                                               :secure false}}}))
