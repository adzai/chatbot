![Clojure CI](https://github.com/adzai/chatbot/workflows/Clojure%20CI/badge.svg?branch=main)
# Prague Parks Chatbot (WIP)

The chatbot provides various information to it's users about 
parks in Prague and activities related to them.

So far the bot provides information about the 
[Bertramka](https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/bertramka/index.html) park,
but all of the parks seen [here](https://www.praha.eu/jnp/cz/co_delat_v_praze/parky/index.html)
will be eventually added.

The work on this project will be documented on https://adzai.github.io/chatbot/

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

## Usage
```
lein run
```
Chatbot will greet you and prompt you for a username. The username can be changed later at any time.
```
Chatbot> Hi!
Chatbot> I am your park guide. I will tell you about Bertramka park. To end the conversation, enter 'finish'.
Chatbot> Input your username or type 'skip'
User> Adam
Chatbot> User name changed to Adam
Chatbot> You can change your username anytime by typing 'username'
Chatbot> Feel free to ask any question about Bertramka!
```

Now you can ask your questions about the Bertramka park.
```
Adam> Can I ride my bicycle in the Bertramka park?
Chatbot> Biking is possible in Bertramka.
```

If you need help, you can type *help* in the input field.
```
Adam> help
Chatbot> The chatbot is designed to answer users' questions regarding Bertramka park.
Chatbot> The bot provides information about following aspects of Bertramka: wc, attractions, biking, skating, sports field, playground, transportation and parking.
Chatbot> Error messages are used to inform user that  asked questions are obscure to chatbot.
Chatbot> The user can finish the conversation by typing word - finish.
```
When you got all the information you needed, you can end the conversation by typing *finish*.
```
Adam> finish
Chatbot> Bye!
```

## Tests
```
lein test
```

## Authors
[Adam](https://github.com/adzai), [Ani](https://github.com/AniSanikidze)
& [Iryna](https://github.com/irinakulinich3712)

## [License](https://github.com/adzai/chatbot/blob/main/LICENSE)

