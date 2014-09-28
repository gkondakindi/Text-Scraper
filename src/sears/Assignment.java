package sears;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class Assignment 
{
    public static void main(String[] args) 
    {
        if(args.length == 1) //If only ProductName is given as input for searching
        {
            /* Calling a function that tries to get data from sears.com 
             * "1" is the default page number given because there was no input from user regarding the page number
             */
            Result res = getDataFromSears(args[0],"1");
            printTotal(res,args[0]);
        }
        else if(args.length == 2) //If both ProductName and Page Number are given as input for searching
        {
            try
            {
                int page = Integer.parseInt(args[1]);
                if(page<0)
                {
                    System.out.println("Error: Page number should be greater than 0");
                    return;
                }
                String pageNumber = page + "";
                Result res = getDataFromSears(args[0],pageNumber);
                printResults(res,args[0],pageNumber);
            }
            catch(NumberFormatException ex)
            {
                System.out.println("Error: Page number given as an input should be an integer greater than 0");
            }
        }
        else
        {
            System.out.println("Error: Please run the application using the following format: java -jar Assignment.jar <keyword> [<page number>]");
        }
    }
    
    /* Connects to sears.com to search for given product
     * Returns the obtained Input Stream as a Result object
     */
    public static Result getDataFromSears(String keyword, String page)
    {
        HttpURLConnection conn = null;
        Result resultObject;
        try
        {
            //Creating URL String
            String newKeyword = URLEncoder.encode(keyword.trim(),"UTF-8");
            String newPage = URLEncoder.encode(page.trim(),"UTF-8");
            String urlString = "http://www.sears.com/search="+newKeyword+"?keywordSearch=false&catPrediction=false&previousSort=ORIGINAL_SORT_ORDER&pageNum="+newPage+"&autoRedirect=false&viewItems=25";
            URL url = new URL(urlString);
            //Initializing Connection
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(10000);
            conn.connect();
            //Converting obtained result into Result object
            resultObject = new Result(conn.getInputStream());
            return resultObject;
        }
        catch (MalformedURLException ex) 
        {
            System.out.println("Error: MalformedURLException: Problem with URL object string");
            return null;
        } 
        catch(UnsupportedEncodingException ex)
        {
            System.out.println("Error: UnsupportedEncodingException: Problem with URLEncoder.encode()");
            return null;
        }
        catch (ProtocolException ex) 
        {
            System.out.println("Error: ProtocolException: Problem with setRequestMethod()");
            return null;
        } 
        catch (IllegalStateException ex) 
        {
            System.out.println("Error: IllegalStateException: Connection already exists");
            return null;
        } 
        catch (IOException ex) 
        {
            System.out.println("Error: IOException: Problem with opening connection ");
            return null;
        }
        finally //Close the opened connection
        {
            if(conn != null) 
                conn.disconnect();
        }
    }
    
    //Prints Total Number of Products obtained from the search
    public static void printTotal(Result re,String key)
    {
        if(re!=null)
        {
           System.out.println("Keyword \"" + key + "\" resulted in \"" + re.getSize() + "\" products."); 
        }
        else
        {
            System.out.println("Error: No results could be retrieved");
        }
    }
    
    //Prints details of all products obtained from the search
    public static void printResults(Result re, String key, String page)
    {
        if(re!=null)
        {
            System.out.println("All results for \"" + key + "\" on Page " + page +":" );
            if(re.getProductInfo().equals(""))
            {
               System.out.println("No results found for given keyword and page number"); 
            }
            else
            {
                System.out.println(re.getProductInfo());
            }
        }
        else
        {
            System.out.println("Error: No results could be retrieved");
        }
    }

}
