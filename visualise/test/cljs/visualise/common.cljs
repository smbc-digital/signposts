(ns visualise.common
  (:require [reagent.core :refer [render]]))

(def ^:dynamic c)

(defn ->render [component]
  (render component c))

