(ns clocking.test.models.events
  (:use [clojure.test]
        [clocking.models.events :as events]))

(defonce event-pair [{:id 1, :type "clock-in", :time #inst "2012-10-16T23:15:22.214421000-00:00", :employee_id 100} {:id 2, :type "clock-out", :time #inst "2012-10-16T23:49:07.818952000-00:00", :employee_id 100}])

(defonce wanted-format {:employee_id 100, :clock-in-time #inst "2012-10-16T23:15:22.214421000-00:00", :clock-out-time  "2012-10-16T23:49:07.818952000-00:00"})

(events/pair-clockins-and-clockouts event-pair)
