(ns gov.stockport.sonar.visualise.util.ajax-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [stubadub.core :refer [with-stub calls-to]]
            [reagent.cookies :refer [get-raw]]
            [gov.stockport.sonar.visualise.util.navigation :as n]
            [gov.stockport.sonar.visualise.util.ajax :as l]))

(def some-handler (fn []))

(deftest ajax-tests

  (testing "should invoke post against supplied url, with default options, json body and supplied handler"

    (with-stub
      l/perform-post

      ;when
      (l/post "/some-url" {:body    {:some "body"}
                           :handler some-handler})

      ;then
      (is (= 1 (count (calls-to l/perform-post))))
      (let [[url options] (first (calls-to l/perform-post))]
        (is (= url "/some-url"))
        (is (= (:headers options) {"Content-Type" "application/json"}))
        (is (= (:format options) :json))
        (is (= (:body options) "{\"some\":\"body\"}"))
        (is (= (:handler options) some-handler))
        (is (= (:error-handler options) l/default-error-handler)))))

  (testing "should include csrf anti-forgery-token, url-decoded, if it exists"

    (with-redefs
      [get-raw (fn [cookie-name] (when (= cookie-name :csrf) "some%2fvalue"))]

      (with-stub
        l/perform-post

        ;when
        (l/post "/some-url" {:body    {:some "body"}
                             :handler some-handler})

        ;then
        (is (= 1 (count (calls-to l/perform-post))))
        (let [[_ options] (first (calls-to l/perform-post))]
          (is (= (:headers options) {"Content-Type" "application/json"
                                     "X-CSRF-Token" "some/value"}))))))

  (testing "default error handling navigates to login page on 401"

    (let [calls (atom 0)]
      (with-redefs [n/navigate-to-login-page (fn [] (swap! calls inc) nil)]

                   (is (nil? (l/default-error-handler {:status 401})))

                   (is (= 1 @calls)))))

  (testing "default error handling returns response when not 401"

    (let [calls (atom 0)]
      (with-redefs [n/navigate-to-login-page (fn [] (swap! calls inc) nil)]

                   (is (= (l/default-error-handler {:status 403}) {:status 403}))

                   (is (= 0 @calls))))))