(ns visualise.common.ui.colors-tests
  (:require [midje.sweet :refer :all]
            [visualise.common.ui.colors :as c]))

(fact "returns color by index or black"
      (c/color 0) => :red
      (c/color 5) => :purple
      (c/color 6) => :black)