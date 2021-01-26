# Changelog

List of changes from ICA1 to ICA2 (tracking from 0.2.0)

## [1.0.0] - 2021-01-26

### Added
* Bird identification mode now raises an error when the user's input is irrelevant to
the question.
* Added more information about the project in https://adzai.github.io/chatbot/intro.html.
* Added a keyword for exiting bird identification mode
* Added sections to README.
* Website was deployed [here](http://schoolstuff.me/)
* Additional styling was applied to the website
* Added option to choose a port for the webserver
* Added help and history sections to the website
* Added greetings to website chatbot

### Changed
* Error counter now explains to the user that there were too many
unrecognized sentences and asks if they want to see the help menu.

### Fixes

* Fixed park names not displaying correctly in the website

## [0.2.0] - 2021-01-25

### Added

* The chatbot will try to identify the type of a bird the user saw in
  a park, when prompted to do so.
* A demo website was created for a more user friendly presentation
  as opposed to the REPL.
* Auto generated documentation was added to github pages.
* Added additional terminating keywords such as exit, terminate and bye.
* When the chatbot fails to recognize a keyword in the user's sentence more
  than 3 times, it displays a help menu to the user.

### Fixes

* Chatbot now provides correct information about restaurants.
