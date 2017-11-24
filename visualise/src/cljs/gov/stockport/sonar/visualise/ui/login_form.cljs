(ns gov.stockport.sonar.visualise.ui.login-form
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.auth.auth-client :refer [login]]
            [gov.stockport.sonar.visualise.state :refer [!login-error]]
            [hodgepodge.core :refer [local-storage clear!]]
            ))

(def initial-state {:username "" :password ""})

(defn- perform-login [!local]
  (let [creds @!local]

    (reset! !local initial-state)
    (login creds)

      ))

(defn login-form []
  (let [!local (r/atom initial-state) login-message (:login-message local-storage) login-error (:login-error local-storage)  ]
    (fn []
      [:div.login-form-container
       [:div.form-group
        (if (> login-error 0 )
          [:p#login-error {:style {:color "rgba(191,125,3,1)" :font-weight "bold" :text-align "left" :font-size "0.9em"}}
           [:i.fa.fa-exclamation-triangle {:style{:font-size "1.5em" :margin-left "10px" :margin-right "20px"}}]
           login-message])
         [:div.input-group.addon
         [:div.input-group-addon
           [:i.fa.fa-user]
          ]
         [:input.form-control.col-12
          {
           :type :text
           :placeholder "Username"
           :autoFocus "autofocus"
           :value     (:username @!local)
           :on-change #(swap! !local assoc :username (-> % .-target .-value))}]]]
       [:div.form-group
        [:div.input-group.addon
         [:div.input-group-addon
          [:i.fa.fa-lock]
          ]
         [:input.form-control.col-12
          {:type :password
           :placeholder "Password"
           :value     (:password @!local)
           :on-change #(swap! !local assoc :password (-> % .-target .-value))
           :on-key-up #(when (= 13 (-> % .-keyCode)) (perform-login !local))}]]]
       [:div.form-group
       [:button.btn.btn-primary.col-12
        {:on-click (fn [] (perform-login !local))} "LOG IN"]]

       [:p {:style {:text-align"center" :font-size "0.8em"}} [:a {:href "mailto:ITHelpDesk@solutionssk.co.uk "} "Forgot your password? Contact the help desk" ]]

       ])))

