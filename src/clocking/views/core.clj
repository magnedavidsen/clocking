(ns clocking.views.core
  (:require [clocking.views.common :as common]
            [noir.content.getting-started]
            [noir.validation :as vali]
            [clocking.db :as db])
  (:use [noir.core]
        [hiccup.form]))

(defpage "/" []
         (common/layout "index"
           [:h1 "clocking.in"]
           (form-to {:autocomplete "off"} [:post "/clockin"]
                    [:div {:class "label-input-row"}
                     (label "employee-id" "id: ")
                     (text-field {:class "employee-id" :maxlength "3" :onchange "setHiddenField()"} "employee-id")]
                    (submit-button {:class "clock-in"} "clock in"))
           (form-to [:post "/clockout"]
                    (hidden-field "hidden-employee-id")
                    (submit-button {:class "clock-out"} "clock out"))))

(defpage [:post "/clockin"] {:keys [employee-id]}
  (db/create-event "clock-in" (Integer/parseInt employee-id))
  (common/layout
   "index"
   [:p employee-id  " has been clocked in."]))

(defpage [:post "/clockout"] {:keys [hidden-employee-id]}
  (db/create-event "clock-out" (Integer/parseInt hidden-employee-id))
  (common/layout
   "index"
   [:p hidden-employee-id " has been clocked out."]))
