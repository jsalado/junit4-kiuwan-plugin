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
 * This rule load 'cobertura' report and generates kiuwan violations for each failed condition.
 */
public class CoberturaKiuwanPlugin extends AbstractRule { 
	private final static Logger logger = Logger.getLogger(CoberturaKiuwanPlugin.class);

	private String COBERTURA_REPORT_NAME = "coverage.xml";
	
	public void initialize (RuleContext ctx) { 
		super.initialize(ctx);	
		
		// basedir.
		File baseDir = ctx.getBaseDirs().get(0);

		logger.debug("initialize: " +  this.getName() + " : " + baseDir);
	}

	
	protected void visit (BaseNode root, final RuleContext ctx) { 
		// this method is run once for each source file under analysis.
		// this method is left in blank intentionally.
	}
	
	
	public void postProcess (RuleContext ctx) { 
		// this method is run once for analysis
		super.postProcess(ctx); 
		logger.debug("postProcess: " +  this.getName());
		
		// basedir.
		File baseDir = ctx.getBaseDirs().get(0);
		
		// iterates over 'cobertura' reports files.
		try {
			Files.walk(Paths.get(baseDir.getAbsolutePath()))
				.filter(Files::isRegularFile)
				.filter(p -> p.getFileName().toString().equals(COBERTURA_REPORT_NAME))
				.forEach(p -> {
					try {
						processCoberturaReportFile(ctx, p);
					} catch (ParserConfigurationException | SAXException | IOException e) {
						logger.error("Error parsing file " + p.getFileName() + ". ", e);
					}
				});
		} catch (IOException e) {
			logger.error("", e);
		}
	}


	private void processCoberturaReportFile(RuleContext ctx, Path p) throws ParserConfigurationException, SAXException, IOException {
		logger.debug("processing: " +  p);

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		CoberturaReportHandler handler = new CoberturaReportHandler(ctx, p.toFile());
		
		parser.parse(p.toFile(), handler);
	}	
	
	
	/**
	 * The cobertura xml report handler
	 * @author mcprol
	 */
	class CoberturaReportHandler extends DefaultHandler {
		private RuleContext ctx;
		private File file;
		private Locator locator = null;
		
		// element info used as cache.
		private int lineNumber;
		
		public CoberturaReportHandler(RuleContext ctx, File file) {
			super();
			this.ctx = ctx;
			this.file = file;
			
			this.setDocumentLocator(locator);
		}
		
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}
		  
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			// logger.debug("CoberturaReportHandler.startElement(" + uri + ", " + localName + ", "+ qName + ")");
			if ("class".equalsIgnoreCase(qName)) {
				String name = attributes.getValue("name");
				String filename = attributes.getValue("filename");
				String linerate = attributes.getValue("line-rate");
				logger.debug("JunitReportHandler.startElement(" + "class" + ", " + name + ", "+ filename + ", "+ linerate + ")");
								
				if (locator != null) {
					lineNumber = locator.getLineNumber();
				} else {
					lineNumber = 0;
				}
				
				addClassCoverageViolation(name, filename, linerate);
			}
		}
		

		private void addClassCoverageViolation(String name, String filename, String linerate) {
			Rule rule = ctx.getRules().getRuleByName("CUS.MCP.KIUWAN.RULES.COBERTURA.ClassCoverage");
			if (null != rule) {
				double rate = Double.parseDouble(linerate);
				double threshold = rule.getDoubleProperty("threshold");
				logger.debug("addClassCoverageViolation(" + rate + ", " + threshold + ")");
				
				if (threshold > rate) {
					RuleViolation rv = new RuleViolation(rule, lineNumber, file);
					rv.setCodeViolated(name + " at " + filename);
					rv.setExplanation("line-rate: " + linerate); 

					ctx.getReport().addRuleViolation(rv);			
				}
			}
		}

	}
}



