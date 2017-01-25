(ns gov.stockport.sonar.spec.event-spec
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.ingest.util.dates :refer [iso-date-string? dmy-date-string?]]))

(s/def ::event (s/keys :req [::event-source ::event-type ::timestamp]
                       :opt [::name ::dob]))

(s/def ::event-source string?) ; consider minimum characters for these ?
(s/def ::event-type string?) ; consider minimum characters for these ?
(s/def ::timestamp (s/or ::timestamp-dmy dmy-date-string?
                         ::timestamp-iso iso-date-string?))
(s/def ::name string?)

;(s/def ::dob (s/nilable dmy-string?))

(def explainer (partial s/explain-str ::event))