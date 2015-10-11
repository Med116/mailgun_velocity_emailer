package velocity;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import base.Email;

public class VelocityUtil {

	
	public static VelocityEngine  getVelocityEngine(){
		
		VelocityEngine ve = new VelocityEngine();
		Properties props = new Properties();
		String path = Email.getVelocityTemplatePath();
        props.put("file.resource.loader.path", path);
        props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		ve.init(props);
		return ve;
	}
	
	public static String getHtmlByTemplateAndContext(String templateName, VelocityContext context){
			
		VelocityEngine ve = getVelocityEngine();
		
		Template template = ve.getTemplate(templateName);
		
		  StringWriter writer = new StringWriter();
		  template.merge(context, writer );
	      System.out.println( writer.toString());
	      String velocityHtml = writer.toString();
	      return velocityHtml;
	}
	
	
	
	
}
