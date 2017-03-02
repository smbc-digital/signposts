(ns gov.stockport.sonar.visualise.cards.explore
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [cljsjs.flot]
            [cljsjs.flot.plugins.time]
            [cljs-time.core :as t]
            [cljs-time.periodic :as p]
            [cljs.core.async :refer [put! chan <! >! timeout]]
            [gov.stockport.sonar.visualise.query.client :as client])
  (:require-macros
    [devcards.core
     :as dc
     :refer [defcard defcard-doc defcard-rg deftest]]
    [cljs.core.async.macros :refer [go]]))

(defcard-doc
  "
  ##exploration
  ")

(defn some-component []
  (let [!state (atom {:some "edn"})]
    (go (reset! !state (<! (client/available-fields))))
    (fn []
      (devcards.util.edn-renderer/html-edn @!state))))

(defcard-rg available-data
            [some-component])


