### Audit document

![bettercodehub picture](https://raw.githubusercontent.com/MangoDream1/Wrds---Programmeer-Project/master/doc/bettercodehub.png "bettercodehub picture")

De DatabaseManager zorgt ervoor dat *Separate Concerns in Modules* niet gehaald kan worden. De DatabaseManager wordt in bijna elke class gebruikt en bevat veel verschillende functies. Hierdoor zijn de classes te sterk gekoppeld; waardoor dit punt niet gehaald kan worden. Dit punt was helaas niet behalen als *Seperation of Concerns* ook behouden moet worden.

Hetzelfde geldt voor *Couple Architecture Components Loosely*, want in deze code wordt gebruik gemaakt van interfaces voor de Firebase callbacks of andere callbacks. Dit zorgt er echter voor dat dit punt ook niet te behalen is.
