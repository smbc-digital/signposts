(ns gov.stockport.sonar.auth.crypto-tests
  (:require [midje.sweet :refer :all]
            [gov.stockport.sonar.auth.crypto :as c]))

(fact "should provide random key with encrypted content"
      (let [{:keys [secret-key ciphertext]} (c/encrypt "something secret")]
        (c/decrypt secret-key ciphertext) => "something secret"))

(fact "secret keys are auto-generated and different"
      (let [{secret-key-1 :secret-key ciphertext-1 :ciphertext} (c/encrypt "something secret")
            {secret-key-2 :secret-key ciphertext-2 :ciphertext} (c/encrypt "something secret")]
        (= secret-key-1 secret-key-2) => false
        (= ciphertext-1 ciphertext-2) => false
        (= (c/decrypt secret-key-1 ciphertext-1) (c/decrypt secret-key-2 ciphertext-2)) => true))

(fact "attempt to decrypt without correct key results in exception"
      (let [{:keys [ciphertext]} (c/encrypt "something secret")]
        (c/decrypt "some-incorrect-key" ciphertext) => (throws Exception)))
