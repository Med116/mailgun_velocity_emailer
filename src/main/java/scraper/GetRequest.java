package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class GetRequest {
	
	public String url;
	public GetRequest(String url){
		this.url = url;
	}
	
	public Document  exec(){
		try{
			Document htmlDoc = Jsoup.connect(url)
					.execute().parse();
			
			return htmlDoc;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return null;
		
	}

}
