COMP4601 - Assignment 2

Student Name & Numbers
  Alexander Nguyen (100908039)
  Redwan Wadud (100873111)

Introduction
  The main objective is to have the student analyze a body of documents and, given user access patterns and recommendations, create a simple contextual advertising system.
  
Source Files

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

System Files
  stop.txt

Data
Input Files
  https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/archive

Compilation
Requirements
  MongoDB (version 3.6)
  Chrome (version 70 and above), iOS (version 11 or later) or Android (version 8 and above)
  Tomcat (version 9.0.30)
  Jersey (version 2.27)

Installation & Launch Instructions
  Download "COMP4601-RS.zip" from cuLearn
  Extract the files from the zip
  Create a dynamic web project in Eclipse by importing the WAR file

Operating Instructions
  Start a MongoDB client connection in terminal prior to launching the application.
  Build & run 'edu.carleton.comp4601.utility.DataLoader.java' as "Java Application"
     Note: This operation will take some time to fully complete.
  Test web endpoints via 'Demonstration Instructions' section.

Demonstration Instructions
.../*
http://localhost:8080/COMP4601-RS/rest/rs/ 
 
.../context
http://localhost:8080/COMP4601-RS/rest/rs/context 
 
.../community
http://localhost:8080/COMP4601-RS/rest/rs/community 
 
.../fetch/{user}/{page}

"Action Packers!" User, Looking at Horror Movie Page:
    http://localhost:8080/COMP4601-RS/rest/rs/fetch/A100JCBNALJFAW/B006H90TLI 
 
"Horror Story." User, Looking at Action Movie Page:
    http://localhost:8080/COMP4601-RS/rest/rs/fetch/A26O0T192IBKY1/B00004CJ20 
 
"Funny ones..." User, Looking at Comedy Movie Page:
    http://localhost:8080/COMP4601-RS/rest/rs/fetch/A18CMGIQZ1OAA2/6301978277 
 
.../advertising/{category}

Action Category URL: 
    http://localhost:8080/COMP4601-RS/rest/rs/advertising/action
 
Horror Category URL:
    http://localhost:8080/COMP4601-RS/rest/rs/advertising/horror
 
Comedy Category URL: 
    http://localhost:8080/COMP4601-RS/rest/rs/advertising/comedy
