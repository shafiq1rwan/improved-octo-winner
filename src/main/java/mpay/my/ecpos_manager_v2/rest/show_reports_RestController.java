package mpay.my.ecpos_manager_v2.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

@RestController
@RequestMapping("/report")
public class show_reports_RestController {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSource dataSource;

	@PostMapping("monthlysalesreport")
	public ResponseEntity<?> getSalesReport(@RequestBody String jsonData,HttpServletResponse response) {
		
/*		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf")); */
		ServletOutputStream outputStream = null;
		
		try {
			JSONObject dateData = new JSONObject(jsonData);

			// parse date
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
			Date convertedDate = formatter1.parse(dateData.getString("date"));
			Calendar cal = Calendar.getInstance();
			cal.setTime(convertedDate);
			int month = cal.get(Calendar.MONTH) + 1;
			int year = cal.get(Calendar.YEAR);

			// called jasperreport engine to generate the report pdf
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("month", month);
			parameters.put("year", year);

			System.out.println("Parameters Length: " + parameters.size());

			File file = new ClassPathResource("reports/EcposSalesReport.jrxml").getFile();
			InputStream monthlySalesReportStream = new FileInputStream(file);

			// InputStream monthlySalesReportStream =
			// getClass().getResourceAsStream("/EcposSalesReport.jrxml");
			JasperReport jasperReport = JasperCompileManager.compileReport(monthlySalesReportStream);
			JRSaver.saveObject(jasperReport, "EcposSalesReport.jasper");

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					dataSource.getConnection());
			
			String filename = "EcposReport" +"_"+month+"_" + ".pdf";

        	response.setContentType("application/pdf");
        	response.addHeader("Content-disposition", "filename=" + filename);
        	outputStream = response.getOutputStream();
        	
        	JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
			outputStream.flush();
		    outputStream.close();
		    
			// Expost file as pdf
			JRPdfExporter exporter = new JRPdfExporter();
			File outputFolder = createEcposReportFolder();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(
					new FileOutputStream(outputFolder + "/" + filename)));

			SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
			reportConfig.setSizePageToContent(true);
			reportConfig.setForceLineBreakPolicy(false);

			SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
			exportConfig.setEncrypted(true);
			exportConfig.setAllowedPermissionsHint("PRINTING");

			exporter.setConfiguration(reportConfig);
			exporter.setConfiguration(exportConfig);

			exporter.exportReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.OK);
		//return ResponseEntity.ok().headers(headers).body(null);
	}

	private File createEcposReportFolder() {
		File file = new File("C:/EcposReport");
		if (!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

}
