// MIT License
//
// Copyright (c) 2018 Marcos Cacabelos Prol
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package mcp.kiuwan.rules.junit;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.als.core.AbstractRule;
import com.als.core.Rule;
import com.als.core.RuleContext;
import com.als.core.RuleViolation;
import com.als.core.ast.BaseNode;


/**
 * @author mcprol
 * 
 * This rule load junit4 report and generates kiuwan violations for each failed test.
 * All junit 'violations' are fired by Junit4KiuwanPlugin by itself.
 * 
 * All junit ruledefs (under resouces/ruledef directory) use a NullRule in its definition, to avoid warning messages when running.
 */
public class Junit4KiuwanPlugin extends AbstractRule { 
	private final static Logger logger = Logger.getLogger(Junit4KiuwanPlugin.class);

	private String JUNIT_REPORT_PREFIX = "TEST-";
	
	private String junitReportsDirectory = "../target/surefire-reports";
	private String junitReportsBaseDir;
	

	public void initialize (RuleContext ctx) { 
		super.initialize(ctx);	
		
		// calculates junit reports directory.
		File baseDir = ctx.getBaseDirs().get(0);
		junitReportsBaseDir = new File(baseDir, junitReportsDirectory).getAbsolutePath();
		
		logger.debug("initialize: " +  this.getName() + " : " + junitReportsBaseDir);
	}

	
	protected void visit (BaseNode root, final RuleContext ctx) { 
		// this method is run once for each source file under analysis.
		// this method is left in blank intentionally.
	}
	
	
	public void postProcess (RuleContext ctx) { 
		// this method is run once for analysis
		super.postProcess(ctx); 
		logger.debug("postProcess: " +  this.getName());
		
		// iterates over junit reports files.
		try {
			DirectoryStream<Path> newDirectoryStream = Files.newDirectoryStream(Paths.get(junitReportsBaseDir), JUNIT_REPORT_PREFIX + "*");
			newDirectoryStream.forEach(p -> {
				try {
					processJunitReportFile(ctx, p);
				} catch (ParserConfigurationException | SAXException | IOException e) {
					logger.error("Error parsing file " + p.getFileName() + ". ", e);
				}
			});
		} catch (IOException e) {
			logger.error("", e);
		}
	}


	private void processJunitReportFile(RuleContext ctx, Path p) throws ParserConfigurationException, SAXException, IOException {
		logger.debug("processing: " +  p);

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		JunitReportHandler handler = new JunitReportHandler(ctx, p.toFile());
		
		parser.parse(p.toFile(), handler);
	}	
	
	
	/**
	 * The JUNIT4 xml report handler
	 * @author mcprol
	 */
	class JunitReportHandler extends DefaultHandler {
		private RuleContext ctx;
		private File file;
		private Locator locator = null;
		
		// element info used as cache.
		private String classname;
		private String name;
		private String time;
		private String message;
		private String testName;
		private int lineNumber;
		private boolean isFailure;
		
		public JunitReportHandler(RuleContext ctx, File file) {
			super();
			this.ctx = ctx;
			this.file = file;
			
			this.setDocumentLocator(locator);
		}
		
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}
		  
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// logger.debug("JunitReportHandler.startElement(" + uri + ", " + localName + ", "+ qName + ")");
			if ("testcase".equalsIgnoreCase(qName)) {
				classname = attributes.getValue("classname");
				name = attributes.getValue("name");
				time = attributes.getValue("time");
				logger.debug("JunitReportHandler.startElement(" + "testcase" + ", " + classname + ", "+ name + ", "+ time + ")");
				
				testName = classname + "." + name;
				
				if (locator != null) {
					lineNumber = locator.getLineNumber();
				} else {
					lineNumber = 0;
				}
				
				isFailure = false;
			}

			if ("failure".equalsIgnoreCase(qName)) {
				isFailure = true;
				message = attributes.getValue("message");
			}
		}
		
		
	    public void endElement (String uri, String localName, String qName) throws SAXException {
			if ("testcase".equalsIgnoreCase(qName)) {
				addSlowTestViolation();
				if (isFailure) {
					addFailedTestViolation();
				}
			}
	    }
		

		private void addSlowTestViolation() {
			Rule rule = ctx.getRules().getRuleByName("CUS.MCP.KIUWAN.RULES.JUNIT.SlowTest");
			if (null != rule) {
				double elapsedTime = Double.parseDouble(time);
				double maxTestElapsedTime = rule.getDoubleProperty("maxTestElapsedTime");
				logger.debug("addSlowTestViolation(" + elapsedTime + ", " + maxTestElapsedTime + ")");
				
				if (elapsedTime > maxTestElapsedTime) {
					RuleViolation rv = new RuleViolation(rule, lineNumber, file);
					rv.setCodeViolated(testName);
					rv.setExplanation("time: " + time);

					ctx.getReport().addRuleViolation(rv);			
				}
			}
		}

		
		private void addFailedTestViolation() {
			Rule rule = ctx.getRules().getRuleByName("CUS.MCP.KIUWAN.RULES.JUNIT.TestFailed");
			if (null != rule) {
				RuleViolation rv = new RuleViolation(rule, lineNumber, file);
				rv.setCodeViolated(testName);
				rv.setExplanation("message: " + message);

				ctx.getReport().addRuleViolation(rv);			
			}
		}

	}
}



