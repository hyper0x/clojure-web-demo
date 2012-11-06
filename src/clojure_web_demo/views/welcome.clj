(ns clojure-web-demo.views.welcome
  (:require [clojure-web-demo.views.common :as common]
            [noir.content.getting-started]
            [noir.response :as response])
  (:use [noir.core :only [defpage]]))

(defrecord todo-item [id title due])

(def todo-list (atom []))

(def counter
 (let [tick (atom 0)] #(swap! tick inc)))

(defn all-todos []
  (deref todo-list))

(defn add-todo-with-id [todo-id title due]
  (swap! todo-list conj (todo-item. todo-id title due)))

(defn add-todo [title due]
  (let [todo-id (counter)  _ (add-todo-with-id todo-id title due)]
    todo-id))

(defn contains? [title due]
  (if (empty? 
        (filter 
          #(and 
             (= title (.title %)) 
             (= due (.due %))) 
          (deref todo-list)))
    false true))

(defn get-date-string []
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd") (java.util.Date.)))

(defn add-default-todo []
  (let [title "View task list"
        tody (get-date-string)
        exists? (contains? title tody)]
    (if (not exists?) 
      (add-todo-with-id (counter) title tody)
      (deref todo-list))))

(defn remove-todo [todo-id]
  (let [orig-count (count (deref todo-list))
    current-count (count 
                    (reset! todo-list 
                          (for [item (deref todo-list) 
                                :let [id (.id item)] 
                                :when (not(= todo-id id))] 
                            item)))]
  (cond (< current-count orig-count) true :else false)))

(defpage "/welcome" []
  (common/layout
    [:p "Welcome to Hypermind! This is a demo website write by Noir with clojure."]))

(defpage "/todos" {}
  (let [items (add-default-todo)]
    (common/todos-list items)))

;; Handle an HTTP POST to /todos, returning a 
;; json object if successful
(defpage [:post "/todos"] {:keys [title due]}
         (if-let [todo-id (add-todo title due)]
           (response/json {:id todo-id
                           :title title
                           :due-date due})
           (response/empty)))

;; We can define route params too by making them
;; a keyword: /some/route/:param-name
(defpage "/todo/remove/:id" {todo-id :id}
         (if (remove-todo todo-id)
           (response/json {:id todo-id})
           (response/empty)))

