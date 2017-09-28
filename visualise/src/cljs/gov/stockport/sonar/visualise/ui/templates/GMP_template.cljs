(ns gov.stockport.sonar.visualise.ui.templates.GMP-template
  (:require [reagent.core :as r]
            [cljs-time.format :as f]
            [gov.stockport.sonar.visualise.util.fmt-help :as fh]
            [gov.stockport.sonar.visualise.ui.results.signposting :as s]
            [gov.stockport.sonar.visualise.util.date :as d]
            [clojure.string :as str]
            [gov.stockport.sonar.visualise.data.people :as people]))


(defn ASBO [event]
   [:div.row
     [:div.col-4-sm
         [:div.col-1-sm
         [:strong "Date"]]
         [:div.col-3-sm
          (:time-stamp event)
          ]
      ]
      [:div.col-4-sm
        [:div.col-1-sm
        [:strong "Address"]
         ]
        [:div.col-3-sm
         (:address event)
         ]
      ]
      [:div.col-4-sm
      ]
    ]
)


(defn CAUTION [event]
  [:div.row
   [:div.col-4-sm
    [:div.col-1-sm
     [:strong "Date"]]
    [:div.col-3-sm
     (:time-stamp event)
     ]
    ]
   [:div.col-4-sm
    [:div.col-1-sm
     [:strong "Address"]
     ]
    [:div.col-3-sm
     (:address event)
     ]
    ]
   [:div.col-4-sm
    ]
   ]
  )