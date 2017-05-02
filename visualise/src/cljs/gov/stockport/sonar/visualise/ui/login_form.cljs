(ns gov.stockport.sonar.visualise.ui.login-form
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.auth.auth-client :refer [login]]))

(def initial-state {:username "" :password ""})

(defn- perform-search [!local]
  (let [creds @!local]
    (login creds)
    (reset! !local initial-state)))

(defn login-form []
  (let [!local (r/atom initial-state)]
    (fn []
      [:div.form
       [:div.form-group
        [:label "Username"
         [:input.form-control.col-sm-12
          {:type      :text
           :value     (:username @!local)
           :on-change #(swap! !local assoc :username (-> % .-target .-value))}]]]
       [:div.form-group
        [:label "Password"
         [:input.form-control.col-sm-9
          {:type      :password
           :value     (:password @!local)
           :on-change #(swap! !local assoc :password (-> % .-target .-value))
           :on-key-up #(when (= 13 (-> % .-keyCode)) (perform-search !local))}]]]
       [:button.btn.btn-primary
        {:on-click (fn [] (perform-search !local))} "Login"]])))