Project: Sears.com Results Text Scraper					
Author: Gouthami Kondakindi										
Date: 28 September, 2014	

Files Included: Assignment.jar,Assignment.java,Result,java,Documentation.txt

How to Run the Application: java -jar Assignment.jar <keyword> [<pageNumber>]  where <pageNumber> is optional field

Note: Jsoup was used for HTML parsing. But the jar file need not be included while executing the application from command line
because I combined both Assignment.jar and jsoup.jar into a single jar -> Assignment.jar
									
******************************************************************************************************************************************
There are 2 classes in the project:
1. Assignment.java
2. Result.java

1. Assignment.java: 
i) This is the main class in the project. 
ii) It validates the input given by the user and calls required functions to get total number of products or to get all products information
based on the given input.
iii) This class also has a function - getDataFromSears() that send HTTP request to sears.com by constructing a request URL and getting results from the website.
getDataFromSears() also converts the obtained information from the website which is a InputStream into a Result object.
iv) This class has a few helper functions to display results on the screen.

2. Result.java
i) This class has methods that help the main class in displaying results in the required format.
ii) An instance variable of this class: Set<Product> results; is a HashSet of type Product where Product is an inner class of Result.java
This instance variable holds multiple results related to a particular product.
iii) This class has a method: convertData() which takes data from Input Stream and converts it into DOM object with the help of an intermediary String format.
**Jsoup is used for manipulating DOM
iv) parseSize() method of this class parses the DOM object to extract the total number of products that match the input product keyword.
This method uses regular expression to extract the number of products from the HTML tag.
v) parseInfo() method extracts all products that match the search keyword and stores it in the "results" Set. For calculating product price,
the product ID of every product should be know and hence the result was parsed to obtain this value.
vi) An Inner Class "Product" is defined inside Result class. This class stores product information of every individual product.

Exception handling is done for most of the functions that have a possibility of throwing exception.

The code was tested with several test cases and it displayed accurate results for all of them. Few of them are mentioned below:
TEST CASES:
#1 java -jar Assignment.jar 
Result: "Error: Please run the application using the following format: java -jar Assignment.jar <keyword> [<page number>]"

#2 java -jar Assignment.jar 1
Result: "Keyword "1" resulted in "0" products."

#3 java -jar Assignment.jar "bean bag" -1
Result: "Error: Page number should be greater than 0"

#4 java -jar Assignment.jar "digital camera"
Result: "Keyword "digital camera" resulted in "500+" products."

#5 java -jar Assignment.jar "gouthami"
Result: "Keyword "gouthami" resulted in "0" products."

#6 java -jar Assignment.jar "digital camera" 1
Result: "All results for "digital camera" on Page 1:
[
PRODUCT INFO:
 ProductName: GE X400 15X zoom 14MP Digital Camera
 Price: $104.00
 Vendor: Sold by sears
 <24 similar results>
 ]"
 
#7 java -jar Assignment.jar "sun" "sun"
Result: "Error: Page number given as an input should be an integer greater than 0"

Challenges faced during the implementation:
Extracting the Product Price was different when compared to extracting other product related information.
Each product that matched the keyword had a different product ID and hence had a different value for "id" attribute in <div> tag. 
As a result, few additional manipulations had to be done to formulate a DOM hierarchy string.

Future Work:
A GUI can be designed on top of this existing code base so that user can type product names in input boxes instead of Command Line.
It will make the application more easy to use.
