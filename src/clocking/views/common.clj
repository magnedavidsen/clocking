(ns clocking.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "clocking"]
               (include-css "/css/reset.css")
               [:link {:rel "stylesheet/less" :type "text/css" :href "/css/default.less"}]
               (include-js "http://lesscss.googlecode.com/files/less-1.3.0.min.js")]
              [:body
               [:div#wrapper
                content]]))
