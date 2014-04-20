(ns clocking.client.common
  (:require [goog.i18n.DateTimeFormat]
            [goog.date.Date]))

(def date-formatter (new goog.i18n.DateTimeFormat "dd/MM/yyyy"))
(def date-formatter-link (new goog.i18n.DateTimeFormat "yyyyMMdd"))
(def time-formatter (new goog.i18n.DateTimeFormat "HH:mm"))

;; TODO remove hackish solution employee_id/employee-id
(defn convert-date-to-goog [event]
  (let [date (new goog.date.Date)]
    (.set date (:date event))
    (assoc event :date date :employee-id (:employee_id event))))

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

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))
