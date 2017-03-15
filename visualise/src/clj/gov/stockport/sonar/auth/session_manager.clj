(ns gov.stockport.sonar.auth.session-manager
  (:require [buddy.core.codecs.base64 :as b64])
  (:import (java.util UUID)))

(def store (atom {}))

(defn- es-auth-header [{:keys [username password]}]
  (str "Basic " (String. ^bytes (b64/encode (str username ":" password)) "UTF-8")))

(defn create-session [creds]
  (let [session (UUID/randomUUID)]
    (swap! store assoc session (es-auth-header creds))
    session))

(defn get-credentials [session]
  (get @store session))