(defproject clocking "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :min-lein-version "2.0.0"


            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.3.0-beta3" :exclusions [org.clojure/clojure]]
                           [korma "0.3.0-beta9"]
                           [postgresql "9.1-901-1.jdbc4"]
                           [org.clojure/java.jdbc "0.1.1"]
                           [clj-time "0.4.4"]
                           [cheshire "5.0.1"]
                           [fetch "0.1.0-alpha2"]
                           [prismatic/dommy "0.0.1"]]
            :dev-dependencies [[org.clojure/java.jdbc "0.1.1"]
                               [postgresql "9.1-901-1.jdbc4"]]
            :plugins [[lein-cljsbuild "0.3.0"]]
            :cljsbuild {
                        :repl-listen-port 9000
                        :builds [

                                 {

                                  :source-paths ["src-cljs" "cljs-repl"]
                                  :compiler {
                                             :output-to "resources/public/js/cljs-debug.js"
                                             :optimizations :whitespace
                                             :pretty-print true}}


                                 {

                                  :source-paths ["src-cljs"]
                                  :compiler {
                                             :exclude "src-cljs/repl.cljs"
                                             :output-to "resources/public/js/cljs.js"
                                             :optimizations :advanced
                                             :pretty-print false}}
                                 ]}
            :main clocking.server)
