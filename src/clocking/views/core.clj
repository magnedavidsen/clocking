(ns clocking.views.core
  (:require [clocking.views.common :as common]
            [noir.content.getting-started]
            [noir.validation :as vali]
            [clocking.db :as db]
            [noir.cookies :as cookie]
            [noir.response :as resp])
  (:use noir.core
        hiccup.form))

;;TODO let passphrase be property
(pre-route [:get ["/:path" :path #"(?!login|logout)*"]]  {}
           (when-not
               (or
                (= "stemplingsur" (cookie/get :passphrase))
                (= "vectra" (cookie/get :passphrase)))
             (resp/redirect "/login")))

(defpage "/" []
  (common/layout "index"
                 [:div {:class "form"}
                  (form-to {:autocomplete "off"} [:post "/clockin"]
                           [:div {:class "label-input-row"}
                            (label "employee-id" "id: ")
                            [:input {:class "employee-id" :maxlength "3" :onchange "setHiddenField()" :autofocus "autofocus" :type "number" :id "employee-id" :pattern "[0-9]*"}]]
                           (submit-button {:class "clock-in"} "clock in"))
                  (form-to [:post "/clockout"]
                           (hidden-field "hidden-employee-id")
                           (submit-button {:class "clock-out"} "clock out"))
                  [:p {:class "help-text"}]]))

(defpage [:post "/clockin"] {:keys [employee-id]}
  (if
      (empty? (db/get-employee (Integer/parseInt employee-id)))
    (do
      (println "Someone tried clocking in with ID: " employee-id)
      (common/layout
       "index"
       [:p employee-id " is not a registered user."]
       [:meta {:http-equiv "refresh" :content "2;url=/"}]))
    (do
      (db/create-event "clock-in" (Integer/parseInt employee-id))
      (common/layout
       "index"
       [:p employee-id  " has been clocked in."]
       [:meta {:http-equiv "refresh" :content "2;url=/"}]))))

(defpage [:post "/clockout"] {:keys [hidden-employee-id]}
  (if
      (empty? (db/get-employee (Integer/parseInt hidden-employee-id)))
    (do
      (println "Someone tried clocking out with ID: " hidden-employee-id)
      (common/layout
       "index"
       [:p hidden-employee-id " is not a registered user."]
       [:meta {:http-equiv "refresh" :content "2;url=/"}]))
    (do
      (db/create-event "clock-out" (Integer/parseInt hidden-employee-id))
      (common/layout
       "index"
       [:p hidden-employee-id " has been clocked out."]
       [:meta {:http-equiv "refresh" :content "2;url=/"}]))))

(defpage "/login" []
  (common/layout "index"
                 (form-to {:autocomplete "off"} [:post "/login"]
                          [:div {:class "label-input-row"}
                           (text-field {:class "passphrase" :size "10" :maxlength "20"} "passphrase")]
                          (submit-button {:class "log-in"} "log in"))))

(defpage [:post "/login"] {:keys [passphrase]}
  (cookie/put! :passphrase passphrase)
  (if (= "vectra" passphrase)
    (resp/redirect "/admin/employees")
    (resp/redirect "/")))

(defpage "/logout" []
  (cookie/put! :passphrase "")
  (resp/redirect "/"))
