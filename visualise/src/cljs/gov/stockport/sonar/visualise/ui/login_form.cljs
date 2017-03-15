(ns gov.stockport.sonar.visualise.ui.login-form
  (:require [reagent.core :as r]
            [gov.stockport.sonar.visualise.auth.auth-client :as login]))

(def initial-state {:username "" :password ""})

(defn login-form []
  (let [!local (r/atom initial-state)]
    (fn []
      [:div
       [:input {:type        :text
                :value       (:username @!local)
                :on-change   #(swap! !local assoc :username (-> % .-target .-value))
                :placeholder "username"}]
       [:input {:type        :password
                :value       (:password @!local)
                :on-change   #(swap! !local assoc :password (-> % .-target .-value))
                :placeholder "password"}]
       [:button {:on-click (fn [] (let [creds @!local]
                                    (reset! !local initial-state)
                                    (login/attempt-login creds)))} "Login"]])))
