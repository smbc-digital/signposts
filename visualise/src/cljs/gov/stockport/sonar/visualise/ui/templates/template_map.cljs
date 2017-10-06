(ns gov.stockport.sonar.visualise.ui.templates.template-map
  (:require
    ;;Template Name spaces
    [gov.stockport.sonar.visualise.ui.templates.CareFirst :as cf]
    [gov.stockport.sonar.visualise.ui.templates.Charity :as ch]
    [gov.stockport.sonar.visualise.ui.templates.EIS :as eis]
    [gov.stockport.sonar.visualise.ui.templates.EIS :as eis]
    [gov.stockport.sonar.visualise.ui.templates.GMP :as gmp]
    [gov.stockport.sonar.visualise.ui.templates.Homes :as ho]
    [gov.stockport.sonar.visualise.ui.templates.RevsBens :as rb]
    [gov.stockport.sonar.visualise.ui.templates.Schools :as sch]
    [gov.stockport.sonar.visualise.ui.templates.Yos :as yos]
    ;;Other Namespaces
    [clojure.string :as s]
    [gov.stockport.sonar.visualise.util.fmt-help :as fh]
  ))

(defn- make-template-key [event]
  (s/lower-case(str (:event-source event) "-" (:event-type event)))
  )

(defn default-template [event]
  [:div
  [:h4   (:event-source event) [:span {:style {:font-weight "normal"}}" "  (:event-type event)]]
  [:div.row {:class (make-template-key event) }
   [:div.col..col-4-sm
    [:div.row
     [:div.col.col-6-sm
      [:strong.label "Date issued"]]
     [:div.col-6-sm
      (:timestamp (fh/unparse-timestamp event))
      ]]
    ]
   [:div.col.col-4-sm
    [:div.row
     [:div.col-1-sm
      [:strong "Address"]
      ]
     [:div.col-3-sm
      (:address event)
      ]]
    ]
   [:div.col.col-4-sm
    [:div.row
     [:div.col.col-4-sm
      [:strong "Postcode"]]
     [:div.col.col-8-sm
      (:postcode event)]]
    ]]])

(def templates
  {
   :carefirst-contact  cf/contact
   :carefirst-service-agreement cf/service-agreement
   :eis-cin    eis/cin
   :eis-contact eis/contact
   :eis-lac eis/lac
   :eis-sen eis/sen
   :gmp-asbo gmp/asbo
   :gmp-caution gmp/caution
   :gmp-domestic gmp/domestic
   :homes-arrears-6-wk ho/arrears-6-wk
   :homes-asb ho/asb
   :homes-eviction-application ho/eviction-application
   :homes-notice-seeking-possession ho/notice-seeking-posession
   :revben-ctax-bill rb/ctax-bill
   :revbens-cts-only rb/ct-support
   :revbens-hb-cts rb/hb-cts
   :schools-attendance sch/attendance
   :schools-exclusion sch/exclusions
   :schools-registrations sch/registrations
   :yos-non-statutory-intervention yos/non-statutory-intervention
   :yos-statutory-intervention yos/statutory-intervention
   }
  )


(defn get-template[event]
  (let [template-key (keyword (s/lower-case(str (:event-source event) "-" (:event-type event))))]
    (template-key templates default-template)))
