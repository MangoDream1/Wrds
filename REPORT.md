## Wrds - Programmeer Project

###### Axel Verheul - 10744185

#### Doel van de app

Mijn app is gericht op middelbare scholieren die taalvakken volgen. Deze scholieren moeten veel lange woordenlijsten uit hun hoofd leren en deze app helpt hiermee. De gebruiker kan in de app de woordenlijst invullen en vervolgens overhoren. Tijdens het overhoren krijgt de gebruiker feedback of het door hun gegeven antwoord klopt of niet. Zo niet wordt aangeven waar de fout ligt. Wanneer alle woorden goed zijn beantwoord dan krijgt de gebruiker een cijfer te zien en een pie chart die de verhouding goed/fout laat zien. Verder is de mogelijkheid ingebouwd om lijsten te delen op Firebase en kunnen andere gebruikers die lijst vervolgens downloaden en zelf gebruiken.   

#### Screenshots

Deze screenshot wordt het woord testtest gevraagd de vertaling hiervan is in dit geval ook testtest. Nu heeft de gebruiker desddesd ingevuld, dit is dus fout. De feedback laat zien waar de fout zit, namelijk de t's zijn omgewisseld met d's, door het te onderstrepen.<br><br>
![12.png](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/screenshots/12.png "12.png")

#### Globale overview app

De eerste activity die de gebruiker ziet is de **MainActivity**. In een ListView worden de bestaande lijsten laten zien van de gebruiker. Deze kunnen geselecteerd worden met een *long click* om lijsten te verwijderen, kopiëren , veranderen of te delen. Alleen verwijderen kan met meerdere lijsten; de andere knopen verdwijnen wanneer meerdere lijsten geselecteerd zijn. De knopen staan in de toolbar. Verder kan de gebruiker ook nieuwe lijsten aanmaken door op de knop te drukken. Als op een lijst wordt gedrukt in de ListView gaat de gebruiker naar de **ListActivity**.<br>
De **ListActivity** laat de woorden zien in de lijst in een ListView. Nieuwe woorden kunnen van hier worden toegevoegd. Hierbij wordt gecheckt of de gebruiker het woord plus de vertaling heeft ingevuld; zo niet geeft het een waarschuwing. Deze woorden kunnen met een click worden geselecteerd en kunnen, net als bij lijsten, worden verwijdert of verandert. Alleen een woord kan per keer worden verandert. Wanneer de lijst tenminste een woord bevat kan de gebruiker de lijst overhoren en begint de **ExamActivity**. Als de gebruiker de lijst eerder heeft overhoord kan de gebruiker ook direct naar de **ResultActivity** gaan door op de *results* knop te drukken. <br>
De **ExamActivity** neemt een willekeurig woord uit de woordenlijst en vraagt aan de gebruiker om het te vertalen. Als de gebruiker het woord correct heeft ingevuld wordt positieve feedback getoont als pop up. Echter wanneer het incorrect is wordt er een negatieve feedback pop up getoont. Hier wordt het goede woord laten zien, en wordt de locatie waar de fout zit onderstreept. Bovenin ziet de gebruiker hoever hij/zij is met de toets in een progressBar. Naast de progressBar zit een knop die de toets beeindigd; hier wordt eerst confirmatie gevraagd. Zodra alle woorden goed zijn beantwoord gaat de gebruiker naar de **ResultActivity**. <br>
In de **ResultActivity** wordt de score van de gebruiker laten zien. De score wordt berekend aan de hand van het aantal woorden correct beantwoord ten opzichte van het aantal woorden incorrect beantwoord. Onder deze score is een pie chart te zien die de verhouding goed/fout laat zien. Vanaf de **ResultActivity** kan de gebruiker de lijst opnieuw overhoren, alleen de woorden die fout ingevult zijn overhoren of terug naar de **ListActivity**.<br>
De share knop in de **MainActivity** neemt de gebruiker naar de **LogInActivity** als de gebruiker niet is ingelogd. Wanneer wel, dan wordt een dialog laten zien die vraagt of de gebruiker de lijst wil uploaden. Zo ja dan wordt de lijst in Firebase gezet en krijgt de gebruiker een sleutel die gedeeld kan worden met andere gebruikers. Deze sleutel kan in de **MainActivity** worden ingevuld en dan wordt de lijst vanaf Firebase ingeladen.<br>
Is de gebruiker echter niet ingelogd wanneer op share gedrukt wordt gaat de gebruiker naar de **LogInActivity**. Hier kan de gebruiker inloggen of registeren met email; dit gebeurt via Firebase. Ook moet de gebruiker een username invullen. Er wordt gecheckt of de username al bestaat voordat de gebruiker aangemaakt wordt. Elke gebruiker heeft een unieke username. Zodra de gebruiker geregistreerd is, wordt er ingelogd. Na het inloggen wordt de **LogInActivity** gesloten.

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
   - **AnswerComparison**: Het algoritme die het goede antwoord vergelijkt met het door de gebruiker gegeven antwoord. Onderstreept in het goede antwoord waar de gebruiker de fout heeft gemaakt. Dit wordt laten zien in de feedback in de *ExamActivity*
- *Database*
   - **DatabaseHelper**: De DatabaseHelper maakt en update de lokale SQL database en bewaard alle column namen
   - **DatabaseManager**: De DatabaseManager doet alle nodige lokale SQL functies
   - **FirebaseDBManager**: De FirebaseDBManager doet alle functies betreft de Firebase database
- *Dialogs*
   - **CMListDialog**: De CMListDialog is een dialog die het aanmaken (**C**reate) en veranderen (**M**odify) van woordenlijsten doet
   - **DefaultDialog**: DefaultDialog maakt een dialog van de gegeven titel, message, en positieve button string en negatieve button string. Gebruikt voor callback *DefaultDialogInterface*.
   - **LoadDialog**: In de LoadDialog geeft de gebruiker een sleutel van een gedeelde lijst die vervolgens kan worden geladen van Firebase. Heeft ingebouwden plak knop.
   - **ModifyWordDialog**: In de ModifyWordDialog kan de gebruiker woorden aanpassen
   - **ShareDialog**: De ShareDialog upload de lijst wanneer nog niet upgeload; wanneer wel dan laat het de key zien om te delen. Heeft ingebouwden kopieer knop.
- *Interfaces*
   - **DefaultDialogInterface**: DefaultDialogInterface zorgt voor de callback van de *DefaultDialog*
   - **FirebaseKeyInterface**: FirebaseKeyInterface is de callback geroepen als de Firebase lijst sleutel is gecontroleerd
   - **QueryFirebaseInterface**: QueryFirebaseInterface is de callback van Firebase query functies

#### Gedetaileerd overview belangrijke classes

**DatabaseManager**<br>
De DatabaseManager is een singleton die alle SQL functies bevat van de app. Hierin staan de creatie, verwijdering, update en insert functies voor de WordTable en ListTable. De database functies worden apart gehouden van de rest van de code om voor *Seperation of Concerns* te zorgen. <br>
Verder staat in de DatabaseManager functies voor het bijgehouden hoe vaak een poging (*tries*) wordt gedaan om het woord goed te krijgen. Dit wordt gebruikt om de score te berekenen in **ResultActivity**, resultaten in de pie chart en alleen de foute woorden te overhoren. Er zijn meerdere methods geschreven om de pogingen te verkrijgen, te incrementeren, reseten, totaal aantal pogingen tellen in lijst en hoogste poging van lijst. <br>
Ook is er een method om de data van Firebase direct in de lokale database te zetten en een methode om lijsten te kopiëren.

**FirebaseDBManager**:<br>
De FirebaseDBManager is een singleton die alle Firebase database function bevat. Deze singleton zorgt voor alle database functies voor Firebase; query, delete, update & insert. Net als de DatabaseManager wordt dit apart gehouden om voor *Seperation of Concerns* te zorgen.

**ExamActivity**:<br>
In de ExamActivity worden de woordjes overhoord. Er wordt een willekeurig woord gekozen en laten zien. De gebruiker moet de vertaling van dit woord invullen. Wanneer goed een positieve feedback, wanneer fout een negatieve. Bij de negatieve feedback wordt aangegeven in de goede vertaling waar de gebruiker de fout heeft gemaakt. De woorden die de gebruiker fout heeft beantwoorden, worden later opnieuw gevraagd.    

**AnswerComparison**:<br>
In de AnswerComparison wordt het goede antwoord (woord A) vergeleken met het door de gebruiker gegeven antwoord (woord B). Zodra het niet goed wordt beantwoord gaat dit algoritme de locaties in het goede antwoord onderstrepen die de gebruiker niet goed gedaan heeft. Hiervoor wordt woord A en B in stukjes gehakt; in alle mogelijke substrings. Deze substrings worden vervolgens met elkaar vergeleken. Als er gelijkenis is, worden deze gebieden van woord A opgeslagen. Hierna wordt het de locaties van de stukjes string tussen deze gebieden gevonden. Deze locaties worden vervolgens onderstreept en wordt de string doorgegeven.  

**DefaultDialog**:<br>
De DefaultDialog wordt gebruikt voor confirmatie. In de activity wordt meegegeven wat in de dialog komt te staan, zoals titel, bericht, string knop positief en string knop negatief. Ook is een *origin* string nodig. De negatieve knop sluit de dialog zonder iets te doen. De positieve knop gebruikt de DefaultDialogInterface voor een callback. Hiermee kan de activity waar deze dialog gemaakt wordt een functie aanroepen; de juiste functie wordt gebruikt aan de hand van de *origin* string.

**ShareDialog**:<br>
Wat de ShareDialog laat zien hangt af van de status van de lijst en de gebruiker. Is de lijst nog niet geupload dan vraagt de ShareDialog of de gebruiker de lijst wil uploaden. Zo ja, wordt een nieuwe ShareDialog gestart. Wanneer de lijst is geupload dan laat de ShareDialog de sleutel zien die gedeeld moet worden met andere gebruikers. Als de gebruiker ook de eigenaar is van de lijst dan wordt ook een *update* en *stop share* knop laten zien. Wanneer de gebruiker niet de eigenaar is van de gedeelde lijst wordt nog gecheckt of de lijst nog wel in Firebase staat.

**LogInActivity**:<br>
In de LogInActivity kan de gebruiker inloggen of registeren. Bij het registeren wordt een username gevraagd. Deze username is voor iedere gebruiker uniek. Het registratie formulier wordt gecontroleerd, dus ook de uniekheid van de username. Wanneer het niet uniek is moet de gebruiker een nieuwe invullen. Zodra de gebruiker is geregistreerd, wordt de gebruiker ingelogd.

> Clearly describe challenges that your have met during development. Document all important changes that your have made with regard to your design document (from the PROCESS.md). Here, we can see how much you have learned in the past month.

#### Uitdagingen

Tijdens de ontwikkeling van de app liep ik tegen drie grote problemen aan (grote problemen als in langer mee bezig dan normaal).
- Ten eerste het zorgen voor de onderstreping van waar de fout zit bij het door de gebruiker gegeven antwoord. Hiervoor moest eerst de overeenkomst gevonden worden en daarna moest alles behalve dit worden onderstreept. Om dit te doen moesten de locaties gevonden worden tussen het begin en eind van de overeenkomst. Deze locaties moest vervolgens onderstreept worden. Dit zorgde voor meerdere, vaak hele vreemde, bugs maar is nu helemaal werkend.
- Het tweede probleem is het zorgen voor unieke usernames voor gebruikers. Daarvoor moet, voordat een account gecreëerd wordt, gekeken worden of de username al bestaat. Dit is niet zo makkelijk in Firebase en er was dus een rare structuur nodig. Namelijk een aparte map waar usernames in staan, hierin werd gekeken of de username al bestond. Ook omdat Firebase queries asynchroon worden gedaan, brengt dit moeilijkheden mee. Er moet dus gewacht worden totdat dit klaar is voordat feedback gegeven kan worden aan de gebruiker. Het asynchrone en de vreemde structuur van de Firebase layout zorgde voor de prolemen.
- Het derde probleem was het laten zien van de sleutel voor gebruikers die de lijst hadden geladen. Het is in dit geval niet zeker of de lijst nog bestaat in Firebase, dus moet dit nog eerst worden gecheckt. Opnieuw problemen met Firebase en asynchroniteit.

Verder gaf BetterCodeHub ook problemen, doordat een DatabaseManager gebruikt wordt kon *Separate Concerns in Modules* niet gehaald worden. De DatabaseManager wordt in bijna elke class gebruikt en bevat veel verschillende functies. Hierdoor zijn de classes te sterk gekoppeld; waardoor dit punt niet gehaald kan worden. Dit zorgde voor veel frustratie omdat het een race werd naar de hoogste BetterCodeHub score in de groep maar dit punt was helaas niet behalen als *Seperation of Concerns* ook behouden moet worden. Hetzelfde geldt voor *Couple Architecture Components Loosely*, want in deze code wordt gebruik gemaakt van interfaces voor de Firebase callbacks of andere callbacks. Dit zorgt er echter voor dat dit punt ook niet te behalen is.

Er zijn niet echt veranderingen gemaakt ten opzichte van de design document. Dit document schetst namelijk het *Minimal Viable Product*, en de code was op dat stadium ook zo opgebouwd. De extra's die zijn toegevoegd hebben voor de verandering gezorgt die nu in de code te vinden is. Dit zijn dus de toegevoegde dialogs, veranderde database structuur plus functies, firebase functies en log in.

#### Database structuur

##### SQL database

ListTable
   - \_id         (Primary key, integer autoincrement)
   - title        (text, not null)
   - desc         (text)
   - createdAt    (datetime default current_date)
   - creator      (text, not null)
   - languageA    (text, not null)
   - languageB    (text, not null)
   - firebaseID   (text, default null)
   - isOwner      (Integer, default 0)

WordTable
   - \_id         (Primary key, integer autoincrement)
   - listID       (integer, not null)
   - wordA        (text, not null)
   - wordB        (text, not null)
   - tries        (Integer, default 0)

##### Firebase database

- username
   - username: userId
- user
   -userId: username
- lists
   - listId
      - createdAt: datetime
      - desc: string
      - languageA: string
      - languageB: string
      - title: string
      - username: string (creator username)
      - words
         - 0
            - wordA: string
            - wordB: string
         - 1
            - wordA: string
            - wordB: string
         etc...

#### Keuzes

Er is gekozen voor unieke usernames, omdat de username aangeeft wie de lijst heeft gemaakt. Als hiervoor niet unieke usernames worden gebruikt dan kunnen gebruikers elkaar personificeren, waardoor niet duidelijk is of een lijst betrouwbaar is of niet. Dit is tevens ook de reden dat alle usernames lower case zijn, zodat het moeilijk wordt usernames te creëren die veel op elkaar lijken. Deze is keuze is vooral gemaakt met de doelgroep in gedachten.

De keuze om geladen lijsten niet te kunnen aanpassen of aan toe voegen is gekozen om ervoor te zorgen dat het duidelijk is dat de lijst in zijn geheel is gemaakt door de gebruiker met de username die aangegeven wordt. Om wel te kunnen toevoegen/aanpassen kan de gebruiker altijd nog de lijst kopiëren, dan krijgt de gebruiker alle rechten en staat zijn username erbij.

De keuze voor een lokale database en een Firebase database is gemaakt om te voorkomen dat een de eigenaar van de lijst de lijst verwijdert of aanpast en dat dit dan ook bij andere gebruikers die de lijst hebben geladen gebeurt. Aangezien de doelgroep vooral middelbare scholieren zijn, is dit een reëel gevaar.

De keuze om een score en pie chart te laten zien is gemaakt om de gebruiker feedback te geven over hoe goed ze het hebben gedaan. Verder is ook gekozen om van deze activity de mogelijkheid te geven om de foute beantwoorden woorden opnieuw te overhoren. In de hoop dat de gebruiker hun score gaan proberen te verbeteren.

De keuze om de database functies apart te houden in FirebaseDBManager en DatabaseManager is gemaakt om zoveel mogelijk *Seperation of Concerns* mogelijk te maken.

De keuze voor de layout van de **ExamActivity** is gemaakt, aan de hand van hoe Duolingo hun layout heeft bij hun overhoring activity. Dit is vooral om voor zoveel mogelijk usability te zorgen. Verder is het idee van de pop up feedbacks ook hiervandaan.

De keuze om de EditText velden in de **ListActivity** in de footer van de ListView te doen, is gemaakt om ervoor te zorgen dat de *Add* knop altijd onderaan de lijst staat. Terwijl de lijst groeit zal deze knop mee gaan. Dit bleek prettig te werken voor de doelgroep na een usability test.  

De keuze om de toolbar te veranderen wanneer items in de ListViews worden geselecteerd is gemaakt aan de hand van hoe WhatsApp dit doet. WhatsApp verandert namelijk ook de toolbar als een bericht wordt geselecteerd en laat andere knopen zien. Dit is bekende manier om met verschillende opties om te gaan en is dus fijn voor gebruikers vanuit een usability perspectief.

De gekozen icon zijn herkenbaar en zitten standaard in Android en zullen dus ook door andere apps gebruikt worden.

De keuze om de DefaultDialog te gebruiken voor confirmatie schermen en niet aparte dialogs is omdat deze app veel verschillende confirmatie schermen heeft. Als deze allemaal een aparte dialog nodig hebben zal het aantal dialogs veel te hoog worden.

Er is gekozen om niet de gehele lijst te verwijderen uit Firebase wanneer de eigenaar stopt met delen of de lijst verwijdert omdat het zou kunnen dat een andere gebruiker deze lijst voor de verwijdering heeft ingeladen. Dan staat de sleutel van deze lijst in zijn lokale database. Wanneer de lijst wordt verwijdert van Firebase is deze sleutel niet meer geldig dus geen probleem. Tenzij een andere lijst dezelfde sleutel krijgt, dan is er wel een probleem want dan wijst deze sleutel niet meer naar de oorspronkelijk ingeladen lijst.
