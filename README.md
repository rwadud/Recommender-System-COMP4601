# Recommender System Project for COMP4601

## Introduction
The main objective is to have the student analyze a body of documents and, given user access patterns and recommendations, create a simple contextual advertising system based on user recommendations.
  
## Source Files

edu.carleton.comp4601.analyzers.CommunityAnalyzer.java

edu.carleton.comp4601.analyzers.PreferenceAnalyzer.java

edu.carleton.comp4601.analyzers.SentimentAnalyzer.java

edu.carleton.comp4601.database.DatabaseManager.java

edu.carleton.comp4601.model.Community.java

edu.carleton.comp4601.model.Model.java

edu.carleton.comp4601.model.Page.java

edu.carleton.comp4601.model.Review.java

edu.carleton.comp4601.model.Reviews.java

edu.carleton.comp4601.model.Sentiment.java

edu.carleton.comp4601.model.User.java

edu.carleton.comp4601.resources.ContextualAdvertisingSystem.java

edu.carleton.comp4601.resources.CorpusParser.java

edu.carleton.comp4601.resources.Main.java

edu.carleton.comp4601.utility.ContextClassListener.java

edu.carleton.comp4601.utility.CsvWriter.java

edu.carleton.comp4601.utility.DataLoader.java

edu.carleton.comp4601.utility.HTMLTableFormatter.java

edu.carleton.comp4601.utility.Utils.java

## System Files
stop.txt

## Data
### Input Files
https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/archive

## Compilation
### Requirements
* MongoDB (version 3.6)
* Chrome (version 70 and above), iOS (version 11 or later) or Android (version 8 and above)
* Tomcat (version 9.0.30)
* Jersey (version 2.27)

### Installation & Launch Instructions
* Download "COMP4601-RS.zip" from cuLearn
* Extract the files from the zip
* Create a dynamic web project in Eclipse by importing the WAR file

## Operating Instructions
* Start a MongoDB client connection in terminal prior to launching the application.
* Build & run 'edu.carleton.comp4601.utility.DataLoader.java' as "Java Application"
** Note: This operation will take some time to fully complete.
* Test web endpoints via 'Demonstration Instructions' section.

## Demonstration Instructions
<b>.../*</b>  

http://localhost:8080/COMP4601-RS/rest/rs/ 

 
<b>.../context</b>

http://localhost:8080/COMP4601-RS/rest/rs/context 

 
<b>.../community</b>

http://localhost:8080/COMP4601-RS/rest/rs/community 
 
<b>.../fetch/{user}/{page}</b>
 
"Action Packers!" User, Looking at Horror Movie Page:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://localhost:8080/COMP4601-RS/rest/rs/fetch/A2ATWKOFJXRRR1/0780625633
 
"Horror Story." User, Looking at Comedy Movie Page:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://localhost:8080/COMP4601-RS/rest/rs/fetch/A1CZICCYP2M5PX/0790742322
 
"Funny ones..." User, Looking at Action Movie Page:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://localhost:8080/COMP4601-RS/rest/rs/fetch/A2AVV9LV9UXT6F/B000A896J8
 
<b>.../advertising/{category}</b>
 
Action Category URL: 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://localhost:8080/COMP4601-RS/rest/rs/advertising/action
 
Horror Category URL:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://localhost:8080/COMP4601-RS/rest/rs/advertising/horror
 
Comedy Category URL: 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; http://localhost:8080/COMP4601-RS/rest/rs/advertising/comedy
