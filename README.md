# **wrds** - ProgrammeerProject

### Axel Verheul - 10744185

The problem that this app is trying to solve is the learning of words in a new language. The user will be able to created lists with a words in one language and the translation. After the list is finished the user start an examination. Here one word will be asked and the user should fill in the translation. The program tracks how many words are correctly translated and then finally will calculate a score. The user sees what mistake is made and will thus learn from these mistakes.  Therefore helping with learning. These lists can also be shared with other users. The app is called **wrds**.

For the pie charts the following external library was used:<br>
https://github.com/PhilJay/MPAndroidChart

For external database and user log in Firebase was user:<br>
https://console.firebase.google.com/

For a more detailed explanation of how the app works see <a href="https://github.com/MangoDream1/Wrds---Programmeer-Project/blob/master/REPORT.md">REPORT.md</a> or the source code (it's well documented with javadoc)

### Screenshots

MainActivity with no lists:<br>
![0.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/0.png "0.png")

Create list dialog (same as modify list dialog, only then values filled in from database for edit):<br>
![1.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/1.png "1.png")

Filled in:<br>
![2.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/2.png "2.png")

If not correctly filled give error:<br>
![3.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/3.png "3.png")

New list in ListView:<br>
![4.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/4.png "4.png")

If selected (long click) possible to edit, copy, delete or upload. To clear selection press return. Only delete possible on multiple lists:<br>
![5.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/5.png "5.png")

Created list for to test deletion:<br>
![6.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/6.png "6.png")

Confirmation:<br>
![7.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/7.png "7.png")

List deleted:<br>
![8.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/8.png "8.png")

After click on list goto ListView:<br>
![9.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/9.png "9.png")

Added words by pressing add after filling in form. Add button goes down with rest. Words can selected and then edited, deleted and modified the same way lists are:<br>
![10.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/10.png "10.png")

Started exam by pressing play button:<br>
![11.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/11.png "11.png")

If the user quits the exam confirmation is asked first:<br>
![32.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/32.png "32.png")

Incorrectly translated word; gives feedback where mistakes were made is shown:<br>
![12.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/12.png "12.png")

Correct feedback:<br>
![13.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/13.png "13.png")

Incorrect words not removed from list thus asked again. All words need to be correct once before continuing to ResultActivity:<br>
![14.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/14.png "14.png")

Show score with a pie chart showing distribution correct/false:<br>
![15.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/15.png "15.png")

Pressed improve. Now only use words that were incorrect for the exam:<br>
![16.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/16.png "16.png")

New score after improving:<br>
![17.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/17.png "17.png")

Pressed share on list in MainActivity without being logged in takes us to log in screen:<br>
![18.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/18.png "18.png")

Pressing register lets us register:<br>
![19.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/19.png "19.png")

After register we are signed in:<br>
![20.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/20.png "20.png")

Now being signed in we can upload a list:<br>
![21.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/21.png "21.png")

After upload get key to give to friends:<br>
![22.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/22.png "22.png")

List can be loaded after pressing "Load List" in MainActivity:<br>
![23.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/23.png "23.png")

Pasted in the key:<br>
![24.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/24.png "24.png")

List added from Firebase, now with a new creator (see test as username):<br>
![25.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/25.png "25.png")

Not possible to add, remove or modify words in these lists:<br>
![26.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/26.png "26.png")

Back to share to stop the share:<br>
![27.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/27.png "27.png")

Share stopped:<br>
![28.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/28.png "28.png")

Now no longer see the key on the loaded list since it isnt shared any longer:<br>
![29.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/29.png "29.png")

Lists can be copied to gain all right to it:<br>
![30.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/30.png "30.png")

All rights available:<br>
![31.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/31.png "31.png")















