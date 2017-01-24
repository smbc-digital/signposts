(ns gov.stockport.sonar.spec.event-spec
  (:require [clojure.spec :as s]
            [gov.stockport.sonar.ingest.util.dates :refer [iso-date-string?]]))

(s/def ::event (s/keys :req [::event-source ::event-type]
                       :opt [::name ::dob]))

(s/def ::event-source string?) ; consider minimum characters for these ?
(s/def ::event-type string?) ; consider minimum characters for these ?
(s/def ::timestamp iso-date-string?)