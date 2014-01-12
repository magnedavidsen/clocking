(defproject clocking "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"
            :min-lein-version "2.3.4"
            :jvm-opts ["-Xmx1024M"]

            :dependencies [[org.clojure/clojure "1.5.1"]
                           [compojure "1.1.6"]
                           [lib-noir "0.7.9"]
                           [korma "0.3.0-RC6"]
                           [postgresql "9.1-901-1.jdbc4"]
                           [org.clojure/java.jdbc "0.2.3"]
                           [clj-time "0.6.0"]
                           [prismatic/dommy "0.0.1"]
                           [cljs-ajax "0.2.3"]
                           [fogus/ring-edn "0.2.0"]
                           [org.clojure/clojurescript "0.0-2138"]
                           [lein-light-nrepl "0.0.9"]]
            :repl-options {:nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]}
            :dev-dependencies [[org.clojure/java.jdbc "0.1.1"]
                               [postgresql "9.1-901-1.jdbc4"]]
            :plugins [[lein-cljsbuild "1.0.1"]]
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
