package sears;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

public class Result 
{
    
    /* The below strings are used for traversing the DOM structure using Jsoup
     * 1. MAIN_DIV point to the main <div> tag for each product shown in search results
     * 2. PRODUCT_NAME points to product name <div> tag which is present inside the main <div> tag
     * 3. PRODUCT_PRICE points to only a part of the product price DOM hierarchy because the remaining data is dynamic 
     * and is appended to PRODUCT_PRICE later in this program. 
     */
    public static final String MAIN_DIV = "div.cardContainer.addToCartEnabled";
    public static final String PRODUCT_NAME = "div.cardContainer.addToCartEnabled > div.cardInner > div.cardProdTitle > h2 > a";
    public static final String PRODUCT_PRICE = "div.cardContainer.addToCartEnabled > div.cardInner > div.SGLView > ";
    public static final String SOLD_BY = "div.cardContainer.addToCartEnabled > div.cardInner > div#mrkplc > p:not([class])";
    public static final String NO_OF_RESULTS = "div#nmbProdItems";
    
    private Document dom;
    private String size;
    
    /* Holds information of all Products obtained from the search */
    private Set<Product> results;
    
    /* This constructor takes input stream and calls function convertData() to work on the input stream
     * 
     * Parameter inps is the result in HTML format obtained from searching sears.com
     * This value is obtained from Assignment.java where a request is sent to sears.com 
     * and result obtained is stored as a Result object.
     * 
     * size - Total number of results
     */
    public Result(InputStream inps) throws IOException
    {
        this.results = new HashSet<Product>();
        this.size = null;
        convertData(inps);
    }
    
    /*
     * Getter method that calls a parser function to get all Product related information like Vendor,Price,Product Name.
     * Returns a set of all the results obtained in String format.
     * If user tries to call the function multiple times, the function checks if the results set is empty or not.
     * If the set is not empty, it directly returns the existing set without calling the parser function.
     */
    public String getProductInfo() 
    {
        if (this.results.isEmpty()) 
        {
            parseInfo();
        }
        return this.results.toString();
    }
    
    /*
     * Getter method that calls a parser function to get total number of results.
     * If the user calls this method multiple times and the object has size value set already,
     * then this value is returned directly without calling the parser function again.
     */
    public String getSize() 
    {
        if (this.size == null) 
        {
            parseSize();
        }
        return this.size;
    }
    
    /*
     * Returns the result as a Set
     */
    public Set<Product> getProductSet() 
    {
        if (this.results.isEmpty()) 
        {
            parseInfo();
        } 
        return this.results;
    }
    
    /*
     * Converts InputStream into a DOM object
     */
    public void convertData(InputStream inp)throws IOException
    {
        StringBuilder sb = new StringBuilder("");
        //Clearing the values to make sure that they weren't modified between call to constructor and call to convertData()
        this.results.clear();
        this.size = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(inp));
        String nextLine = br.readLine();
        //Converting InputStream into String
        while(nextLine!=null)
        {
            if(!nextLine.trim().equals(""))
            {
                sb.append(nextLine).append("\n");
            }
            nextLine = br.readLine();
        }
        //Converting String into DOM object using Jsoup
        dom = (Document) Jsoup.parse(sb.toString());
    }
    
    /*
     * Parses the DOM object to obtain Total Number of Products
     */
    private void parseSize() 
    {
        String text = "";
        Elements number = this.dom.select(NO_OF_RESULTS);
        //System.out.println("Debugging: No of results" + number.toString());
        if (!number.isEmpty()) 
        {
            text = number.first().text();
        }
        /* 
         * The <span> tag that contains info about total no. of products has value is in the following format:
         * "Items 26-50 of 500+" where 500+ is total no. of products.
         * That is extracted using the following regular expression:
         */
	String regex = "\\d+\\s*\\+?$";
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.find()) 
            text = m.group(0);
        //    System.out.println("No match found.");
        //else 
            
        if(text.equals(""))
            this.size = "0";
        else
            this.size = text;
    }
    
    /*
     * Parses the DOM object to obtain Product related info - Product Name, Price, Vendor
     */
    private void parseInfo() 
    {
        // Main DOM element for all products
        Elements products = this.dom.select(MAIN_DIV);
        for (Element single : products) 
        {
            Document doc = Jsoup.parse(single.toString());
            String productName  = doc.select(PRODUCT_NAME).text();
            
            String vendor = doc.select(SOLD_BY).text();
            /* A product is sold by sears if there is no vendor tag for that product*/
            if(vendor.equals(""))
                vendor = "Sold by sears";
            
            /*
             * Each product has a seperate product ID and this is used to extract price values
             * The following 2 lines of code extract this pID and then construct a DOM hierarchy string for obtaining Price info
             */
            String tree = "div.cardContainer.addToCartEnabled > div.cardInner > input#pId";
            String temp = doc.select(tree).attr("value");
            String finalPrice = PRODUCT_PRICE + "div#ss_" + temp + ".cPP_v2.gridPrice > span.price_v2.intShipHide";
            String price  = doc.select(finalPrice).text();
            
            //Add these values to the results set
            if(!price.equals("") || !productName.equals(""))
            {
                this.results.add(new Product(productName, price, vendor));
                //System.out.println("Debugging: "+ results);
            }
        }
    }
    
    //Overriding toString() for converting results Set into String format 
    @Override
    public String toString() 
    {
        StringBuilder output = new StringBuilder("");;
        Iterator<Product> it = this.results.iterator();
        while(it.hasNext()) 
        {
            output.append(it.next().toString()).append('\n');
        }
        return output.toString();
    }
   
    //Inner class Product that stores information related to each individual product
    public class Product 
    {
	// 3 Fields in Product class
	private String productName;
	private String price;
	private String vendor;
	
	// Default constructor with no parameters(i.e., no productName, price or vendor)
	public Product() 
        {
		this("","","");
	}
        //Overloaded constructor with parameters
        public Product(String productName, String price, String vendor) 
        {
		this.productName = productName.trim();
		this.price = price.trim();
		this.vendor = vendor.trim();
	}
        
	// Setter methods for all the fields for the current product
	public void setTitle(String productName) 
        {
            this.productName = productName.trim();
	}
	public void setPrice(String price) 
        {
            this.price = price.trim();
	}
	public void setVendor(String vendor) 
        {
            this.vendor = vendor.trim();
	}
	
	// Overriding toString() to return a concatenation of various fields in a single product
	@Override
	public String toString() 
        {
            return "\n\nPRODUCT INFO:\n" + " ProductName: " + this.productName + "\n Price: " + this.price + "\n Vendor: " + this.vendor + "\n";
	}
    }
}
