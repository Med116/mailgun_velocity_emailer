package base;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;

import mailgun.MailGunSender;
import models.Recipient;
import velocity.VelocityUtil;

public abstract class Email implements IEmail {
	
	private static String velocityTemplatePath;
	private VelocityEngine ve;
	protected static String templateName = "";
	protected List recipientList;
	private  VelocityContext context = null;
	private String subjectLine;
	private String fromEmail = "markdavis116@gmail.com";
	private String firstNameTemplateField = "firstName";
	private String lastNameTemplateField = "lastName";
	private String fullNameTemplateField = "fullName";
	private int waitBetweenSeconds = 5;
	private String mailgunKey;
	private String mailgunDomain;
	
	
	
	public void addRecipients(List recipientList){
		this.setRecipientList(recipientList);
	}
	
	public Email(String subjectLine, String templateBaseName){
	
		setUpProperties();
		setTemplateName(templateBaseName);
		setSubjectLine(subjectLine);
		this.setRecipientList(getRecipients());
	}
	

	public String getTemplateName() {
		return templateName;
	}

	public List getRecipients() {
		System.out.println("WARNING: TEST LIST ONLY , OVERRIDE getRecipients in Child Class TO GET FULL LIST OF RECIPIENTS");
		ArrayList<Recipient> markOnlyList = new ArrayList<Recipient>();
		Recipient recipient = new Recipient("markdavis116@gmail.com");
		recipient.setFirstName("MARK");
		recipient.setLastName("DAVIS");
		recipient.setTitle("MR.");
		markOnlyList.add(recipient);
		markOnlyList.add(recipient);
		System.out.println(" EMAIL PARENT  : ADDED TEST USER, REPLACE getRecipients() call with ScraperMALegs for send time");
		return markOnlyList;
	}


	 public void setTemplateName(String templateName) {
		 	DateFormat sqlDateFormat = new SimpleDateFormat("yyyy_MM_dd");
			String dateStrToday = sqlDateFormat.format(new Date());
			String fileName = templateName.concat("_" + dateStrToday + ".vm");
			System.out.println("TEMPLATE FILE NAME EXPECTED :" + fileName);
			File file;
			file = new File(getVelocityTemplatePath() + fileName);
			if(!file.exists()){
				System.out.println("TEMPLATE FILE IS MISSING , MAKE SURE ITS APPENDED BY TODAYS DATE : IE: template_title_" + dateStrToday + ".vm");
				System.exit(0);
			}
			this.templateName = fileName;
	}


	protected ArrayList<Recipient> getRecipientList() {
		return (ArrayList<Recipient>) recipientList;
	}


	protected void setRecipientList(List recipientList) {
		this.recipientList = recipientList;
	}




	private void setContext(VelocityContext context) {
		this.context = context;
	}


	protected String getSubjectLine() {
		return subjectLine;
	}


	public VelocityContext getContext(Recipient recipient){
		VelocityContext context = new VelocityContext();
		
		context.put(getFirstNameTemplateField(), recipient.getFirstName());
		context.put(getLastNameTemplateField(), recipient.getLastName());
		context.put("title", recipient.getTitle());
		System.out.println("FULL NAME TEMPLATE FIELD: " + getFullNameTemplateField());
		context.put(getFullNameTemplateField(), recipient.getFirstName() + " " + recipient.getLastName());
		
		return context;
	}

	private String getFullNameTemplateField() {
		
		return fullNameTemplateField;
	}

	protected void setSubjectLine(String subjectLine) {
		this.subjectLine = subjectLine;
	}
	
	public void sendEmail() {
	
		if(getRecipientList().isEmpty()){
			System.out.println("No Recipients! Make sure to fill in the getRecipients() method in your email subclass.");
			System.exit(0);
		}
		int emailCount = 0;
		
		for(Recipient recip : getRecipientList()){
			emailCount++;
			String htmlEmail = null;
			try{
				htmlEmail = VelocityUtil.getHtmlByTemplateAndContext(getTemplateName(), getContext(recip));
			}catch(VelocityException e){
				System.out.println("LOG ERROR: " + e.getMessage());
			}
			
			MailGunSender sender = new MailGunSender(getMailgunKey(), getMailgunDomain());
			sender.setFrom(getFromEmail());
			if(htmlEmail == null){
				System.out.println("HTML EMAIL NULL, EXIT");
				System.exit(0);
			}
			sender.setHtml(htmlEmail);
			sender.addRecip(recip.getRecipientEmail());
			sender.setSubject(getSubjectLine());
			sender.send();
			if(emailCount < getRecipients().size()){
				try{
					System.out.println("Waiting " + waitBetweenSeconds + " before next email is sent");
					Thread.sleep((long) waitBetweenSeconds * 1000);
				}catch(Exception e){
					e.getMessage();
				}
			}
		}
		System.out.println("ALL " + emailCount + " EMAILS HAVE BEEN SENT OUT OF TOTAL "  + getRecipients().size());
		
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public void setFirstNameForTemplate(String firstNameField) {
		setFirstNameForTemplate(firstNameField);
		
	}

	public void setLastNameForTemplate(String lastName) {
		setLastNameTemplateField(lastName);
		
	}

	public String getFirstNameTemplateField() {
		return firstNameTemplateField;
	}

	public void setFirstNameTemplateField(String firstNameTemplateField) {
		this.firstNameTemplateField = firstNameTemplateField;
	}

	public String getLastNameTemplateField() {
		return lastNameTemplateField;
	}

	public void setLastNameTemplateField(String lastNameTemplateField) {
		this.lastNameTemplateField = lastNameTemplateField;
	}

	public void setFullNameTemplateField(String fullNameTemplateField) {
		this.fullNameTemplateField = fullNameTemplateField;
	}

	public int getWaitBetweenSeconds() {
		return waitBetweenSeconds;
	}

	public void setWaitBetweenSeconds(int waitBetweenSeconds) {
		this.waitBetweenSeconds = waitBetweenSeconds;
	}
	public void setWait(int wait){
		setWaitBetweenSeconds(wait);
	}
	
	private void setUpProperties() {
		Properties props = new Properties();
		String templatePathStr = null;
		InputStream input = null;
		
		try{
			
			input = new FileInputStream("src/main/resources/application.properties");
			props.load(input);
			templatePathStr = props.getProperty("templateAbsolutePath");
			System.out.println("SETTING TEMPLATE PROPERTY NAME TO " + templatePathStr);
			setVelocityTemplatePath(templatePathStr);
			String mailgunKey = props.getProperty("mailgunKey");
			setMailgunKey(mailgunKey);
			String mailgunDomain = props.getProperty("mailgunDomain");
			setMailgunDomain(mailgunDomain);
			
		}catch(IOException iox){
			iox.printStackTrace();
		}finally{
			if(input != null){
				try{
					input.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		
	}

	public static String getVelocityTemplatePath() {
		return velocityTemplatePath;
	}

	public static void setVelocityTemplatePath(String velocityTemplatePath) {
		Email.velocityTemplatePath = velocityTemplatePath;
	}

	public String getMailgunKey() {
		return mailgunKey;
	}

	public void setMailgunKey(String mailgunKey) {
		this.mailgunKey = mailgunKey;
	}

	public String getMailgunDomain() {
		return mailgunDomain;
	}

	public void setMailgunDomain(String mailgunDomain) {
		this.mailgunDomain = mailgunDomain;
	}
}
