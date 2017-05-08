(ns gov.stockport.sonar.visualise.query.client-tests
  (:require [cljs.test :refer-macros [deftest is testing]]
            [gov.stockport.sonar.visualise.util.ajax :as ajax]
            [gov.stockport.sonar.visualise.query.client :as c]))

(deftest client-tests

  (let [!last-call (atom nil)
        query {:some :query}
        query-results-handler (fn qrh [])]

    (with-redefs
      [ajax/post (fn [& args] (reset! !last-call args))]

      (testing "search"
        (c/search query query-results-handler)
        (let [[url {:keys [body handler response-format keywords?]}] @!last-call]
          (is (= "/query" url))
          (is (= query body))
          (is (= query-results-handler handler))
          (is (= response-format :json))
          (is (= keywords? true)))))

    (with-redefs
      [ajax/post-and-forget (fn [& args] (reset! !last-call args))]

      (testing "keep-alive is fire and forget post with null handler"
        (c/keep-alive)
        (let [[url {:keys [handler]}] @!last-call]
          (is (= "/keep-alive" url))
          (is (= c/null-handler handler)))))))


