(ns gov.stockport.sonar.visualise.auth.auth-client-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [stubadub.core :refer [with-stub calls-to]]
            [accountant.core :as accountant]
            [gov.stockport.sonar.visualise.auth.auth-client :as l]))


(deftest auth-client-tests

  (testing "should wire up ajax call and handler to perform login attempt"

    (with-stub
      l/perform-login

       ;when
      (l/attempt-login "some-user" "some-pwd")

      ;then
      (is (= 1 (count (calls-to l/perform-login))))
      (let [[url options] (first (calls-to l/perform-login))]
        (is (= url "/login"))
        (is (= (:body options) "{\"username\":\"some-user\",\"password\":\"some-pwd\"}"))
        (is (= (:handler options) l/handle-login-response)))))

  (testing "should perform client side redirect if login succeeds"

    (with-stub accountant/navigate!
               ;when
               (l/handle-login-response {})
               ;then
               (is (= 1 (count (calls-to accountant/navigate!))))
               (is (= "/" (first (first (calls-to accountant/navigate!)))))


      )


    )

  )