(ns web.pages
  (:require
    [clojure.string :as str]
    [hiccup.page :as page]
    [chatbot.park_utils :as park]))


(defn chatbot-page
  "Page of the selected park"
  [content current-uri]
  (page/html5
    [:head
     [:meta {:charset "UTF-8"}]
     [:title "Chatbot"]
     (page/include-css "style.css")
     (page/include-js "script.js")]
    [:body
     [:div {:id "content"}
      [:h1 "Prague parks chatbot"]
      [:p [:a {:href "/"} "Home"]]
      [:h3 (str "Current park: " (park/keyword->park
                                   (str/replace current-uri #"/" "")))]
      [:div {:id "chat-window"}
       content]
      [:form {:method "POST" :action current-uri}
       [:input
        {:type "text" :autofocus "autofocus" :id "input" :name "input"}]]]]))

(defn index
  "Home page"
  []
  (page/html5
    [:head
     [:meta {:charset "UTF-8"}]
     [:title "Chatbot"]
     (page/include-css "style.css")]
    [:body
     [:h1 "Prague parks chatbot"]
     [:h2 "List of parks: "]
     [:p
      [:a {:href "/bertramka"}
       "Bertramka"]]
     [:p
      [:a {:href "/riegerovy-sady"}
       "Riegerovy Sady"]]
     [:p
      [:a {:href "/klamovka"}
       "Klamovka"]]
     [:p
      [:a {:href "/letna"}
       "Letná"]]
     [:p
      [:a {:href "/stromovka"}
       "Stromovka"]]
     [:p
      [:a {:href "/ladronka"}
       "Ladronka"]]
     [:p
      [:a {:href "/frantiskanska-zahrada"}
       "Františkánská zahrada"]]
     [:p
      [:a {:href "/kampa"}
       "Kampa"]]
     [:p
      [:a {:href "/petrin"}
       "Petřín"]]
     [:p
      [:a {:href "/kinskeho-zahrada"}
       "Kínského zahrada"]]
     [:p
      [:a {:href "/vysehrad"}
       "Vyšehrad"]]]))
