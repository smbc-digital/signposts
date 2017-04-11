(ns gov.stockport.sonar.visualise.util.ajax-tests
  (:require [cljs.test :refer-macros [deftest testing is are use-fixtures]]
            [reagent.cookies :as rc]
            [gov.stockport.sonar.visualise.util.navigation :as n]
            [gov.stockport.sonar.visualise.util.ajax :as l]))

(def some-handler (fn [& _]))

(deftest ajax-tests

  (testing "should invoke post against supplied url, with default options, json body and supplied handler"

    (let [calls (atom nil)]

      (with-redefs
        [l/perform-post (fn [& args] (reset! calls args))]

        ;when
        (l/post "/some-url" {:body    {:some "body"}
                             :handler some-handler})

        ;then
        (let [[url options] @calls]
          (is (= url "/some-url"))
          (is (= (:headers options) {"Content-Type" "application/json"}))
          (is (= (:format options) :json))
          (is (= (:body options) "{\"some\":\"body\"}"))
          (is (= (:handler options) some-handler))
          (is (= (:error-handler options) l/default-error-handler))))))

  (testing "should include csrf anti-forgery-token, url-decoded, if it exists"

    (let [calls (atom nil)]

      (with-redefs
        [l/perform-post (fn [& args] (reset! calls args))
         rc/get-raw (fn [cookie-name] (when (= cookie-name :csrf) "some%2fvalue"))]

        ;when
        (l/post "/some-url" {:body    {:some "body"}
                             :handler some-handler})

        ;then
        (let [[_ options] @calls]
          (is (= (:headers options) {"Content-Type" "application/json"
                                     "X-CSRF-Token" "some/value"}))))))

  (testing "default error handling navigates to login page on error status >= 400"

    (let [calls (atom 0)]
      (with-redefs [n/navigate-to-login-page (fn [] (swap! calls inc) nil)]

                   (is (nil? (l/default-error-handler {:status 401})))

                   (is (= 1 @calls)))))

  (testing "default error handling returns response when not 401"

    (let [calls (atom 0)]
      (with-redefs [n/navigate-to-login-page (fn [] (swap! calls inc) nil)]

                   (is (= (l/default-error-handler {:status 201}) {:status 201}))

                   (is (= 0 @calls)))))

  )