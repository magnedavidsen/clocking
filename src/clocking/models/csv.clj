(ns clocking.models.csv
  (:use [clojure-csv.core])
  (:require
        [clocking.db :as db]
        [clocking.models.events :as events]
   [clj-time.format :as timeformat]
   [clj-time.core :as time]
   [clj-time.local :as timelocal]))

(def time-formatter (timeformat/formatter "HH:mm"))
(def date-formatter (timeformat/formatter "dd.MM.yyyy"))

(defn time-to-csv-time [timestamp]
  (if (nil? timestamp) ""
    (timeformat/unparse time-formatter (time/to-time-zone timestamp (time/default-time-zone)))))

(defn date-to-csv-date [timestamp]
  (if (nil? timestamp) ""
    (timeformat/unparse date-formatter (time/to-time-zone timestamp (time/default-time-zone)))))

(defn interval-in-minutes [clock-in clock-out]
  (if (or (nil? clock-in) (nil? clock-out)) ""
    (if (time/before? clock-out clock-in) ""
      (time/in-minutes (time/interval clock-in clock-out)))))

(defn map-to-string-seq-seq [events]
  (defn stringify-event [event]
    (let [new-map [(:employee_id event)  (date-to-csv-date (:date event)) (time-to-csv-time (:clock-in event))
                   (time-to-csv-time (:clock-out event)) (interval-in-minutes (:clock-in event) (:clock-out event))]]
      (map #(str "" % "") new-map)
      ))
  (map stringify-event events))


(defn generate-csv [events]
  (str
  (write-csv [["Employee id" "Date" "Clocked in" "Clocked out" "Minutes worked"]] :delimiter ";")
  (write-csv (map-to-string-seq-seq events) :delimiter ";")))
