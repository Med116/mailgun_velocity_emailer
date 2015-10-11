package mailgun;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.MessageBodyWriter;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.core.spi.component.ProviderServices.ProviderClass;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPartMediaTypes;
import com.sun.jersey.multipart.file.*;
import com.sun.jersey.multipart.impl.MultiPartWriter;

/*
 * EXAMPLE USAGE
MailGunSender mailgunMsg = new MailGunSender("key-xxxxxxxxxxx", "yourwebsite.com");
mailgunMsg.setFrom("mark.davis@email.com");
mailgunMsg.setSubject("This is a test message");
mailgunMsg.setText("This is some test text for the message");
mailgunMsg.addRecip("markd6@gmail.com");
mailgunMsg.addRecip("mark.davis@roofs.com");
mailgunMsg.send();
*/


public class MailGunSender {
	String apiKey, domain, text, html, from, subject, attachment;
	ArrayList<String> recipArr;
	
	public MailGunSender(String apiKey, String domain){
		this.apiKey = apiKey;
		this.domain = domain;
		this.recipArr = new ArrayList<String>();
	}
	public MailGunSender(){;
		this.apiKey = "";
		this.domain = "";
		this.recipArr = new ArrayList<String>();
	}
	
	public void setFrom(String from){
		this.from = from;
	}
	public void setSubject(String subject){
		this.subject = subject;
	}
	
	public void addRecip(String recip){
		this.recipArr.add(recip);
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public void setHtml(String html){
		this.html = html;
	}
	
	public void setAttachment(String attachmentPath){
		this.attachment = attachmentPath;
	}
	
	public void  setToMark(){
		String recipEmail = "markdavis116@gmail.com";
		setFrom(recipEmail);
		addRecip(recipEmail);
	}
	
	public int send(){
		int sent = 0;
		for(String email : recipArr){
			System.out.println("ABOUT TO SEND TO " + email);
			ClientConfig cc = new DefaultClientConfig();
			Client client = null;
			cc.getClasses().add(MultiPartWriter.class);
			client = client.create(cc);
			client.addFilter(new HTTPBasicAuthFilter("api", this.apiKey));
			WebResource webResource = client.resource("https://api.mailgun.net/v2/" + this.domain + "/messages");
			FormDataMultiPart formData = new FormDataMultiPart();
			formData.field("from", this.from);
		    formData.field("to", email);
		    formData.field("subject", this.subject);
		    if(this.html != null){
		    	formData.field("html", this.html);
		    }else{
		    	formData.field("text", this.text);
		    }
		    if(this.attachment != null){
		    	webResource.setProperty("type", MediaType.MULTIPART_FORM_DATA);
		    	System.out.println("IN ATTACHMENT IF");
		    	formData.field("attachment", this.attachment);
		    	File attachmentFile = new File(this.attachment);
		    	if(attachmentFile.exists()){
		    		System.out.println("File exists");
		    	}else{
		    		System.out.println("FILE DOES NOT EXIST");
		    	}
		    	FileDataBodyPart bodyPart = new FileDataBodyPart();
		    	bodyPart.setName("attachment");
		    	bodyPart.setFileEntity(attachmentFile, new MediaType("application","octet-stream"));
		    	bodyPart.setMediaType(MediaType.APPLICATION_OCTET_STREAM_TYPE);
		    	formData.bodyPart(bodyPart);
		    }else{
		    	webResource.setProperty("type", MediaType.APPLICATION_FORM_URLENCODED);
		    }

		    System.out.println("ABOUT TO POST");
		    ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, formData);
	
		    
		   
		}
		return sent;
		
	}

	

}