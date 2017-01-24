(ns gov.stockport.sonar.spec.event-spec
  (:require [clojure.spec :as s]))

(s/def ::event (s/keys :req [::event-source ::event-type]
                       :opt [::name ::dob]))

(s/def ::event-source string?)
(s/def ::event-type string?)

