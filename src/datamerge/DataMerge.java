package datamerge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The 'DataMerge' class has been developed to merge the contents of three input files namely
 		1. reports.csv
 		2. reports.json
 		3. reports.xml
 * 
 * into one single file namely combinedReport.csv with the following characteristics:

		- The same column order and formatting as reports.csv
		- All report records with packets-serviced equal to zero are excluded
		- Records are sorted by request-time in ascending order
 * 
 * Furthermore, it prints a summary showing the number of records in the output file associated with each service-guid.
 * 
 * 
 * @author Neervan Anooroop
 *
 */

public class DataMerge {
	
	/*
	 *  The global ArrayList 'datalist' will hold the objects of the class 'Report'
	 *  Each object equals to a record/object/element in the report files
	 */
	static List<Report> dataList = new ArrayList<>();
	
	/**
	 * The main function calls five user-defined methods:
	 		1. inputCSV() - load data from reports.csv
	 		2. inputJSON() - load data from reports.json
	 		3. inputXML() - load data from reports.json
	 		4. Merge() - merge all data into one file
	 		5. displaySummary() - display a summary of new file
	 * 
	 * It also sorts 'datalist' by the 'request-time' field using sort() method
	 * The sort() method uses polymorphism by passing any object that is Comparable
	 * 
	 */
	public static void main(String[] args) {
		
		inputCSV();
		
		inputJSON();
		
		inputXML();

		Collections.sort(dataList);

		Merge();
		
		displaySummary();
		
	}
	
	/*
	 * This method reads each line from reports.csv
	 * It extracts each attribute from that line and assign them
	 * to an instance of the 'Report' class
	 * 
	 * This instance is added to the ArrayList 'datalist'
	 */
	public static void inputCSV() {
		
		// Getting the path of reports.csv
		Path p1 = Paths.get("reports.csv");

		Report reportCSV;

		try (Scanner s = new Scanner(p1)) {

			// Statement to discard the header of reports.csv
			s.nextLine();
			
			// Iterating through each line till the end
			while( s.hasNext() ) {
				
				// Creating an instance of class 'Report' (object)
				reportCSV = new Report();
				
				// Getting one line of the document
				String line = s.nextLine();
				
				// Extracting each attribute of the line into the array v[]
				String[] v = line.split(",", -1);
				
				// Setting the extracted attributes to the instance created
				reportCSV.setclientAddress(v[0]);
				reportCSV.setclientGuid(v[1]);
				
				/*
				 *  Converting the String 'request-time' to Date format
				 *  This step is important for sorting the ArrayList later
				 */
				Date d1 = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss z").parse(v[2]);
				reportCSV.setrequestTime(d1);
				
				reportCSV.setserviceGuid(v[3]);
				reportCSV.setretriesRequest(Long.parseLong(v[4]));
				reportCSV.setpacketsRequested(Long.parseLong(v[5]));
				reportCSV.setpacketsServiced(Long.parseLong(v[6]));
				reportCSV.setmaxHoleSize(Long.parseLong(v[7]));
				
				// Adding the instance to 'datalist' only if packets-serviced is not zero
				if (reportCSV.getpacketsServiced() != 0) {
					dataList.add(reportCSV);
				}
					
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace(); 
		}
		
	}
	
	/*
	 * This method reads each object from reports.json
	 * It extracts each attribute from that object and assign them
	 * to an instance of the 'Report' class
	 * 
	 * This instance is added to the ArrayList 'datalist'
	 */
	public static void inputJSON() {
		
		Report reportJSON;
		
		// JSONParser provides forward, read-only access to JSON data in a streaming way
		JSONParser parser = new JSONParser();
		
		// JSONArray provides an unmodifiable list view of the values in the array
		JSONArray a;
		
		try {
			// Parsing reports.json into instance of JSONArray
			a = (JSONArray) parser.parse(new FileReader("reports.json"));
			
			// Iterating through each object the the JSONArray
			for (Object o : a) {
				  
				// Creating an instance of class 'Report'
				reportJSON = new Report();

				// Casting o to JSONObject
			    JSONObject report = (JSONObject) o;
	 
			    // Setting the extracted attributes to the instance of class 'Report'
			    String clientAddress = (String) report.get("client-address");
			    reportJSON.setclientAddress(clientAddress);
			    
			    String clientGuid = (String) report.get("client-guid");
			    reportJSON.setclientGuid(clientGuid);
			    
			    long requestTime = (long) report.get("request-time");
			    
			    /*
			     * Converting milliseconds request-time to Date format
			     * A Zone Id of UTC-3 has been used instead of local time so that the
			     * program returns the same result when run from anywhere
			     */
			    String date = LocalDateTime.ofInstant(
			    		Instant.ofEpochMilli(Long.valueOf(requestTime)), ZoneId.of("UTC-3")
				        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			    Date d1 = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss").parse(date);
			    reportJSON.setrequestTime(d1);
			    
			    String serviceGuid = (String) report.get("service-guid");
			    reportJSON.setserviceGuid(serviceGuid);
			    
			    long retriesRequest = (long) report.get("retries-request");
			    reportJSON.setretriesRequest(retriesRequest);
			    
			    long packetsRequested = (long) report.get("packets-requested");
			    reportJSON.setpacketsRequested(packetsRequested);
			    
			    long packetsServiced = (long) report.get("packets-serviced");
			    reportJSON.setpacketsServiced(packetsServiced);

			    long maxHoleSize = (long) report.get("max-hole-size");
			    reportJSON.setmaxHoleSize(maxHoleSize);
			    
			    // Adding the Report object to 'datalist' only if packets-serviced is not zero
			    if (reportJSON.getpacketsServiced() != 0) {
					dataList.add(reportJSON);
				}

			  }
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace(); 
		}
		
	}
	
	/*
	 * This method reads each element from reports.xml
	 * It extracts the attribute from that element and assign it
	 * to an instance of the 'Report' class
	 * 
	 * This instance is added to the ArrayList 'datalist'
	 */
	public static void inputXML() {
		
		Report reportXML;
		
		try {  
			// Creating a constructor of file class and parsing an XML file  
			File file = new File("reports.xml");
			
			// An instance of factory that gives a document builder  
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
			
			// An instance of builder to parse reports.xml
			DocumentBuilder db = dbf.newDocumentBuilder();  
			Document doc = db.parse(file);  
			doc.getDocumentElement().normalize();  
			
			// Getting the elements inside of the 'report' element
			NodeList nodeList = doc.getElementsByTagName("report");  
			
			// nodeList is not iterable, so we are using for loop  
			for (int itr = 0; itr < nodeList.getLength(); itr++) {  
				
				// Creating an instance of class 'Report'
				reportXML = new Report();
				
				//Iterating through each node of nodeList
				Node node = nodeList.item(itr);  
				
				if (node.getNodeType() == Node.ELEMENT_NODE) {  
					
					// Casting node to Element
					Element eElement = (Element) node;  
					
					// Setting the extracted attributes to the instance of class 'Report'
					reportXML.setclientAddress(eElement.getElementsByTagName("client-address")
							.item(0).getTextContent());
					reportXML.setclientGuid(eElement.getElementsByTagName("client-guid")
							.item(0).getTextContent());
					
					// request-time is converted from String to Date format for sorting purposes
					Date d1 = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss z").
							parse(eElement.getElementsByTagName("request-time").item(0).getTextContent());
					reportXML.setrequestTime(d1);

					reportXML.setserviceGuid(eElement.getElementsByTagName("service-guid")
							.item(0).getTextContent());
					reportXML.setretriesRequest(Long.parseLong(eElement.getElementsByTagName("retries-request")
							.item(0).getTextContent()));
					reportXML.setpacketsRequested(Long.parseLong(eElement.getElementsByTagName("packets-requested")
							.item(0).getTextContent()));
					reportXML.setpacketsServiced(Long.parseLong(eElement.getElementsByTagName("packets-serviced")
							.item(0).getTextContent()));
					reportXML.setmaxHoleSize(Long.parseLong(eElement.getElementsByTagName("max-hole-size")
							.item(0).getTextContent()));
					
					// Adding the Report object to 'datalist' only if packets-serviced is not zero
					if (reportXML.getpacketsServiced() != 0) {
						dataList.add(reportXML);
					}
				}  
			}  
			
		} catch (Exception e) {  
			e.printStackTrace();  
		} 
		
	}
	
	/*
	 * Converts request-time from Date format to String in order to match
	 * the format specified in reports.csv
	 * 
	 * @param d	the timestamp of request-time
	 * @return 	the required request-time format as a String
	 */
	public static String ADTFormat(Date d) {
		
		SimpleDateFormat d2 = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss z");
		d2.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
		String gmtFormat = d2.format(d);
		String adtFormat = gmtFormat.replace("GMT-03:00", "ADT");
		
		return adtFormat;
	}
	
	/*
	 * This methods writes all the data from the ArrayList 'datalist' into
	 * a new csv file namely 'combinedReport.csv'.
	 * 
	 * Note: The ArrayList contains data from reports.csv, reports.json & reports.xml
	 */
	public static void Merge() {
		
		Report all;

		try {
			// Create a new file called combinedReport.csv
			FileWriter fw = new FileWriter("combinedReport.csv", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			
			/* 
			 * For combinedReport.csv to have the same format as reports.csv, the headers
			 * are added first
			 */
			pw.println(
					
				"client-address,client-guid,request-time,service-guid,retries-request,"
				+ "packets-requested,packets-serviced,max-hole-size"
							
			);

			// Iterating through 'datalist'
			Iterator<Report> it = dataList.iterator();
			while(it.hasNext()) {
				
				// Create a new instance of class 'Report'
				all = new Report();
				all = it.next();

				// Writing the data from 'datalist' onto combinedRecord.csv
				pw.println(
					
					all.getclientAddress() + "," + all.getclientGuid() + "," +
					ADTFormat(all.getrequestTime()) + "," + all.getserviceGuid() + "," + 
					all.getretriesRequest() + "," + all.getpacketsRequested() + "," + 
					all.getpacketsServiced() + "," + all.getmaxHoleSize()
				);

				// Clearing the PrintWriter Stream
				pw.flush();
			}

			// Closing the PrintWriter Stream
			pw.close();
			
		} catch(Exception e) {
			e.printStackTrace(); 
		}
		
	}
	
	/*
	 * This method displays a summary showing the number of records in combinedRecord.csv 
	 * associated with each service-guid
	 * 
	 * This is achieved by using a Map Interface with the attribute as the key and
	 * the number of times it appears as the value
	 */
	public static void displaySummary() {
		
		// Creating the Map Interface
		Map<String, Integer> c = new HashMap<>();
		
		// Getting the path of combinedReport.csv
		Path p = Paths.get("combinedReport.csv");
		
		try (Scanner s = new Scanner(p)) {
			
			// Skipping the first line i.e. the headers
			s.nextLine();
			
			System.out.println("                    SUMMARY OF COMBINED REPORTS");
			System.out.println("===============================================================");
			System.out.println("             service-guid             |   Number of records");
			System.out.println("===============================================================");
			
			while( s.hasNext() ) {
				
				String line = s.nextLine();
				String[] sg = line.split(",", -1);

				// Checks if the attribute is already in the Map
				if (c.containsKey(sg[3])) {
					// If it is, increment the value
					c.put(sg[3], c.get(sg[3]) + 1);
				}
				else {
					// If it is not, then set the value to 1
					c.put(sg[3], 1);
				}
			}
			
			// Printing the key alongside the value
			c.forEach((key, value) -> System.out.println(key + "  |          " + value + "           "));
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace(); 
		}
	}
	
}