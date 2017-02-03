(ns gov.stockport.sonar.ingest.elastic.client-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.ingest.elastic.client :as client]
            [cheshire.core :as c]
            [clj-http.client :as http]))

(facts "about bulk uploads"

       (fact "should ignore no data"
             (client/post-bulk-data "") => {})

       (fact "should report time taken, status, and failures"
             (let [data {:some "data"}]
               (client/post-bulk-data data) => {:took-ms          50
                                                :status           200
                                                :records-indexed  2
                                                :records-rejected 1
                                                :indexes          #{"index-1"}}
               (provided
                 (client/es-url-for "/_bulk") => "/es/_bulk"
                 (client/auth-header) => {:Authorization ""}
                 (http/post "/es/_bulk" {:headers {:Authorization ""}
                                         :body    data}) => {:status 200
                                                             :body   (c/generate-string {:took  50
                                                                                         :items [{:index {:_index "index-1"}} {} {:error {}}]})}))))

