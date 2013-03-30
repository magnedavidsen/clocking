(ns clocking.client.common
  (:require [goog.i18n.DateTimeFormat]
            [goog.date.Date]))

(def date-formatter (new goog.i18n.DateTimeFormat "dd/MM/yyyy"))
(def time-formatter (new goog.i18n.DateTimeFormat "HH:mm"))

;;TODO generalize to fix everything with a :date-field
(defn convert-date-to-goog [event]
  (let [date (new goog.date.Date)]
    (.set date (:date event))
    {:clock-in (:clock-in event) :clock-out (:clock-out event) :date date}))

(defn minutes-between [clock-in clock-out]
  (when (and clock-in clock-out)
    (let [one-min (* 1000 60)]
      (.round js/Math
              (/ (- (.getTime clock-out) (.getTime clock-in)) one-min)))))

 (defn format-minutes [minutes]
  (let [remainder (mod minutes 60)]
    (let [hours (/ (- minutes remainder) 60)]
         (str hours "h " remainder "m"))))

(defn sum-hours [events]
  (reduce + (map #(minutes-between (:clock-in %) (:clock-out %)) events)))
