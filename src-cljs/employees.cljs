(ns clocking.client.employees
  (:require [clocking.client.common :as common]
            [fetch.remotes :as remotes]
            [goog.dom :as googdom]
            [goog.events :as events]
            [goog.ui.DatePicker]
            [goog.ui.DatePicker.Events]
            [goog.date.Date]
            [clojure.browser.dom :as dom]
            [dommy.template :as template])
  (:require-macros [fetch.macros :as fm]))

(def userid (js/parseInt (last (clojure.string/split js/document.URL #"/"))))

(defn date-in-range [date from-date to-date]
  (and
   (>= 0 (goog.date.Date.compare from-date date))
   (>= 0 (goog.date.Date.compare date to-date))))

(defn create-datepicker [date]
  (let [picker (new goog.ui.DatePicker)]
    (.setUseNarrowWeekdayNames picker true)
    (.setUseSimpleNavigationMenu picker true)
    (.setAllowNone picker false)
    (.setShowToday picker false)
    (.setFirstWeekday picker 0)
    (.setDate picker date)
    picker))

(def from-datepicker
  (let [date (new goog.date.Date)]
    (.setDate date 1)
    (create-datepicker date)))
(def to-datepicker (create-datepicker (new goog.date.Date)))

(defn event-row [{:keys [date clock-in clock-out]}]
  (template/node
   [:tr
    [:td (when-not (nil? date) (.format common/date-formatter date))]
    [:td (when-not (nil? clock-in) (.format common/time-formatter clock-in))]
    [:td (when-not (nil? clock-out) (.format common/time-formatter clock-out))]
    [:td (common/format-minutes (common/minutes-between clock-in clock-out))]]))

(defn employee-report [events]
  (template/node
   [:div {:class "employee-report"}
    [:div (str "Showing hours from " (.format common/date-formatter (.getDate from-datepicker)) " to " (.format common/date-formatter (.getDate to-datepicker)) )]
    [:div {:class "total-hours"} (str "Total hours: " (common/format-minutes (common/sum-hours events)))]
    [:table [:tr [:th "Date"] [:th "Clocked in"] [:th "Clocked out" ] [:th "Sum"]]
     (map event-row events)]]))

(defn filter-events-between [events from-date to-date]
  (filter #(date-in-range (:date %) from-date to-date ) events))
h
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
    (employee-report (filter-events-between all-events (.getDate from-datepicker) (.getDate to-datepicker)))]))

(defn buildpage []
  (.log js/console "Starting to build page.")
  (dom/replace-node (googdom/getElement "employee-app") (start-page))
  (.render from-datepicker (googdom/getElement "from-datepicker"))
  (.render to-datepicker (googdom/getElement "to-datepicker"))
  (events/listen from-datepicker goog.ui.DatePicker.Events.CHANGE handle-date-change)
  (events/listen to-datepicker goog.ui.DatePicker.Events.CHANGE handle-date-change))

;;TODO is it ok to do def all-events here?
(defn get-events-from-server []
  (.log js/console "Getting events from server.")
  (fm/letrem [events (get-all-events userid)]
             (.log js/console "Events returned")
             (def all-events (map common/convert-date-to-goog events))
             (buildpage)))

;;TODO find better way to start different apps
(when (not (nil? (googdom/getElement "employee-app") )) (get-events-from-server))
