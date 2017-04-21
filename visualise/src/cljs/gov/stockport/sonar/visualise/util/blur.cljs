(ns gov.stockport.sonar.visualise.util.blur)

; this code is used to help the ui when multiple events occur on the same day for the same event type
; the event type y-axis is typically represented by a whole number e.g 2
; to show 3 events on the same day for an event with y-axis value of 2 we 'blur' the number 2 so that the events
; are distributed vertically around the 2 position e.g. 1.9 2.0 2.1
; this distribution in intended to make it possible to discern and select from the different events

; for some data sets e.g. council tax bills in the name of smith, there can be a large number of collisions
; so you can bound the effect
; e.g. (blurrer 0.05 0.4) provides a function that will separate events by 0.05 around the given number but will also
; but will also ensure that no number is more than 0.4 from the given number (using a floor / ceiling approach)
; this is a compromise; the user should narrow their search to focus on less individuals

(defn- limiter-fn [number limit]
  (if limit
    (let [lower (- number limit)
          upper (+ number limit)]
      (fn [value]
        (if (< value number)
          (max lower value)
          (min upper value))))
    identity))

(defn blurrer [amount & [limit]]
  (fn [number number-of-distinct-values]
    (let [start (- number (* amount (/ (- number-of-distinct-values 1) 2)))
          limiter (limiter-fn number limit)]
      (map (comp float limiter) (take number-of-distinct-values (range start 100 amount))))))
