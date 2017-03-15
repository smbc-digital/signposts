(ns gov.stockport.sonar.auth.session-manager-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.auth.session-manager :as s]
            [buddy.core.codecs.base64 :as b64]))

(facts "about session manager"

       (fact "create session returns unique session identifier"
             (let [session-1 (s/create-session {})
                   session-2 (s/create-session {})]
               (= session-1 session-2) => false))

       (fact "returns nil credentials for non-existant session"
             (s/get-credentials ..session..) => nil)

       (fact "given existing session retrieves credentials"

             (with-redefs [b64/encode (fn [creds]
                                        (when (= creds "the-username:the-password")
                                          (.getBytes "based64-encoded-username-and-password" "UTF-8")))]
               (let [session (s/create-session {:username "the-username" :password "the-password"})]
                 (s/get-credentials session) => "Basic based64-encoded-username-and-password")))

       (fact "sessions based on the same username and password produce the same credentials"
             (let [session-1 (s/create-session {:username "U" :password "P"})
                   session-2 (s/create-session {:username "U" :password "P"})]
               (= session-1 session-2) => false
               (= (s/get-credentials session-1) (s/get-credentials session-2)) => true))

       )