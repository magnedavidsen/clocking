(ns clocking.client.employees
  (:require [fetch.remotes :as remotes]
            [goog.dom :as googdom]
            [goog.ui.DatePicker]
            [goog.date.Date]
            [clojure.browser.dom :as dom]
            [dommy.template :as template])
  (:require-macros [fetch.macros :as fm]))

(def userid js/window.employeeid)

(defn date-in-range [date from-date to-date]
  (and
   (>= 0 (goog.date.Date.compare from-date date))
   (>= 0 (goog.date.Date.compare date to-date))))

;;TODO generalize to fix everything with a :date-field
(defn convert-date-to-goog [event]
  (let [date (new goog.date.Date)]
    (.set date (:date event))
    {:clock-in (:clock-in event) :clock-out (:clock-out event) :date date}))

(defn create-datepicker []
  (let [picker (new goog.ui.DatePicker)]
    (.setUseNarrowWeekdayNames picker true)
    (.setUseSimpleNavigationMenu picker true)
    (.setAllowNone picker false)
    (.setShowToday picker false)
    (.setFirstWeekday picker 0)
    picker))

(def from-datepicker (create-datepicker))
(def to-datepicker (create-datepicker))

(defn minutes-between [clock-in clock-out]
  (when (and clock-in clock-out) (.minBetween js/Date clock-in clock-out)))

(defn event-row [{:keys [date clock-in clock-out]}]
  (template/node
   [:tr
    [:td (.toString date)]
    [:td (js/formatTime (str clock-in))]
    [:td (js/formatTime (str clock-out))]
    [:td (js/formatMinutes (minutes-between clock-in clock-out))]]))

(defn sum-hours [events]
  (reduce + (map #(minutes-between (:clock-in %) (:clock-out %)) events)))

(defn employee-report [events]
  (template/node
   [:div {:class "employee-report"}
    [:div {:id "total-hours" :class "total-hours"} (str "Total hours: " (js/formatMinutes (sum-hours events)))]
    [:table [:tr [:th "Date"] [:th "Clocked in"] [:th "Clocked out" ] [:th "Sum"]]
     (map event-row events)]]))

(defn filter-events-between [events from-date to-date]
  (filter #(date-in-range (:date %) from-date to-date ) events))

(defn refresh-employee-report-filtered [events from-date to-date]
  (dom/replace-node (googdom/getElementByClass "employee-report") (employee-report (filter-events-between events from-date to-date)))
  )

;;TODO is it possible to skip this step?
(defn handle-date-change []
  (refresh-employee-report-filtered all-events (.getDate from-datepicker) (.getDate to-datepicker)))

(defn start-page []
  (template/node
   [:div {:id "employee-app"}
    [:div {:class "datepickers"}
     [:div {:id "from-datepicker" :class "datepicker"} "From"]
     [:div {:id "to-datepicker" :class "datepicker"} "To"]]
    (employee-report all-events)]))

(defn buildpage []
  (.log js/console "Starting to build page.")
  (dom/replace-node (googdom/getElement "employee-app") (start-page))
  (.render from-datepicker (googdom/getElement "from-datepicker"))
  (.render to-datepicker (googdom/getElement "to-datepicker"))
  (js/goog.events.listen from-datepicker goog.ui.DatePicker.Events.CHANGE handle-date-change)
  (js/goog.events.listen to-datepicker goog.ui.DatePicker.Events.CHANGE handle-date-change))

;;TODO is it ok to do def all-events here?
(defn get-events-from-server []
  (.log js/console "Getting events from server.")
  (fm/letrem [events (get-all-events userid)]
             (.log js/console "Events returned")
             (def all-events (map convert-date-to-goog events))
             (buildpage)))





(get-events-from-server)
