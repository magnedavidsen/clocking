(ns clocking.server
  (:require [noir.server :as server]))

(server/load-views-ns 'clocking.views)

(defn run-server [port]
  (defonce server
        (server/start port {:mode :dev :ns 'clocking})))

(defn -main [& port]
  (run-server (if (nil? port) 8080 (Integer. port))))
