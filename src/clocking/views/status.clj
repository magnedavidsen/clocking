(ns clocking.views.status
  (:require
   [clj-time.core :as time])
  (:use
   compojure.core))

(defroutes handler
  (GET "/status/timezone" [] (str (time/default-time-zone))))
