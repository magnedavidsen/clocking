(ns clocking.test.models.events
  (:use [clojure.test]
        [clocking.models.events :as events]
        [clj-time.coerce]
        [clocking.db :as db]))

(def event-list
  (map db/convert-date
       [{:id 1, :type "clock-out", :time #inst "2012-10-16T23:15:22.214421000-00:00", :employee_id 100}
        {:id 2, :type "clock-in", :time #inst "2012-10-16T23:49:07.818952000-00:00", :employee_id 100}
        {:id 3, :type "clock-in", :time #inst "2012-10-16T23:49:07.818952000-00:00", :employee_id 100}
        {:id 4, :type "clock-out", :time #inst "2012-10-16T23:49:07.818952000-00:00", :employee_id 100}
        {:id 5, :type "clock-out", :time #inst "2012-10-16T23:49:07.818952000-00:00", :employee_id 100}]))

(def wanted-format
  [[{:type "clock-out", :employee_id 100, :time (from-sql-date  #inst "2012-10-16T23:15:22.214-00:00"), :id 1}]
   [{:type "clock-in", :employee_id 100, :time (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :id 2}]
   [{:type "clock-in", :employee_id 100, :time (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :id 3}
    {:type "clock-out", :employee_id 100, :time (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :id 4}]
   [{:type "clock-out", :employee_id 100, :time (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :id 5}]])

(def one-event
  [(db/convert-date {:type "clock-in", :employee_id 100, :time #inst "2012-10-16T23:49:07.818-00:00", :id 3})])

(def two-events
  (map db/convert-date
       [{:type "clock-in", :employee_id 100, :time #inst "2012-10-16T23:49:07.818-00:00", :id 3}
        {:type "clock-out", :employee_id 100, :time #inst "2012-10-16T23:49:07.818-00:00", :id 4}]))

(def two-events-different-days
  (map db/convert-date
       [{:type "clock-in", :employee_id 100, :time #inst "2012-10-16T23:49:07.818-00:00", :id 3}
        {:type "clock-out", :employee_id 100, :time #inst "2012-10-17T23:49:07.818-00:00", :id 4}]))

(def one-event-flattened
  {:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :clock-in (from-sql-date #inst  "2012-10-16T23:49:07.818-00:00"), :clock-in-id 3})

(def two-events-flattened
  {:employee_id 100,:clock-in-id 3,:clock-out-id 4, :date (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"),  :clock-in (from-sql-date  #inst "2012-10-16T23:49:07.818-00:00"), :clock-out (from-sql-date #inst "2012-10-16T23:49:07.818-00:00")})

(def list-of-flattened-events
  [{:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :clock-in (from-sql-date #inst  "2012-10-16T23:49:07.818-00:00")}
   {:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"),  :clock-in (from-sql-date  #inst "2012-10-16T23:49:07.818-00:00"), :clock-out (from-sql-date #inst "2012-10-16T23:49:07.818-00:00")}
   {:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :clock-out (from-sql-date #inst  "2012-10-16T23:49:07.818-00:00")}])

(def incomplete-events
  [{:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :clock-in (from-sql-date #inst  "2012-10-16T23:49:07.818-00:00")}
   {:employee_id 100, :date (from-sql-date #inst "2012-10-16T23:49:07.818-00:00"), :clock-out (from-sql-date #inst  "2012-10-16T23:49:07.818-00:00")}])

(deftest events-get-paired-correctly
  (is (= wanted-format
         (events/pair-clockins-and-clockouts event-list []))))

(deftest two-events-get-flattened-correctly
  (is (= two-events-flattened
         (events/flatten-two-events two-events))))

(deftest one-event-get-flattened-correctly
  (is (= one-event-flattened
         (events/flatten-two-events one-event))))

(events/flatten-two-events one-event)

(deftest same-day-returns-true
  (is
   (events/same-day? (first two-events) (second two-events))))

(deftest same-day-returns-false
  (is (not
       (events/same-day? (first two-events-different-days) (second two-events-different-days)))))

(deftest proper-pair-returns-true
  (is
   (events/proper-pair? (first two-events) (second two-events))))

(deftest proper-pair-returns-false
  (is (not
       (events/proper-pair? (first one-event) (second one-event)))))

(deftest proper-pair-returns-false-2
  (is (not
       (events/proper-pair? (first event-list) (second event-list)))))

(deftest incomplete-days-returns-correctly
  (is (= incomplete-events
         (events/incomplete-days-in-events list-of-flattened-events))))
