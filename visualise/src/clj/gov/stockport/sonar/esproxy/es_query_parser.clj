(ns gov.stockport.sonar.esproxy.es-query-parser
  (:require
            [clojure.string :as str]))

  (def es-query {:should :boolean})

  (defn query-tokens [query]
        (str/split query #"\s+")
        )

  (defn token-count [query]
    (count (query-tokens query))
  )

  (defn wild-card? [token]
       (not (nil? (re-matches #"^[\w\'\-]+\*.*$" token )))
  )

  (defn parse-token
    [field token]
    (if (wild-card? token)
      {:wildcard {field token}}
      {:match {field token}}
    ))

  (defn parse-tokens [term value]
     (let [tokens (query-tokens value)]
      (map #(parse-token term %) tokens)
    ))

(defn parse-query [term value]
           {:should (parse-tokens term value)
            :minimum_should_match (token-count value)
            }

  )