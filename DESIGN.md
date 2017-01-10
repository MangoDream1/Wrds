### Design document

###### Activity flow:

MainActivity -> ListActivity -> ExamActivity -> ResultActivity

###### Classes

- **MainActivity**: Main screen that shows all the lists of the user. The user can create or select a word list.
- **ListActivity**: List screen that shows one list. Here the user can add new words or start the exam.
- **ExamActivity**: Asks for the translation of a random word. Continues until all words have been asked and then starts the ResultActivity. Can also stop examination. After the user presses the CHECK button the awnser is checked and feedback is given. If not correct shows where the fault lies. Also shows a progress bar.
- **ResultActivity**: Shows the score the user would have gotten for the examination.
- **DatabaseHelper**: Helps with the creation and maintenance of the database.
- **DatabaseManager**: Singelton that handles inserting and deleting of items in the database.
- **CMListDialog**: Dialog that handles create of modification of a list
- **AnswerComparison**: Class that compares two strings and shows the differences.

###### Sketches

![Main View](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/Main View.png "Main View")

Main View add list dialog:

![Main View add list dialog](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/Main View add list dialog.png "Main View add list dialog")

List View:

![List View](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/List View.png "List View")

Exam View:

![Exam view](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/Exam view.png "Exam view")

Exam view incorrect pop up:

![Exam view incorrect pop up](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/sketch/Exam view incorrect pop up.png "Exam view incorrect pop up")

###### Database Design

For the database there are two tables: list and word. The list table has the following attributes:
- list_id (primary key, int)
- desc (text)
- createdAt (datetime)
- creator (text)
- languageA (text)
- languageB (text)

The word table has the following attributes:
- word_id (primary_key, int)
- list_id (foreign key, int)
- word (text)
- translation (text)
