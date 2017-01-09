# wrds - ProgrammeerProject

**what problem will be solved for the user**

The problem that this app is trying to solve is the learning of words in a new language.

**what features will be available to solve the problem**

The user will be able to created lists with a words in one language and the translation. After the list is finished the user start an examination. Here one word will be asked and the user should fill in the translation. The program tracks how many words are correctly translated and then finally will calculate a score. The user sees what mistake is made and will thus learn from these mistakes.

**a visual sketch of what the application will look like for the user; if you envision the application to have multiple screens, sketch these all out; not in full detail though**

**what data sets and data sources will you need, how you will get the data into the right form for your app**

The user creates lists of words and these need to be saved. Otherwise no external data sets are used.

**what separate parts of the application can be defined (decomposing the problem) and how these should work together**

The application has several parts. The view that lists all the user created lists of words. In this view new lists can be created and existing lists can be deleted. A view of one list of words where new words can be added with translation. Also in this view the examination is started. The examination view asks for the translation of random word in the selected list and then checks if it is correct. If not correct shows where the faults are. After examination a results view shows the user score.   

**what external components (APIs) you probably need to make certain features possible**

No APIs are needed. If there is any time left, maybe Firebase so that lists of words can be shared between users.

**technical problems or limitations that could arise during development and what possibilities you have to overcome these**

A possible technical problem is the comparing between the correct word and the translation. Best case it could show how much is correct and how much how is incorrect. This might be difficult to program.

**a review of similar applications or visualizations in terms of features and technical aspects (what do they offer? how have they implemented it?)**

A similar app is Duolingo, only here the the list of words is pre-defined by the application. The examination part however is similar in the way how this application should do it.
