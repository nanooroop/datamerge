package datamerge;

import java.util.Date;

/**
 * The 'DataMerge' Report has been developed to create objects that can be stored in 
 * an ArrayList. These objects hold attributes extracted from the report files.
 * 
 * Furthermore, it implements the Comparable interface which helps in sorting the
 * ArrayList by request-time
 * 
 * 
 * @author Neervan Anooroop
 *
 */

public class Report implements Comparable<Report> {
	
	private String clientAddress;
	private String clientGuid;
	private Date requestTime;
	private String serviceGuid;
	private long retriesRequest;
	private long packetsRequested;	
	private long packetsServiced;
	private long maxHoleSize;
	
	/*
	 * Default Constructor for Report
	 */
	public Report () {
		
	}
	
	/*
	 *  Implementing the setters for the Report class
	 */
	public void setclientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}
	
	public void setclientGuid(String clientGuid) {
		this.clientGuid = clientGuid;
	}
	
	public void setrequestTime(Date f) {
		this.requestTime = f;
	}
	
	public void setserviceGuid(String serviceGuid) {
		this.serviceGuid = serviceGuid;
	}
	
	public void setretriesRequest(long retriesRequest) {
		this.retriesRequest = retriesRequest;
	}
	
	public void setpacketsRequested(long packetsRequested) {
		this.packetsRequested = packetsRequested;
	}
	
	public void setpacketsServiced(long packetsServiced) {
		this.packetsServiced = packetsServiced;
	}
	
	public void setmaxHoleSize(long maxHoleSize) {
		this.maxHoleSize = maxHoleSize;
	}

	/*
	 * // Implementing the getters for the Report class
	 */
	public String getclientAddress() {
		return clientAddress;
	}
	
	public String getclientGuid() {
		return clientGuid;
	}
	
	public Date getrequestTime() {
		return requestTime;
	}
	
	public String getserviceGuid() {
		return serviceGuid;
	}
	
	public long getretriesRequest() {
		return retriesRequest;
	}
	
	public long getpacketsRequested() {
		return packetsRequested;
	}
	
	public long getpacketsServiced() {
		return packetsServiced;
	}
	
	public long getmaxHoleSize() {
		return maxHoleSize;
	}
	
	/*
	 * Compares request-time of Date format for sorting purposes
	 * 
	 * @param o Instance of class Report
	 * @return	a negative integer, zero, or a positive integer as this object 
	 * 			is less than, equal to, or greater than the specified object
	 */
	@Override
	  public int compareTo(Report o) {
	    return getrequestTime().compareTo(o.getrequestTime());
	  }
	
}
