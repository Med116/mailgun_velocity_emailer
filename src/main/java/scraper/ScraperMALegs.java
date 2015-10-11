package scraper;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.remoting.soap.SoapFaultException;

import models.Legislator;

public class ScraperMALegs {

	public String houseLegsUrl = "https://malegislature.gov/People/List/3";
	public String senateLegsUrl = "https://malegislature.gov/People/List/2";
	public ScraperMALegs(){
		
	}
	
	
	public ArrayList<Legislator> getLegislators(){
		
		ArrayList<Legislator> legs = new ArrayList<Legislator>();
		
		GetRequest houseReq = new GetRequest(houseLegsUrl);
		GetRequest senateReq = new GetRequest(senateLegsUrl);
		ArrayList<Legislator> houseLegs = getLegs(houseReq, "Representative");
		// throttle 3 seconds between requests
		try{
			Thread.sleep(3000);
		}catch(Exception e){
			e.getMessage();
		}
		ArrayList<Legislator> senateLegs = getLegs(senateReq, "Senator");
		
		legs.addAll(houseLegs);
		legs.addAll(senateLegs);
		
		
		return legs;
		
	}


	private ArrayList<Legislator> getLegs(GetRequest req, String jobTitle) {
		ArrayList<Legislator> legs = new ArrayList<Legislator>();
		Document htmlDoc = req.exec();
		//System.out.println("BODY: " + htmlDoc.body().toString());
		if(htmlDoc != null){
			
			Elements dataRowElems = htmlDoc.select(".dataRow");
			
			System.out.println("SIZE OF ELEMS : " + dataRowElems.size());
			for(Element dataRow : dataRowElems){
				
				Elements nameElems = dataRow.select(" td a[href*=/People]");
				if(nameElems.size() > 0){
					Element nameElem = nameElems.first();
					System.out.println( jobTitle + " : " + nameElem.text());
					String firstLastName = nameElem.text();
					String[] nameArr = firstLastName.split(",");
					String firstName = nameArr[0].trim();
					String lastName = nameArr[1].trim();
					
					String email = dataRow.select("a[href*=mailto]").first().text();
					
					Legislator leg = new Legislator(email,firstName,lastName);
					leg.setTitle(jobTitle);
					//legs.add(leg);
					
				}else{
					System.out.println("VACANCY");
				}
				
				
			}
			
			
		}else{
			System.out.println("DOC NULL REQ: " + req.url);
		}
		
		
		return legs;
	}
	
	
}
