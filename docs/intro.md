# Prague Parks Chatbot

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
, other were
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
[levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance) is used, which allows to calculate the number of edits word 1 is away from word 2.
