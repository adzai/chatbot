# Prague Parks Chatbot

---
## ICA1

The presented task is to create a chatbot, which would provide
information to it's users about various parks in Prague.
The chatbot has to be written in the clojure programming language,
hence some clojure research had to be done.
The team decided to use [leiningen](https://leiningen.org/), which
is a tool that helps automate projects written in clojure. There
is not any other alternative which would be a better choice, making
this decision relatively simple.

The work on the project is conducted on github, which is a hosting
    platform for software which uses the git version control tool.

This project uses [github actions](https://github.com/features/actions), which allow
the use of automated building tools. The current workflow checks if
the program requires all the dependencies needed, checks for linting
errors, warnings and runs automated tests. The whole project was
managed from [the project page](https://github.com/adzai/chatbot/projects/1) on github.

The main goal (creating a chatbot which answers questions about
Prague parks) was split into many smaller goals. Some became milestones
, other
were tracked under [issues](https://github.com/adzai/chatbot/issues). The created sub tasks allowed everyone to pick one to
work on and thus allowing work in parallel, as these tasks did not
depend on each other. Another added benefit of
this approach is the fact that separate modules were created. These
modules were designed to only
achieve their specific task. This makes it easy to write
a module and write tests for it, which verify the module's functionality
at every stage of the project. At the end, the modules were
assembled together to create the chatbot. When new features had to
be added, it was quite easy to extend an existing module or create
a new one and just plug it in into the existing system.

The chatbot was supposed to answer questions about only 1 park
at this stage - Bertramka. However, the data about all the parks
was gathered at the start of the project.
Since the code of the chatbot was
well maintained and properly modularized, it was trivial
to add the code for answering questions about all of the
parks in Prague near the deadline for ICA1.

The main task of the chatbot is giving information about
specified criteria and features of the park by searching for keywords
in user's input that match these criteria names (e.g. "wc", "playground").
The chatbot functions on the synonyms of keywords, as well,
thus increasing the comprehensibility of the user's input.
The chatbot includes some basic responses imitating a natural
conversation (e.g. greetings, goodbye messages).

A pattern matching methodology was chosen as the core chatbot
mechanism. Currently, the most popular way of making chatbots
is with the help of machine learning. However, from the conducted
research it seems rather evident, that clojure does not seem
to have very mature tools for doing machine learning. Thus, the team
decided to stick with the classical approach of using pattern matching.
Research was also done on chatbot systems working on such principles
such as [ELIZA](https://en.wikipedia.org/wiki/ELIZA).

For more fine tuned pattern matching the chatbot will be using
fuzzy string matching. The difference between fuzzy and regular
matching is, that the former allows matching words which are not
100% similar as the searched word. The team believes, that this
could help with the flexibility of the chatbot as a word can have
slight variations, but have the same meaning. With fuzzy matching
the chatbot could capture such words as valid keywords.
To achieve fuzzy string matching, the
[Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance) is used, which allows to calculate the number of edits word 1 is away from word 2.

---

## ICA2

### Fixes
Based on the remarks provided in ICA 1 feedback,
some functionalities of the chatbot were fixed.

Due to the inappropriateness of the previously defined terminating keyword
– ‘finish’, the more suitable terminating keywords were added, such as ‘exit’
(commonly used in UNIX environment), ‘end’, ‘terminate’, ‘bye’ and etc.

Furthermore, the initial chatbot could not provide information about the
restaurants of Prague parks. This problem was solved by adding restaurant
information in park data and defining corresponding keyword and synonyms for
data retrieval. Chatbot now provides correct information about restaurants.

### Bird Identification Feature
The chatbot was enhanced with a more interactive feature, in particular,
the bot provides the users with the possibility to identify birds.
It was decided to choose birds as the local natural domain because
it is the most widely spread natural domain throughout the Prague parks.
Therefore, it was assumed that there’s a higher chance of users seeing and
getting interested in various bird species than other less abundant
natural domains.

The bird identification feature of the chatbot is based on
the machine learning tool – decision tree. The implemented decision tree was
integrated with the chatbot and allows the user to identify 10 different domain
objects (bird species). The process of bird identification is initiated by
typing the keyword – ‘bird’. Afterward, the chatbot carries out a textual
dialogue with the user by asking questions in order to determine the correct
answer to the domain problem.

For a better user experience, the chatbot provides a helper function which
describes the functionalities of bird identification mode. Moreover,
in case the user input is obscure, the chatbot raises errors and gives clear
instructions about expected responses.

### Website

A website was created to showcase a more common and user friendly approach to deploying chatbots, rather than the REPL environment.
The backend is written in clojure and leverages the [ring](https://github.com/ring-clojure/ring) and [hiccup](https://github.com/weavejester/hiccup) libraries. The web server can be started by using the `--web` flag.

There is an option to connect the web server to MongoDB. MongoDB has
to be running and credentials can be provided through environment
variables or following instructions of the environ library that
was used for this purpose. 
When the web server is run with the `--mongo` flag, the server
uses MongoDB for storing data about the user and their conversations.
Each park has a unique conversation associated with it.

If not started with `--mongo`, the web server defaults to using a clojure map for storing 
data about the user and their conversation. The parks share the
user's conversation history with each other. So if the user chats
about Bertramka and later switches to Ladronka, they will still see
the conversation about Bertramka in the Ladronka page.

Furthermore, a port number the server will start on can be configured
via the `--port` flag, following a valid port number.


The project can be compiled to a `.jar` file which can be run
with `java -r chatbot.jar`. This configuration is used on the deployment
server of the website, which can be found on https://schoolstuff.me.
The `.jar` file also supports an additonal flag `-h` or `--help` which
explains the available flags to the user. This behavior is not possible
when the program is started with `lein run`, as the flag triggers
a leiningen help list.



### Documentation

Automatic documentation generation was added to the project with the help
of the [codox](https://github.com/weavejester/codox) library. Codox extracts doc strings found in the source 
files and creates the documentation pages which you are seeing right
now. The generation of documentation is done with the `lein codox` 
command.

### Further enhancement
The improved version of the chatbot has the ability to keep track of error
responses. If a keyword is not detected in the user's input more than 3 times
in a row, the user is prompted if they want to see a help menu.

---
