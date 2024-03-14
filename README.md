# availity-assessment
Technical Assessment for Availity Interview

## Questions: ##
1. Tell me about something you have read recently that you would recommend and why. (Can be a Github Repo, Article, Blog, Book, etc.)

2. Coding exercise: You are tasked to write a checker that validates the parentheses of a LISP code. Write a program (in Java or JavaScript) which takes in a string as an input and returns true if all the parentheses in the string are properly closed and nested.

```
cd lisp-validator
javac org/ed4becky/LispValidatorApplication.java
java -cp . org.ed4becky.LispValidatorApplication "<some string>"
```

3. Undone

4. Coding exercise: Availity receives enrollment files from various benefits management and enrollment solutions (I.e. HR platforms, payroll platforms).  Most of these files are typically in EDI format.  However, there are some files in CSV format.  For the files in CSV format, write a program in a language that seems appropriate to you that will read the content of the file and separate enrollees by insurance company in its own file. Additionally, sort the contents of each file by last and first name (ascending).  Lastly, if there are duplicate User Ids for the same Insurance Company, then only the record with the highest version should be included. The following data points are included in the file:

- User Id (string)
- First and Last Name (string)
- Version (integer)
- Insurance Company (string)

```
cd csv-parser
mvn cleam compile
mvn spring-boot:run -Dspring-boot.run.arguments="test.csv"
```

