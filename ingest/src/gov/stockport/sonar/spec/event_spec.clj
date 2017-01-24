(ns gov.stockport.sonar.spec.event-spec
  (:require [clojure.spec :as s])
  (:import (org.joda.time DateTime)))

(s/def ::event (s/keys :req [::event-source ::event-type]
                       :opt [::name ::dob]))

(s/def ::event-source string?) ; consider minimum characters for these ?
(s/def ::event-type string?) ; consider minimum characters for these ?
;(s/def ::timestamp #(instance? DateTime %))

