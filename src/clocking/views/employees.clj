(ns clocking.views.employees
  (:require [clocking.views.common :as common]
            [clocking.db :as db])
  (:use [noir.core]
        [hiccup.form]
        [hiccup.page]))

(defpartial add-employee-form []
  (form-to {:autocomplete "off"} [:post "/employees/add"]
           (label "id" "Employee ID: ")
           (text-field {:class "employee-id"} "id")
           (label "name" "Name: ")
           (text-field "name")
           (submit-button "Add employee")))

(defpartial employee-row [{:keys [id name]}]
  [:tr
   [:td id] [:td name]])

(defpartial employees-table [employees]
  [:table
   [:tr
    [:th "Employee ID"] [:th "Name"]]
   (map employee-row employees)])

(defpage "/employees" []
  (common/layout
   (add-employee-form)
   (employees-table (db/list-all-employees))))

(defpage [:post "/employees/add"] {:as employee}
  (db/create-employee (Integer/parseInt  (:id employee)) (:name employee))
  (render "/employees"))
