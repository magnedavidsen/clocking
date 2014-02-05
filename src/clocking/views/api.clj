(ns clocking.views.api
  (:use [compojure.core]
        [ring.adapter.jetty :as ring]
        )
  (:require
   [clj-time.coerce :as time]
   [clocking.models.events :as events]
   [clocking.db :as db]
   [clocking.models.csv :as csv]))


;todo, writer smarter
(defn convert-date [event]
  {:clock-in (time/to-date (:clock-in event)) :clock-out (time/to-date (:clock-out event)) :date (time/to-date (:date event)) :employee-id (:employee_id event)})

(defn get-all-events [employee-id]
  (map convert-date
       (events/get-all-events-for-employee employee-id)))


(defn get-all-incomplete []
  (defn get-all-events-per-employee []
    (map #(get-all-events (:id %)) (db/list-all-employees)))

  (flatten
   (map events/incomplete-days-in-events (get-all-events-per-employee))))

(defn save-event [event]
  (let [time (time/from-string (:time event))]
    (println (time/to-timestamp time))
    (db/save-event {:type (:type event) :employee-id (:employee-id event) :time (time/to-timestamp time)}))
  "OK")

(defn generate-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/edn"}
   :body (pr-str data)})

(defroutes handler
  (GET "/event/:id" [id] (generate-response (get-all-events (Integer/parseInt id))))
  (GET "/event/:id/report.csv" [id] {:status 200
                                     :headers {"Content-Type" "file/csv"}
                                     :body (csv/generate-csv (events/get-all-events-for-employee (Integer/parseInt id)) )})
  (POST "/event" {edn-params :edn-params} (generate-response (save-event edn-params)))
  (GET "/incomplete" [] (generate-response (get-all-incomplete))))


