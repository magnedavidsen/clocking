(ns clocking.views.employees
  (:require [clocking.views.common :as common]
            [clocking.db :as db])
  (:use [noir.core]
        [hiccup.form]
        [hiccup.page]))


(defn latest-action [employee-id]
     (:time (first (db/most-recent-event employee-id))))

(defpartial add-employee-form []
  (form-to {:autocomplete "off"} [:post "/employees/add"]
           [:span {:class "label-input-row"}
            (label "employee-id" "id: ")
            (text-field {:class "employee-id" :maxlength "3"} "employee-id")]

           [:span {:class "label-input-row"}
            (label "employee-name" "name: ")
            (text-field {:class "employee-name"} "employee-name")]

           (submit-button {:class "add-employee"}  "Add")))

(defpartial employee-row [{:keys [id name]}]
  [:tr
   [:td id] [:td name] [:td  (latest-action id)] [:td "Edit / Delete"]])

(defpartial employees-table [employees]
  [:table
   [:tr
    [:th "ID"] [:th "Name"] [:th "Last event"]  [:th "Change"]]
   (map employee-row employees)])

(defpage "/employees" []
  (common/layout "admin"
   [:h1 "Employees"]
   (add-employee-form)
   (employees-table (db/list-all-employees))))

(defpage [:post "/employees/add"] {:as employee}
  (db/create-employee (Integer/parseInt  (:employee-id employee)) (:employee-name employee))
  (render "/employees"))

(defpage "/employees/:id" []
  [:h1 "Employee"])
