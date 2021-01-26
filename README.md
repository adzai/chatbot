![Clojure CI](https://github.com/adzai/chatbot/workflows/Clojure%20CI/badge.svg?branch=main)
# Prague Parks Chatbot (WIP)

The Prague Parks Chatbot answers the user's questions about various
parks in Prague and activities related to them.
A list of those parks
can be found [here](https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/index.html).

The work on this project is documented on https://adzai.github.io/chatbot/

## Requirements
* The chatbot is written in [clojure](https://clojure.org/) and as such requires
a [clojure installation](https://clojure.org/guides/getting_started).

* This project uses [leiningen](https://leiningen.org/), follow the installation instructions
[here](https://github.com/technomancy/leiningen#Installation).

## Installation
```
$ git clone https://github.com/adzai/chatbot.git
$ cd chatbot
$ lein deps
```

## Usage in the REPL
```
lein run
```
Chatbot will greet you and prompt you for a username. The username can be changed later at any time.
```
Chatbot> Hi!
Chatbot> I am your park guide.
Chatbot> Input your username or type 'skip'.
User> Adam
Chatbot> User name changed to Adam
Chatbot> You can change your username at any time by typing 'username'.
```
After the username selection, you will be prompted to select a park.
```
Chatbot> Select a park to get info for (type the corresponding number):
1: Bertramka
2: Vysehrad
3: Kinskeho zahrada
4: Petrin
5: Riegrovy sady
6: Obora hvezda
7: Kampa
8: Frantiskanska zahrada
9: Ladronka
10: Stromovka
11: Letna
12: Klamovka
Adam> 1
```
Now you can ask your questions about the chosen park.
The park can be changed later by typing *park*.
```
Adam> Can I ride my bicycle in the Bertramka park?
Chatbot> Biking is possible in Bertramka.
```

If you need an overview of the chatbot's commands and overall functionality
of the chatbot, type *help* in the input field.

When you got all the information you needed, you can end the conversation by typing *finish*.
```
Adam> finish
Chatbot> Bye!
```

## Usage in the website
A demo website was created, it is deployed [here](https://schoolstuff.me).
The chatbot on the website has slightly less features than the REPL version, since like mentioned before it's more of a demo
of how a better presentation
of the chatbot could look like. The biggest missing feature is the
information about birds, however it still offers information 
about every park listed.


The web server can also be started locally with `lein --web`.
This starts the server on localhost:3000. The port can be changed via
the `--port` flag following a valid port number.


MongoDB can be used as the database, by using the `--mongo` flag.
MongoDB has to be already running and credentials supplied through
environment variables `database` and `collection`. The environ
library is used to retrieve the credentials, you can follow a guide
[here](https://github.com/weavejester/environ).


The default database is just a clojure map. Which stores all conversations the user in one place. Thus, when changing parks the conversations
about previous parks that user had 
will still be seen on that page. That is not
the case when using MongoDB, as for every user conversations about
parks are stored separately.

## Tests
```
lein test
```

## Authors
[Adam](https://github.com/adzai), [Ani](https://github.com/AniSanikidze)
& [Iryna](https://github.com/irinakulinich3712)

## [License](https://github.com/adzai/chatbot/blob/main/LICENSE)
