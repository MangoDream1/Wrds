> Start with a short description of your application (like in the README.md, but very short, including a single screen shot).

## Wrds - Programmeer Project

#### Doel van de app

Mijn app is gericht op middelbare scholieren die taalvakken volgen. Deze scholieren moeten veel lange woordenlijsten uit hun hoofd leren en deze app bied helpt hiermee. De gebruiker kan in de app de woordenlijst invullen en vervolgens overhoren. Tijdens het overhoren krijgt de gebruiker feedback of het door hun gegeven antwoord klopt of niet. Zo niet wordt aangeven waar de fout ligt. Wanneer alle woorden zijn overhoord dan krijgt de gebruiker een cijfer te zien en een pie chart die de verhouding goed/fout laat zien. Verder is de mogelijkheid ingebouwd om lijsten te delen op Firebase en kunnen andere gebruikers die lijst vervolgens downloaden en zelf gebruiken.   

> Clearly describe the technical design: how is the functionality implemented in your code? This should be like your DESIGN.md but updated to reflect the final application. First, give a high level overview, which helps us navigate and understand the total of your code (which components are there?). Second, go into detail, and describe the modules/classes and how they relate.

#### Globale overview app

De eerste activity die de gebruiker ziet is de **MainActivity**. In een ListView worden de bestaande lijsten laten zien van de gebruiker. Deze kunnen geselecteerd worden met een *long click* om lijsten te verwijderen, copieren, veranderen of te delen. Alleen verwijderen kan met meerdere lijsten; de andere knopen verdwijnen wanneer meerdere lijsten geselecteerd zijn. De knopen staan in de toolbar. Verder kan de gebruiker ook nieuwe lijsten aanmaken door op de knop te drukken. Als op een lijst wordt gedrukt in de ListView gaat de gebruiker naar de **ListActivity**.<br>
De **ListActivity** laat de woorden zien in de lijst in een ListView. Nieuwe woorden kunnen van hier worden toegevoegd. Hierbij wordt gecheckt of de gebruiker het woord plus de vertaling heeft ingevult; zo niet geeft het een waarschuwing. Deze woorden kunnen met een click worden geselecteerd en kunnen, net als bij lijsten, worden verwijdert of verandert. Alleen een woord kan per keer worden verandert. Wanneer de lijst tenminste een woord bevat kan de gebruiker de lijst overhoren en begint de **ExamActivity**. Als de gebruiker de lijst eerder heeft overhooird kan de gebruiker ook direct naar de **ResultActivity** gaan door op de *results* knop te drukken. <br>
De **ExamActivity** vraagt neemt een willekeurig woord uit de woordenlijst en vraagt aan de gebruiker om het te vertalen. Als het de gebruiker het woord correct heeft ingevult wordt positieve feedback gegeven als pop up. Echter wanneer het incorrect is wordt negatieve feedback pop up gegeven. Hier wordt het goede woord laten zien, en wordt de locatie waar de fout zit onderstreept. Bovenin wordt een laten zien hoever de gebruiker is met de toets in een progressBar. Naast de progressBar zit een knop die de toets beeindigd; hier wordt eerst confirmatie gevraagd. Zodra alle woorden gevraagd zijn gaat de gebruiker naar de **ResultActivity**. <br>
In de **ResultActivity** wordt de score van de gebruiker laten zien. Voor de score wordt gekeken hoeveel woorden correct geantwoord zijn ten opzichte van het aantal woorden. Onder deze score is een pie chart te zien die de verhouding goed fout laat zien. Vanaf de **ResultActivity** kan de gebruiker de lijst opnieuw overhoren, alleen de woorden die fout ingevult zijn overhoren of terug naar de **ListActivity**.<br>
De share knop in de **MainActivity** neemt de gebruiker naar de **LogInActivity** als de gebruiker niet is ingelogt. Wanneer wel, dan wordt een dialog laten zien die vraagt of de gebruiker de lijst wil uploaden. Zo ja dan wordt de lijst in Firebase gezet en krijgt de gebruiker een sleutel die gedeeld kan worden met andere gebruikers. Deze sleutel kan in de **MainActivity** worden ingevult en dan wordt de lijst vanaf Firebase ingeladen.<br>
Is de gebruiker echter niet ingelogt wanneer op share gedrukt wordt gaat de gebruikter naar de **LogInActivity**. Hier kan de gebruiker inloggen of registeren met Email. Dit gebeurt via Firebase. Ook moet de gebruiker een username invullen. Er wordt gecheckt of de username al bestaat voordat de gebruiker aangemaakt wordt. Elke gebruiker heeft een unieke username. Zodra de gebruiker geregistreert is, wordt er ingelogt. Na het inloggen wordt de **LogInActivity** gesloten.

#### Overview classes

- *Activities*
   - **ExamActivity**: De activity waar de gebruiker overhoort wordt
   - **ListActivity**: De activity waar de woorden van een woordenlijst wordt laten zien
   - **LogInActivity**: De activity waar de gebruiker kan inloggen of registeren
   - **MainActivity**: De activity waar de woordenlijsten worden laten zien
   - **ResultActivity**: De activity waar het resultaat van de overhoring wordt laten zien
- *Adapters*
   - **WordListsCursorAdapter**: De ListView adapter die woordenlijsten laat zien in de *MainActivity*
   - **WordsCursorAdapter**: De ListView adapter die de woorden laat zien van een woordenlijst in de *ListActivity*
- *Algorithms*:
   - **AnswerComparison**: Het algorithme die het goede antwoord vergelijkt met het door de gebruiker gegeven antwoord. Onderstreept in het goede antwoord waar de gebruiker de fout heeft gemaakt. Dit wordt laten zien in de feedback in de *ExamActivity*
- *Database*
   - **DatabaseHelper**: De DatabaseHelper maakt en update de lokale sql database en bewaard alle column namen
   - **DatabaseManager**: De DatabaseManager doet alle nodige lokale sql functies
   - **FirebaseDBManager**: De FirebaseDBManager doet alle functies betreft Firebase
- *Dialogs*
   - **CMListDialog**: De CMListDialog is een dialog die het aanmaken (**C**reate) en veranderen (**M**odify) van woordenlijsten doet
   - **DefaultDialog**: DefaultDialog maakt een dialog van de gegeven titel, message, en positieve button string en negatieve button string. Gebruikt voor callback *DefaultDialogInterface*.
   - **LoadDialog**: In de LoadDialog geeft de gebruiker een sleutel van een gedeelde lijst die vervolgens kan worden geladen van Firebase
   - **ModifyWordDialog**: In de ModifyWordDialog kan de gebruiker woorden aanpassen
   - **ShareDialog**: De ShareDialog upload de lijst wanneer nog niet upgeload en wanneer wel laat het de key zien om te delen
- *Interfaces*
   - **DefaultDialogInterface**: DefaultDialogInterface zorgt voor de callback van de *DefaultDialog*
   - **FirebaseKeyInterface**: FirebaseKeyInterface is de callback geroepen als de Firebase lijst sleutel is gecontroleerd
   - **QueryFirebaseInterface**: QueryFirebaseInterface is de callback van Firebase query functies

#### Gedetaileerd overview belangrijke classes

**DatabaseManager**<br>
De DatabaseManager is een singleton die alle SQL functies bevat van de app. Hierin staan de creatie, verwijdering, update en insert functies voor de WordTable en ListTable. De database functies worden apart gehouden van de rest van de code om voor Seperation of Concerns te zorgen. <br>
In de WordTable wordt per woord bijgehouden hoe vaak een poging (*tries*) wordt gedaan om het woord goed te krijgen. Dit wordt gebruikt om de score te berekenen in **ResultActivity**, resultaten in de pie chart en alleen de foute woorden te overhoren. Er zijn meerdere methods geschreven om de pogingen te verkrijgen, te incrementeren, reseten, totaal aantal pogigen tellen in lijst en hoogste poging van lijst. <br>
Ook is er een method om de data van Firebase direct in de lokale database te zetten

**FirebaseDBManager**:<br>
De FirebaseDBManager is een singleton die alle Firebase database function bevat.  

**AnswerComparison**:<br>

**DefaultDialog**:<br>
De DefaultDialog wordt gebruikt voor confirmatie. In de activity wordt meegegeven wat in de dialog komt te staan, zoals titel, bericht, string knop positief en string knop negatief. Ook is een *origin* string nodig. De negatieve knop sluit de dialog zonder iets te doen. De positieve knop gebruikt de DefaultDialogInterface voor een callback. Hiermee kan de activity waar deze dialog gemaakt wordt een functie aanroepen; de juiste functie wordt gebruikt aan de hand van de *origin* string.

**ShareDialog**:<br>


**LogInActivity**:<br>




> Clearly describe challenges that your have met during development. Document all important changes that your have made with regard to your design document (from the PROCESS.md). Here, we can see how much you have learned in the past month.

#### Challanges

Tijdens de development liep ik tegen drie problemen aan. Ten eerste het zorgen voor de onderstreping van waar de fout zit bij het door de gebruiker gegeven antwoord. Hiervoor moest eerst de overeenkomst gevonden worden en daarna moest alles behalve dit worden onderstreept. Om dit te doen moesten de locaties gevonden worden waar de overeenkomst eindigt totdat het weer begint. Tussen deze locaties moest ten slotte onderstreept worden. Dit zorgde voor meerdere bugs maar is nu helemaal werkend. Het tweede probleem is het zorgen voor unieke usernames voor gebruikers. Daarvoor moet, voordat een account gecreeerd wordt, gekeken worden of de username al bestaat. Dit is niet zo makkelijk in Firebase en er was dus een rare structuur nodig. Namelijk een aparte map waar usernames in staan, hierin werd gekeken of de username al bestond. Ook omdat Firebase queries asynchroon worden gedaan, brengt dit moeilijkheden mee. Er moet dus gewacht worden totdat het klaar is voordat feedback gegeven kan worden aan de gebruiker. Het derde probleem was het laten zien van de sleutel voor gebruikers die de lijst hadden geladen. Het is in dit geval niet zeker of de lijst nog bestaat in Firebase, dus moet dit nog eerst worden gecheckt. Opnieuw problemen met Firebase.
<br>Verder gaf BetterCodeHub ook problemen, doordat een DatabaseManager gebruikt wordt kon *Separate Concerns in Modules* niet gehaald worden. De DatabaseManager wordt in bijna elke class gebruikt en bevat veel verschillende functies. Hierdoor zijn de classes te sterk gekoppelt; waardoor dit punt niet gehaald kan worden. Alleen is ervoor gekozen het zo te houden om *Seperation of Concerns* te behouden.

> Defend your decisions by writing an argument of a most a single paragraph. Why was it good to do it different than you thought before? Are there trade-offs for your current solution? In an ideal world, given much more time, would you choose another solution?

#### Keuzes

De keuze voor unieke usernames is gemaakt, omdat de username aangeeft wie de lijst heeft gemaakt. Als hiervoor niet unieke usernames worden gebruikt dan kunnen gebruiker elkaar verpersonificeren, waardoor niet duidelijk is of een lijst betrouwbaar is of niet.

De keuze om geladen lijsten niet te kunnen aanpassen of aan toe voegen is gekozen om ervoor te zorgen dat het duidelijk is dat de lijst in zijn geheel is gemaakt door de gebruiker met de username die aangegeven wordt. Om wel te kunnen toevoegen/aanpassen kan de gebruiker altijd nog de lijst kopieeren, dan is krijgt de gebruiker alle rechten en staat zijn username erbij.

De keuze voor een lokale database en een Firebase database is gemaakt om te voorkomen dat een de eigenaar van de lijst de lijst verwijdert of aanpast en dat dit dan ook bij andere gebruikers die de lijst hebben geladen gebeurt. Aangezien de doelgroep vooral middelbare scholieren zijn, is dit een reeel gevaar.

De keuze om een score en pie chart te laten zien is gemaakt om de gebruiker feedback te geven over hoe goed ze het hebben gedaan. Hierdoor krijgen ze een idee over goed ze het hebben gedaan. Verder is ook gekozen om van deze activity de mogelijkheid te geven om de foute beantwoorden woorden opnieuw te overhoren. In de hoop dat de gebruiker hun score gaan proberen te verbeteren.

De keuze om de database functies apart te houden in FirebaseDBManager en DatabaseManager is gemaakt om zoveel mogelijk *Seperation of Concerns* mogelijk te maken.

De keuze voor de layout van de **ExamActivity** is gemaakt, aan de hand van hoe Duolingo hun layout heeft bij hun overhoring layout. Om voor zoveel mogelijk usability te zorgen. Verder is het idee van de pop up feedbacks ook hiervandaan.

De keuze om de EditText velden in de **ListActivity** in de footer van de ListView te doen, is gemaakt om ervoor te zorgen dat de *Add* knop altijd onderaan de lijst staat. Terwijl de lijst groeit zal deze knop mee gaan. Dit bleek prettig te werken voor de doelgroep na een usability test.  

De keuze om de toolbar te veranderen wanneer items in de ListViews worden geselecteerd is gemaakt aan de hand van hoe WhatsApp dit doet. WhatsApp verandert namelijk ook de toolbar als een bericht wordt geselecteerd en laat andere knopen zien. Dit is bekende manier om met verschillende opties om te gaan en is dus fijn voor gebruikers vanuit een usability perspectief.

De gekozen icon zijn herkenbaar en zitten standaard in Android en zullen dus ook door andere apps gebruikt worden.

De keuze om de DefaultDialog te gebruiken voor confirmatie schermen en niet aparte dialogs is omdat deze app veel verschillende confirmatie schermen heeft. Als deze allemaal een aparte dialog nodig hebben zal het aantal dialogs veel te hoog worden.

> Make sure the document is complete and reflects the final state of the application. The document will be an important part of your grade.
