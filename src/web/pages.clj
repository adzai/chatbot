(ns web.pages
  (:require
    [clojure.string :as str]
    [hiccup.page :as page]
    [chatbot.parse :refer [parse-json]]
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
      [:p {:class "btn-home-wrapper"}
       [:a {:href "/help" :class "btn btn-home"} "Help"]]
      [:p {:class "btn-home-wrapper"}
       [:a {:href "/history" :class "btn btn-home"} "History"]]
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
     [:div {:class "greeting"}
      "Hi! I am your park guide. You can choose the park you are
      interested in and write what you want to know in the chat.
      If you need help, use the Help button. History of the park
      can be viewed by tapping the button History."]
     [:h2 "List of parks: "]
     [:div {:class "park-list"}
       [:p {:class "btn-wrapper"}
        [:a {:href "/bertramka" :class "btn btn-park"}
         "Bertramka"]]
       [:p {:class "btn-wrapper"}
        [:a {:href "/riegrovy-sady" :class "btn btn-park"}
         "Riegrovy Sady"]]
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

(defn help-page
  "Help page"
  []
  (page/html5
    [:head
     [:meta {:charset "UTF-8"}]
     [:title "Chatbot"]
     (page/include-css "style.css")]
    [:body
      [:p {:class "btn-home-wrapper"}
       [:a {:href "/" :class "btn btn-home"} "Home"]]
      [:p {:class "btn-home-wrapper"}
       [:a {:class "btn btn-home" :onclick "history.back(-1)"} "Back"]]
     [:p (str "The chatbot is designed to answer users' questions regarding "
              "the park of their choice.")]
     [:p (str "The bot provides information about various aspects such as wc, "
              "attractions, biking, skating, sports field, playground, "
              "transportation, parking and more.")]
     [:p (str "Error messages are used to inform user that asked questions "
              "are obscure to the chatbot.")]
     [:p "Example question: Can I ride a bike in Bertramka?"]]))

(defn history-page
  "History page"
  []
  (page/html5
    [:head
     [:meta {:charset "UTF-8"}]
     [:title "Chatbot"]
     (page/include-css "style.css")]
    [:body
      [:p {:class "btn-home-wrapper"}
       [:a {:href "/" :class "btn btn-home"} "Home"]]
      [:p {:class "btn-home-wrapper"}
       [:a {:class "btn btn-home" :onclick "history.back(-1)"} "Back"]]
      ; Fetches history about currently chosen park
     [:p (get (parse-json "data/park-history.json")
         (park/park->keyword @park/park-name))]]))
