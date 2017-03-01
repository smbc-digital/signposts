(ns visualise.esproxy-tests
  (:require [midje.sweet :refer :all]
            [visualise.esproxy :as ep]))

(fact "should handle posts to elastic search"
      (ep/proxied-request {:request-method :post
                           :content-type   "application/json"
                           :uri            "/elasticsearch/some/path"
                           :body           (.getBytes "{}")})
      => {:uri            "http://localhost:9200/some/path"
          :content-type   "application/json"
          :request-method :post
          :body           "{}"
          :headers        ..auth-header..}
      (provided
        (ep/authorisation-header) => ..auth-header..))


