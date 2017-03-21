(ns gov.stockport.sonar.visualise.state
  (:require [reagent.core :as r]))

(defonce !app (r/atom {}))
(defonce !data (r/atom {}))

(defn initialise! []
  (reset! !app {})
  (reset! !data {}))

