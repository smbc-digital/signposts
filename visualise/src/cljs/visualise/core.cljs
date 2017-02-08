(ns visualise.core
  (:require [reagent.core :as reagent :refer [atom]]
            [visualise.v1 :as v]
            [visualise.query.client]ajax.core))

(defn home-page []
  [v/home-page])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))