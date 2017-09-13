(ns gov.stockport.sonar.auth.cookies-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.visualise.middleware :as middleware]
            [gov.stockport.sonar.auth.cookies :as c]))

(facts "when adding a cookie"

       (fact "it takes the secure-cookies setting into account"
             (with-redefs [middleware/secure-cookies true]
               (c/add-cookie {:some :response} "name" "value") => {:some    :response
                                                                   :cookies {"name" {:value  "value"
                                                                                     :secure true}}})

             (with-redefs [middleware/secure-cookies false]
               (c/add-cookie {:some :response} "name" "value") => {:some    :response
                                                                   :cookies {"name" {:value  "value"
                                                                                     :secure false}}}))

       (fact "it sets http only if requested"
             (with-redefs [middleware/secure-cookies false]
               (c/add-http-cookie {:some :response} "name" "value") => {:some    :response
                                                                        :cookies {"name" {:value     "value"
                                                                                          :secure    false
                                                                                          :http-only true}}})))