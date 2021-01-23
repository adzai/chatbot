(defproject chatbot "0.1.0"
  :description "A clojure chatbot focused on parks in Prague"
  :url "https://adzai.github.io/chatbot"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clj-http "3.10.3"]
                 [enlive "1.1.6"]
                 [cheshire "5.10.0"]]
:plugins [[lein-bikeshed "0.5.2"]
          [lein-codox "0.10.7"]]
:codox {:doc-files ["docs/intro.md"]
        :output-path "docs"}
:bikeshed {:max-line-length 80
           :docstrings false
           :var-redefs false
           :name-collisions false}
:main chatbot.core/main-loop
:repl-options {:init-ns chatbot.core})
