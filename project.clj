(defproject clocking "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :min-lein-version "2.0.0"
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.3.0-beta3" :exclusions [org.clojure/clojure]]
                           [korma "0.3.0-beta9"]
                           [postgresql "9.1-901-1.jdbc4"]
                           [org.clojure/java.jdbc "0.1.1"]
                           [clj-time "0.4.4"]]
            :dev-dependencies [[org.clojure/java.jdbc "0.1.1"]
                               [postgresql "9.1-901-1.jdbc4"]]
            :main clocking.server)
