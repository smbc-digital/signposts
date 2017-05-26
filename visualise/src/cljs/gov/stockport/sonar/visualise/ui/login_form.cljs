(ns gov.stockport.sonar.visualise.ui.login-form
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.auth.auth-client :refer [login]]))

(def initial-state {:username "" :password ""})

(defn- perform-login [!local]
  (let [creds @!local]
    (reset! !local initial-state)
    (login creds)))

(defn login-form []
  (let [!local (r/atom initial-state)]
    (fn []
      [:div.form
       [:div.form-group
        [:label "Username"
         [:input.form-control.col-sm-12
          {:type      :text
           :autoFocus "autofocus"
           :value     (:username @!local)
           :on-change #(swap! !local assoc :username (-> % .-target .-value))}]]]
       [:div.form-group
        [:label "Password"
         [:input.form-control.col-sm-9
          {:type      :password
           :value     (:password @!local)
           :on-change #(swap! !local assoc :password (-> % .-target .-value))
           :on-key-up #(when (= 13 (-> % .-keyCode)) (perform-login !local))}]]]
       [:button.btn.btn-primary
        {:on-click (fn [] (perform-login !local))} "Login"]])))

