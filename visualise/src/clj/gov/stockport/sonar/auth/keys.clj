(ns gov.stockport.sonar.auth.keys
  (:import (java.security KeyPairGenerator KeyPair)))

(defn ^KeyPairGenerator key-pair-generator []
  (doto
    ; RSA / [B]ouncy [C]astle Provider
    (KeyPairGenerator/getInstance "RSA" "BC")
    (.initialize 2048)))

(def ^KeyPair key-pair
  (delay
    (.generateKeyPair (key-pair-generator))))

(def privkey (delay (.getPrivate @key-pair)))
(def pubkey (delay (.getPublic @key-pair)))