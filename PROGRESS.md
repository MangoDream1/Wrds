#### Axel Verheul - 10744185

# Week 1

## Day 1

- started writing README.md
- started making sketches
- made the github repository
- initialized Android Studio

## Day 2

- Started DESIGN.md
- Created Database layout
- Finshed initial outline of Activities/Classes
- Created Adapters for ListViews
- Made simple toolbar
- Finished initial app layout XML for MainActivity
- Created DatabaseHelper class
- Started DatabaseManager class

## Day 3

- Updated README.md
- Updated DESIGN.md with class layout sketch
- Finished intial DatabaseManager update, delete and insert functions
- In app adding of lists
- See the items in ListView (but not immediately only after Activity is created again)

## Day 4

- Data changes will show up immediately
- Added ListActivity and XML
- Can reach ListActivity
- Can see correct list content in ListActivity
- Updated DatabaseManager and DatabaseHelper
- Created ExamActivity, can also be reached
- Started ExamActivity XML
- Can now add words in ListActivity
- Display random word in ExamActivity

## Day 5

- Updated DESIGN.md with possible extra features
- Fixed Android Studio package name
- Created PROGRESS.md

## Day 6

## Day 7

- Started AnswerComparison, to find where the user has made errors

# Week 2

## Day 1

- Fixed bug in AnswerComparison
- Finished AnswerComparison, should work now
- Created ResultActivity
- Created a way to reach ResultActivity
- ResultActivity now shows a score
- Finished initial design of ResultActivity
- Display number of words and date in MainActivity per list
- Added progress bar to ExamActivity

## Day 2

- Added way to modify lists and words, new dialogs and ways to open them and database support
- Added new toolbar that only shows when words or lists are selected
- Start counting at 1 not 0
- Save instance state of selected items
- Rearranged code files into folders
- Added XML of feedback view for ExamActivity

## Day 3

- Corrected feedback and added continue button
- Improved looks of layouts
- Fixed bug if input is empty
- Created STYLE_GUIDE.md
- Count the number of mistakes made per word in database
- Added increment and reset functions for mistake in DatabaseManager
- In the ResultActivity show the number of mistakes that have been made
- Save instance state in ExamActivity
- Updated DESIGN.md
- Can retry lists

## Day 4

- Renamed Database constants
- Also now count tries not mistakes
- Changed old functions for mistakes into tries
- Give warning if user hasn't filled in both or a single word when adding words
- Added padding to dialogs
- Added maxLines and scoll in TextView in ListActivity and MainActivity
- Improved ListLayout
- Added BarGraph to ResultActivity

## Day 5

- Fixed bug in AnswerComparison now should work if there are multiple faults

# Week 3

## Day 1

- Improved graph with titles, and labels
- Disable retry if max score
- Removed bug in underline in AnswerComparison
- Added Firebase
- Set focus on WordA after adding word
- Added padding to graph
- Can continue to retry mistakes until user has correctly answered everything right once
- Can now goto ResultActivity from ListActivity
- Added landscape xml for ResultActivity
- Improved warning
- Started login activity with Firebase
- Downgraded firebase version so that it works with emulator (!! stupid bug)

## Day 2

- Added trim to word enter
- Can now login and checks if username is taken
- Added account creation, also create username
- Added log in and log out button
- Moved firebase auth instance creation over to MainActivity
- Removed login button. Now goto LoginActivity if user is not logged in when share is pressed
- Changed firebase layout and started FirebaseDBManager
- Fixed check if username is taken
- Able to upload list to Firebase

## Day 3

- 

