# wrds - ProgrammeerProject

The problem that this app is trying to solve is the learning of words in a new language. The user will be able to created lists with a words in one language and the translation. After the list is finished the user start an examination. Here one word will be asked and the user should fill in the translation. The program tracks how many words are correctly translated and then finally will calculate a score. The user sees what mistake is made and will thus learn from these mistakes. Therefore helping with learning.

The user creates lists of words and these need to be saved. Otherwise no external data sets are used. The application has several parts. The view that lists all the user created lists of words. In this view new lists can be created and existing lists can be deleted. A view of one list of words where new words can be added with translation. Also in this view the examination is started. The examination view asks for the translation of random word in the selected list and then checks if it is correct. If not correct shows where the faults are. After examination a results view shows the user score.   

No APIs are needed. If there is any time left, maybe Firebase so that lists of words can be shared between users. A possible technical problem is the comparing between the correct word and the translation. Best case it could show how much is correct and how much how is incorrect. This might be difficult to program.

A similar app is Duolingo, only here the the list of words is pre-defined by the application. The examination part however is similar in the way how this application should do it.

###### Sketches

Main View:

![Main View](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/Main View.png "Main View")

Main View add list dialog:

![Main View add list dialog](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/Main View add list dialog.png "Main View add list dialog")

List View:

![List View](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/List View.png "List View")

Exam View:

![Exam view](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/Exam view.png "Exam view")

Exam view incorrect pop up:

![Exam view incorrect pop up](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/Exam view incorrect pop up.png "Exam view incorrect pop up")
