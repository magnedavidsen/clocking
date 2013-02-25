(ns clocking.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css include-js html5]]))

(defpartial layout [bodyclass & content]
            (html5
              [:head
               [:title "clocking"]
               (include-css "/css/reset.css")
               [:link {:rel "stylesheet/less" :type "text/css" :href "/css/elements.less"}]
               [:link {:rel "stylesheet/less" :type "text/css" :href "/css/default.less"}]
               [:link {:rel "stylesheet/less" :type "text/css" :href "/css/icons.less"}]
               (include-js "/js/statuscake.js")
               (include-js "/js/less-1.3.0.min.js")
               (include-js "/js/jquery-1.8.2.min.js")
               (include-js "/js/date.js")
               (include-js "/js/default.js")]
              [:body {:class bodyclass}
               [:div#wrapper
                content]]))

(defpartial layout-cljs [bodyclass & content]
            (html5
              [:head
               [:title "clocking - employee"]
               (include-css "/css/reset.css")
               [:link {:rel "stylesheet/less" :type "text/css" :href "/css/elements.less"}]
               [:link {:rel "stylesheet/less" :type "text/css" :href "/css/default.less"}]
               [:link {:rel "stylesheet/less" :type "text/css" :href "/css/icons.less"}]
               (include-js "/js/statuscake.js")
               (include-js "/js/less-1.3.0.min.js")]
              [:body {:class bodyclass}
               [:div#wrapper
                content]]))
