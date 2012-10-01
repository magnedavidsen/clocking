(ns tidsur.views.core
  (:require [tidsur.views.common :as common]
            [noir.content.getting-started]
            [noir.validation :as vali])
  (:use [noir.core :only [defpage]]
  		[hiccup.form]))

(defn valid? [employee_id]
  (vali/rule (vali/min-length? employee_id 3)
             [:employee_id "Your first name must have more than 3 numbers."])
  (not (vali/errors? :employee_id)))

(defpage "/" []
         (common/layout
           [:p "Tidsur"]
           (form-to [:post "/checkin"]
               (text-field "employee_id")
               (submit-button "Checkin"))))

(defpage [:post "/checkin"] {:keys [employee_id]}
	(if (valid? employee_id)
    (common/layout
      [:p "You tried to checkin as " employee_id])    
    (common/layout
      [:p "Not valid " employee_id])))    

(defpage [:post "/checkout"] {:keys [employee_id]}
  (str "You tried to checkout as " employee_id))


