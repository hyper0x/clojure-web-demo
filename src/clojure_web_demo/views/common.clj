(ns clojure-web-demo.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page :only [include-css html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "clojure-web-demo"]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                content [:a {:href "http://hypermind.com.cn:8080/todos"} "Todo list"]]]))

(defpartial todo-item [{:keys [id title due]}]
    [:li {:id id} ;; maps define HTML attributes
        [:h3 title]
        [:span.due due]]) ;; add a class

(defpartial todos-list [items]
    [:h1 "Todo list!"]
    [:ul#todoItems ;; set the id attribute
        (map todo-item items)])
