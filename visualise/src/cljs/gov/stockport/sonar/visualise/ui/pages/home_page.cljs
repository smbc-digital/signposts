(ns gov.stockport.sonar.visualise.ui.pages.home-page
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.query.handler :as h]
            [gov.stockport.sonar.visualise.ui.search.search-control :as nsc]
            [gov.stockport.sonar.visualise.ui.results.tabbed-results :as tr]
            [gov.stockport.sonar.visualise.ui.results.individual-cards :as ic]
            [gov.stockport.sonar.visualise.auth.auth-client :as ac]
            [gov.stockport.sonar.visualise.state :refer [!app !data]]
            [gov.stockport.sonar.visualise.ui.busy :as busy]
            [gov.stockport.sonar.visualise.data.people :as people]
            [gov.stockport.sonar.visualise.state :refer [!search-control-state]]
            [gov.stockport.sonar.visualise.ui.search.search-control-state :as scs]
            [gov.stockport.sonar.visualise.ui.components.welcome-status :refer [welcome-message]]))


(comment(def !seconds-left (r/atom 3600))

(def timer-id (r/atom 0))

(defn logout-timer[]
         (js/setTimeout ac/logout (* @!seconds-left 1000)))

(defn- reset-timer[id]
  (js/clearTimeout id)
  (reset! timer-id (logout-timer))
  )

(defn results [!data]
  [:div.container-fluid
   [:div.row.no-gutters
    [:div.col-3
     [ic/cards !data]]
    [:div.col-9
     [tr/results-tab !data]]]])

(defn- set-event-handlers[]
  (set! (.-onkeypress js/document) (reset-timer @timer-id))
  (set! (.-onmousedown js/document) (reset-timer @timer-id))
  (set! (.-onmousewheel js/document) (reset-timer @timer-id))
  ))

(defn home-page []
  [:div
   (set-event-handlers)
   [:div {:style {:display "none"}}
   (busy/overlay)
   [:div.container-fluid
    {:style {:background-color "#1c3645" :color :white}}
    [:div.row.align-items-center.py-1
     [:div.column.col-1
      [:div.row.justify-content-center
       [:i.fa.fa-map-signs.fa-2x]]]
     [:div.column.col-10
      [:span.h2 "SIGNPOSTS "]]
     [:div.column.col-1
      [:button.btn.btn-primary {:on-click ac/logout} "Logout"]]]]

   [nsc/new-search-control (h/default-handler !data)]

   (when (not (nil? (:total @!data)))
     [:div.container-fluid
      {:style {:background-color "#1d2932"}}
      [:h6.text-white.pb-1 (people/results-summary @!data)]])

   (if (not-empty (:people @!data))
     [results !data]
     [welcome-message])])