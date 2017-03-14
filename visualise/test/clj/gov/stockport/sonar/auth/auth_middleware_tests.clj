(ns gov.stockport.sonar.auth.auth-middleware-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.auth.auth-middleware :as am]))


(fact "include middleware to read token into an auth header"

      (let [wrapped-request (atom nil)
            handler (am/wrap-raise-auth-token-from-cookies-to-header (fn [req] (reset! wrapped-request req)))]
        ;when
        (handler {:cookies {"token" {:value "some-token-value"}}})
        ;then
        (:headers @wrapped-request) => {"Authorization" "Token some-token-value"}))

