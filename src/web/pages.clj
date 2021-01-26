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
      [:p {:class "btn-home-wrapper"} 
       [:a {:href "/" :class "btn btn-home"} "Home"]]
      [:h3 (str "Current park: " (park/keyword->park
                                   (str/replace current-uri #"/" "")))]
      [:form {:method "POST" :action current-uri}
       [:input
        {:type "text" :autofocus "autofocus" :id "input" :name "input"}]]
      [:div {:class "chat"}
      [:div {:class "chat-title"} "Park bot chat"]
      [:div {:id "chat-window"}
         content]]]]))

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
     [:div {:class "park-list"}
       [:p {:class "btn-wrapper"}
        [:a {:href "/bertramka" :class "btn btn-park"}
         "Bertramka"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/riegerovy-sady" :class "btn btn-park"}
         "Riegerovy Sady"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/klamovka" :class "btn btn-park"}
         "Klamovka"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/letna" :class "btn btn-park"}
         "Letná"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/stromovka" :class "btn btn-park"}
         "Stromovka"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/ladronka" :class "btn btn-park"}
         "Ladronka"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/frantiskanska-zahrada" :class "btn btn-park"}
         "Františkánská zahrada"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/kampa" :class "btn btn-park"}
         "Kampa"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/petrin" :class "btn btn-park"}
         "Petřín"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/kinskeho-zahrada" :class "btn btn-park"}
         "Kínského zahrada"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/vysehrad" :class "btn btn-park"}
         "Vyšehrad"]]]]))
