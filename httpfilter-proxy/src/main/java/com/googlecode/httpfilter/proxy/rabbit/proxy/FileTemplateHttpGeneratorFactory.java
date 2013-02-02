package com.googlecode.httpfilter.proxy.rabbit.proxy;

import java.io.File;
import com.googlecode.httpfilter.proxy.rabbit.util.SProperties;
import java.util.logging.Logger;

/**
 * A HttpGeneratorFactory that creates FileTemplateHttpGenerator instances.
 * 
 * @author <a href="mailto:robo@khelekore.org">Robert Olofsson</a>
 */
public class FileTemplateHttpGeneratorFactory implements HttpGeneratorFactory {
	private File dir;

	public HttpGenerator create(String identity, Connection con) {
		if (dir != null)
			return new FileTemplateHttpGenerator(identity, con, dir);
		return new StandardResponseHeaders(identity, con);
	}

	public void setup(SProperties props) {
		String templateDir = props.get("error_pages");
		dir = new File(templateDir);
		if (!dir.exists()) {
			dir = null;
			Logger logger = Logger.getLogger(getClass().getName());
			logger.warning("Failed to find error pages directory: "
					+ templateDir);
		}
	}
}
