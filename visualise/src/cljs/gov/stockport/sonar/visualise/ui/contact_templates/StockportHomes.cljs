(ns gov.stockport.sonar.visualise.ui.contact-templates.StockportHomes
  (:require
    [gov.stockport.sonar.visualise.util.fmt-help :as fh]))

  ;;Tcy Refno:68499 Acc Balance:-848.53 5-week arrers
  ;;Tcy Refno:64627 AEW Date:-30-MAY-17
  ;;Tcy Refno:68506 NSP Date:-03-AUG-17
  (defn- format-other-info [event]
    (let  [tenancy-ref (re-find #"\d{5,}" (:otherinfo event))]
      (let [date (re-find #"\d{2}\-[A-Za-z]{3}\-\d{2}"  (:otherinfo event))]
        (let [amount (re-find #"\-[\d\.]+" (:otherinfo event))]
          (when tenancy-ref
            (let  [tenancy-ref-text (str "Tenancy Reference: " tenancy-ref)]
              [:div tenancy-ref-text [:br]
               (when amount
                 [:strong "Account Balance:"] amount)
               (when (and date (= "homes-evictions" (:event-type event) )
                          [:strong "AEW Date:"] date)
                 )
               (when (and date (= "notice-possesion" (:event-type event) )
                          [:strong "NSP Date: "] date)
                 )
               ] ))))))

  (defn- middle-column[event]
           [:div.col.col-md-4
            [:div.row
             [:div.col.col-md-3
              [:strong "Address"]]
             [:div.col.col-md-9
              (:address event)]]])

  (defn- right-column[event]
    [:div.col.col-md-4
     [:div.row
      [:div.col.col-md-3
       [:strong "Key worker"]]
      [:div.col.col-md-9
       (:keyworker event)]]
     (when(some? (:otherinfo event ))
     [:div.row
      [:div.col.col-md-3
       [:strong "Other Information"]]
      [:div.col.col-md-9
       (:otherinfo event)]])])

  (defn arrears-6-wk[event]
     [:div
      [:h4   "Stockport Homes " [:span.not-bold "6 Week Arrears"]]
      [:div.row {:class "cin"}
      [:div.col.col-md-4
       [:div.row
        [:div.col.col-md-3
         [:strong "Open Date"]]
        [:div.col.col-md-9
         (:timestamp (fh/unparse-timestamp event))]]
       [:div.row
        [:div.col.col-md-3
         [:strong "National Insurance"]
         ]
        [:div.col.col-md-9
         (:nino event)
         ]]
       ]
      (middle-column event)
      (right-column event)]])


  (defn asb[event]
        [:div
         [:h4   "Stockport Homes " [:span.not-bold "Anti Social Behaviour"]]
         [:div.row {:class "cin"}
          [:div.col.col-md-4
           [:div.row
            [:div.col.col-md-3
             [:strong "Individual Id"]]
            [:div.col.col-md-9
             (:individual-id event)]]
           [:div.row
            [:div.col.col-md-3
             [:strong "Open Date"]]
            [:div.col.col-md-9
             (:timestamp (fh/unparse-timestamp event))]]
           [:div.row
            [:div.col.col-md-3
             [:strong "Close Date"]]
            [:div.col.col-md-9
             (fh/close-date(:date-completed event))]]]
          (middle-column event)
          (right-column event)]])

  (defn eviction-application[event]
        [:div
         [:h4   "Stockport Homes " [:span.not-bold "Eviction Application"]]
         [:div.row {:class "cin"}
          [:div.col.col-md-4
           [:div.row
            [:div.col.col-md-3
             [:strong "Open Date"]]
            [:div.col.col-md-9
             (:timestamp (fh/unparse-timestamp event))]]]
          (middle-column event)
          (right-column event)]])

  (defn notice-seeking-possession[event]
        [:div
         [:h4   "Stockport Homes " [:span.not-bold "Notice Seeking Possesion"]]
         [:div.row {:class "cin"}
          [:div.col.col-md-4
           [:div.row
            [:div.col.col-md-3
             [:strong "Open Date"]]
            [:div.col.col-md-9
             (:timestamp (fh/unparse-timestamp event))]]           ]
          (middle-column event)
          (right-column event)]])