package rfromjava.com.rchart;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@RequestMapping( "/" )
	public ModelAndView homePage() {
		
		return new ModelAndView( "homepage" );
	}

	
	@ResponseBody
	@RequestMapping( value = "DisplayChart", method = RequestMethod.POST )
	public String showChartHTML( @RequestParam Map<String,String> requestParams ) throws Exception {

		Charset encoding = StandardCharsets.UTF_8; // Set the encoding we will use in our files.
		File file = new File( "" );
		String tomcatAbsoluteBinPath = file.getAbsolutePath().replaceAll( "\\\\", "/" ); // If Windows, it will replace.
		String[] parts = tomcatAbsoluteBinPath.split("/"); // Splitting Tomcat path to get the home folder name.
				
		// Reading the chart template.
		byte[] rMarkDownCode = Files.readAllBytes( Paths.get( "/" + parts[1] 
								+ "/webapps/RChartFromJava/WEB-INF/classes/static/ChartTemplates/QualityControl_IndividualRangeChart.Rmd" ) );
		String chartTemplate = new String( rMarkDownCode, encoding );
		
		// Assign values we got from our form into our RMarkdown template
		chartTemplate = chartTemplate.replace( "measurementValues", requestParams.get( "chartdata" ).toString() );
		chartTemplate = chartTemplate.replace( "LowerSpecificationLimitValue", requestParams.get( "lsl" ).toString() );
		chartTemplate = chartTemplate.replace( "UpperSpecificationLimitValue", requestParams.get( "usl" ).toString() );
		
		try {
			File temporaryRMarkDownChartFile = File.createTempFile( "IMR_", ".rmd", new File( "/" + parts[1] 
												+ "/webapps/RChartFromJava/WEB-INF/classes/static/tmp" ) ); // Get new temporary file
			System.out.println( "Chart temporary file:" + temporaryRMarkDownChartFile.getAbsolutePath() );
			// Write data to new RMarkdown file.
			Files.write( Paths.get( temporaryRMarkDownChartFile.getAbsolutePath() ), chartTemplate.getBytes() ); 
			
			// Running Rscript to render the RMarkdown file to html file.
			Process child = Runtime.getRuntime().exec( requestParams.get( "rScriptPath" ).toString() 
							+ " -e \"rmarkdown::render('" + temporaryRMarkDownChartFile.getAbsolutePath().replaceAll( "\\\\", "/" ) + "')\"");
			int code = 0;
			
			try {
				code = child.waitFor();
			} catch (Exception e) { e.printStackTrace(); }
			
			switch (code) {
		    case 0:
				byte[] chartHTMLCode = Files.readAllBytes( Paths.get( temporaryRMarkDownChartFile.getAbsolutePath().replace( ".rmd", ".html" ) ) );				
				return new String( chartHTMLCode, encoding );
		    case 1:
		    	System.out.println( "\n\n Error creating html file from Rmd file:" + temporaryRMarkDownChartFile.getAbsolutePath() );
		    	System.out.println( "R markdown file creation command:" + requestParams.get( "rScriptPath" ).toString() 
		    						+ " -e \"rmarkdown::render('" + temporaryRMarkDownChartFile.getAbsolutePath() + "')\"" );
			}
			
		} catch (IOException e) { e.printStackTrace(); }
		
		return ( "Error processing chart." ); // If chart RMarkdown file could not be rendered and html data returned. 
	}

}

