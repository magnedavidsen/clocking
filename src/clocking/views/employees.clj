(ns clocking.views.employees
  (:require [clocking.views.common :as common]
            [clocking.db :as db]
            [clocking.models.events :as events]
            [clocking.models.employees :as employee]
            [noir.cookies :as cookie]
            [noir.response :as resp]
            [cheshire.core :refer :all]
            )
  (:use [noir.core]
        [hiccup.form]
        [hiccup.page]
        [hiccup.element]))


;;TODO let passphrase be property
(pre-route "/admin/*" {}
           (when-not
               (= "vectra" (cookie/get :passphrase))
             (resp/redirect "/login")))

(defpartial add-employee-form []
  (form-to {:autocomplete "off"} [:post "/admin/employees/add"]
           [:span {:class "label-input-row"}
            (label "employee-id" "id: ")
            (text-field {:class "employee-id" :maxlength "3"} "employee-id")]

           [:span {:class "label-input-row"}
            (label "employee-name" "name: ")
            (text-field {:class "employee-name"} "employee-name")]

           (submit-button {:class "add-employee"}  "Add")))

(defpartial employee-row [{:keys [id name]}]
  [:tr
   [:td id] [:td {:class (if (employee/working-now? id) "online icon" "offline icon")} "&#128100;"] [:td name] [:td {:class "timestamp"} (:time (db/most-recent-event id))]
   [:td (link-to (str  "/admin/employees/" id) "Report")]])

(defpartial employees-table [employees]
  [:table
   [:tr
   [:th "ID"] [:th ""] [:th "Name"] [:th "Last event"] [:th ""]]
   (map employee-row employees)])

(defpage "/admin/employees" []
  (common/layout "admin"
   [:h1 "Employees"]
   (add-employee-form)
   (employees-table (db/list-all-employees))))

(defpage [:post "/admin/employees/add"] {:as employee}
  (db/create-employee (Integer/parseInt  (:employee-id employee)) (:employee-name employee))
  (render "/admin/employees"))

(defpartial event-row [{:keys [date clock-in clock-out]}]
  [:tr
   [:td {:class "date"} date] [:td {:class "time"} clock-in]
   [:td {:class "time"} clock-out][:td {:class "interval-minutes"} (events/time-between-timestamps clock-in clock-out)]])

(defpage "/admin/employees/:id" {:keys [id]}
  (let [id-int (Integer/parseInt id)]
    (common/layout "admin"
                   [:h1 (:name (first  (db/get-employee id-int)))]
                   [:table
                    [:tr
                     [:th "Date"] [:th "Clocked in"] [:th "Clocked out" ] [:th "Sum"]]
                    (map event-row (events/get-all-events-for-employee id-int))])))

(defpage "/admin/json/employees/:id" {:keys [id]}
  (let [id-int (Integer/parseInt id)]
    (generate-string
     (events/get-all-events-for-employee id-int))))
