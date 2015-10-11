package base;

import java.util.List;

import org.apache.velocity.VelocityContext;
import org.springframework.remoting.soap.SoapFaultException;

import models.Legislator;
import models.Recipient;

public interface IEmail {

	public <T> List<T> getRecipients();
	public void sendEmail();
	void setTemplateName(String template);
	public VelocityContext getContext(Recipient recipientObject);

	
}
