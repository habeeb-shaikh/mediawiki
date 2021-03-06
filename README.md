# mediawiki

# Java Coding Exercise:-
Wikipedia is the largest online encyclopedia, with millions of Articles in many different articles. There is
currently more than 500,000 articles in Arabic.
Wikipedia provides an API for searching and retrieving articles, documentation can be found here
https://www.mediawiki.org/wiki/API:Search.

# The Assignment:-
Create a JAVA Service that has two main parts:
1. Call Wikipedia API to retrieve articles related to (Amman, Jordan), and store results in a DB.
2. Create a REST endpoint that search stored articles based on title or body text/snippet.
3. Create an endpoint to return, the smallest and largest articles based on word count
4. Create an endpoint that returns statistics about documents including, min size, largest size,
median size and same for word count. 

# Checkout and Run Project:-
  Checkout project using git command:-
		$ git init
		$ git clone  git@github.com:habeeb-shaikh/mediawiki.git
	
	Run Spring boot application using maven and embaded tomcat
	 1. To be able to run your Spring Boot app you will need to first build it. To build and package a Spring Boot app into a single executable Jar file with a Maven, 
	 use the below command. You will need to run it from the project folder which contains the pom.xml file.
	  $ maven package or  $ mvn install
	 2.Run Spring Boot app with java -jar command
	  $ java -jar target/mywebserviceapp-0.0.1-SNAPSHOT.jar
	          OR
	 2.Run Spring Boot app using Maven
		$ mvn spring-boot:run
    
    
# Rest endpoints:-
1. Retrive article and store into database.
http://localhost:8080/wiki/storearticles?country=jordan

2. Retrive stored articles based on title or snippet
http://localhost:8080/wiki/searcharticle?title=jordan
http://localhost:8080/wiki/searcharticle?snippet=officially%20the%20Hashemite%20Kingdom%20of.

3.get Largest article as per word count.
http://localhost:8080/wiki/largestarticle?country=jordan

4.get smallest article as per word count.
http://localhost:8080/wiki/smallestarticle?country=jordan

5.get statics of size and word count like min/max/median.
http://localhost:8080/wiki/articlestatics?country=jordan

# Rest endpoints Result:-
 Added output result captured screenshot under doc directory.
