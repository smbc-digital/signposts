(ns gov.stockport.sonar.visualise.auth.auth-client-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [stubadub.core :refer [with-stub calls-to]]
            [accountant.core :as accountant]
            [gov.stockport.sonar.visualise.auth.auth-client :as l]
            [gov.stockport.sonar.visualise.state :as s]
            [secretary.core :as secretary]))

(deftest auth-client-tests

  (testing "should wire up ajax call and handler to perform login attempt"

    (with-stub
      l/perform-post

      ;when
      (l/attempt-login {:username "some-user" :password "some-pwd"})

      ;then
      (is (= 1 (count (calls-to l/perform-post))))
      (let [[url options] (first (calls-to l/perform-post))]
        (is (= url "/login"))
        (is (= (:body options) "{\"username\":\"some-user\",\"password\":\"some-pwd\"}"))
        (is (= (:handler options) l/handle-login-response)))))

  (testing "should perform client side redirect to home page if login succeeds"

    (with-stub
      accountant/navigate!

      ;when
      (l/handle-login-response {})

      ;then
      (is (= 1 (count (calls-to accountant/navigate!))))
      (is (= "/" (first (first (calls-to accountant/navigate!)))))))

  (testing "should wire up ajax call and handler for logout"

    (with-stub
      l/perform-post

      ;when
      (l/logout)

      ;then
      (is (= 1 (count (calls-to l/perform-post))))
      (let [[url options] (first (calls-to l/perform-post))]
        (is (= url "/logout"))
        (is (= (:handler options) l/handle-logout-response)))))

  (testing "should perform client side redirect to login page if logout succeeds"

    (let [initialise-counter (atom 0)]
      (with-redefs
        [s/initialise! (fn [] (swap! initialise-counter inc))]

        (with-stub
          accountant/navigate!

          ;when
          (l/handle-logout-response {})

          ;then
          (is (= 1 (count (calls-to accountant/navigate!))))
          (is (= "/login" (first (first (calls-to accountant/navigate!)))))
          (is (= 1 @initialise-counter))))))


  (testing "should redirect to login when receiving 401 response from server"

    (let [initialise-counter (atom 0)]
      (with-redefs
        [s/initialise! (fn [] (swap! initialise-counter inc))]

        (with-stub
          accountant/navigate!

          ;when
          (= nil (l/error-handler {:status 401}))

          ;then
          (is (= 1 (count (calls-to accountant/navigate!))))
          (is (= "/login" (first (first (calls-to accountant/navigate!)))))
          (is (= 1 @initialise-counter))))))

  (testing "should return underlying response from server if not 401"

    (let [initialise-counter (atom 0)]
      (with-redefs
        [s/initialise! (fn [] (swap! initialise-counter inc))]

        (with-stub
          accountant/navigate!

          ;when
          (is (= {:status 500} (l/error-handler {:status 500})))

          ;then
          (is (= 0 (count (calls-to accountant/navigate!))))
          (is (= 0 @initialise-counter)))))))