(ns clocking.views.employees
  (:require [clocking.views.common :as common]
            [clocking.db :as db]
            [noir.cookies :as cookie]
            [noir.response :as resp])
  (:use [noir.core]
        [hiccup.form]
        [hiccup.page]))


;;TODO let passphrase be property
(pre-route "/admin/*" {}
           (when-not
               (= "vectra" (cookie/get :passphrase))
             (resp/redirect "/login")))

(defn latest-action [employee-id]
     (:time (last (db/most-recent-event employee-id))))

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
   [:td id] [:td name] [:td {:class "timestamp"} (latest-action id)] [:td "Edit / Delete"]])

(defpartial employees-table [employees]
  [:table
   [:tr
    [:th "ID"] [:th "Name"] [:th "Last event"]  [:th "Change"]]
   (map employee-row employees)])

(defpage "/admin/employees" []
  (common/layout "admin"
   [:h1 "Employees"]
   (add-employee-form)
   (employees-table (db/list-all-employees))))

(defpage [:post "/admin/employees/add"] {:as employee}
  (db/create-employee (Integer/parseInt  (:employee-id employee)) (:employee-name employee))
  (render "/admin/employees"))

(defpartial event-row [{:keys [time type]}]
  [:tr
   [:td {:class "date"} time] [:td {:class "time"} time] [:td type]])

(defpage "/admin/employees/:id" {:keys [id]}
  (common/layout "admin"
    [:table
      [:tr
        [:th "Date"] [:th "Time"] [:th "Event" ]]
          (map event-row (db/all-events (Integer/parseInt  id)))]))
