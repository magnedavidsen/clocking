(ns clocking.test.models.events
  (:use [clojure.test]
        [clocking.models.events :as events]))

(def event-list [
                     {:id 1, :type "clock-out", :time #inst "2012-10-16T23:15:22.214421000-00:00", :employee_id 100}
                     {:id 2, :type "clock-in", :time #inst "2012-10-16T23:49:07.818952000-00:00", :employee_id 100}
                     {:id 3, :type "clock-in", :time #inst "2012-10-16T23:49:07.818952000-00:00", :employee_id 100}
                     {:id 4, :type "clock-out", :time #inst "2012-10-16T23:49:07.818952000-00:00", :employee_id 100}])

(def wanted-format [
                    [{:type "clock-out", :employee_id 100, :time #inst "2012-10-16T23:15:22.214-00:00", :id 1}]
                    [{:type "clock-in", :employee_id 100, :time #inst "2012-10-16T23:49:07.818-00:00", :id 2}]
                    [{:type "clock-in", :employee_id 100, :time #inst "2012-10-16T23:49:07.818-00:00", :id 3} {:type "clock-out", :employee_id 100, :time #inst "2012-10-16T23:49:07.818-00:00", :id 4}]])

(deftest first-test
  (is (= wanted-format
         (events/pair-clockins-and-clockouts event-list []))))
