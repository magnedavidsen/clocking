(ns clocking.client.incomplete
  (:require [fetch.remotes :as remotes]
            [goog.dom :as googdom]
            [goog.events :as events]
            [goog.i18n.DateTimeFormat]
            [clojure.browser.dom :as dom]
            [dommy.template :as template])
  (:require-macros [fetch.macros :as fm]))

(def date-formatter (new goog.i18n.DateTimeFormat "dd/MM/yyyy"))
(def time-formatter (new goog.i18n.DateTimeFormat "HH:mm"))

;;TODO generalize to fix everything with a :date-field
(defn convert-date-to-goog [event]
  (let [date (new goog.date.Date)]
    (.set date (:date event))
    {:clock-in (:clock-in event) :clock-out (:clock-out event) :date date}))

(defn minutes-between [clock-in clock-out]
  (when (and clock-in clock-out)
    (let [one-min (* 1000 60)]
      (.round js/Math
              (/ (- (.getTime clock-out) (.getTime clock-in)) one-min)))))

 (defn format-minutes [minutes]
  (let [remainder (mod minutes 60)]
    (let [hours (/ (- minutes remainder) 60)]
         (str hours "h " remainder "m"))))

(defn event-row [{:keys [date clock-in clock-out]}]
  (template/node
   [:tr
    [:td (when-not (nil? date) (.format date-formatter date))]
    [:td (when-not (nil? clock-in) (.format time-formatter clock-in))]
    [:td (when-not (nil? clock-out) (.format time-formatter clock-out))]
    [:td (format-minutes (minutes-between clock-in clock-out))]]))

(defn sum-hours [events]
  (reduce + (map #(minutes-between (:clock-in %) (:clock-out %)) events)))


(defn employee-report [events]
  (template/node
   [:div {:class "employee-report"}
    [:div (str "Showing hours from " (.format date-formatter (.getDate from-datepicker)) " to " (.format date-formatter (.getDate to-datepicker)) )]
    [:div {:class "total-hours"} (str "Total hours: " (format-minutes (sum-hours events)))]
    [:table [:tr [:th "Date"] [:th "Clocked in"] [:th "Clocked out" ] [:th "Sum"]]
     (map event-row events)]]))

(defn start-page []
  (template/node
   [:div {:id "incomplete-app"}
    (employee-report (filter-events-between all-events (.getDate from-datepicker) (.getDate to-datepicker)))]))

(defn buildpage []
  (.log js/console "Starting to build page.")
  (dom/replace-node (googdom/getElement "incomplete-app") (start-page)))

;;TODO is it ok to do def all-events here?
(defn get-events-from-server []
  (.log js/console "Getting events from server.")
  (fm/letrem [events (get-all-incomplete)]
             (.log js/console "Events returned")
             (def all-events (map convert-date-to-goog events))
             (buildpage)))

;;TODO find better way to start different apps
(when (not (nil? (googdom/getElement "employee-app") )) (get-events-from-server))
