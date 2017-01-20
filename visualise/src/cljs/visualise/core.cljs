(ns visualise.core
  (:require [reagent.core :as reagent :refer [atom]]
            [visualise.v1 :as v]))

(defn home-page []
  [v/home-page])

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [home-page] (.getElementById js/document "app")))

(defn init! []
  (mount-root))