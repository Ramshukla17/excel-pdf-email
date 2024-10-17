package com.ashok.file.generator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException; // Import for handling IO exceptions
import java.util.List; // Import for List data structure

import org.apache.poi.hssf.usermodel.HSSFRow; // Import for Excel row manipulation
import org.apache.poi.hssf.usermodel.HSSFSheet; // Import for Excel sheet manipulation
import org.apache.poi.hssf.usermodel.HSSFWorkbook; // Import for Excel workbook manipulation
import org.springframework.beans.factory.annotation.Autowired; // Import for Spring's dependency injection
import org.springframework.data.domain.Example; // Import for Query By Example (QBE)
import org.springframework.stereotype.Service; // Import for Service annotation

import com.ashok.file.generator.binding.SearchCriteria; // Import for search criteria binding
import com.ashok.file.generator.entity.CitizenPlan; // Import for CitizenPlan entity
import com.ashok.file.generator.repository.CitizenPlanRepository; // Import for CitizenPlan repository
import com.ashok.file.generator.utils.EmailUtils;
import com.lowagie.text.Document; // Import for PDF document creation
import com.lowagie.text.Element; // Import for PDF element alignment
import com.lowagie.text.Font; // Import for PDF font styling
import com.lowagie.text.FontFactory; // Import for creating fonts
import com.lowagie.text.PageSize; // Import for setting PDF page size
import com.lowagie.text.Paragraph; // Import for PDF paragraph creation
import com.lowagie.text.Phrase; // Import for PDF phrase creation
import com.lowagie.text.pdf.CMYKColor; // Import for CMYK color model
import com.lowagie.text.pdf.PdfPCell; // Import for PDF table cell
import com.lowagie.text.pdf.PdfPTable; // Import for PDF table
import com.lowagie.text.pdf.PdfWriter; // Import for PDF writing

import io.micrometer.common.util.StringUtils; // Import for string utility methods
import jakarta.servlet.ServletOutputStream; // Import for servlet output stream
import jakarta.servlet.http.HttpServletResponse; // Import for HTTP response

// Service annotation indicates that this class provides business logic
@Service
public class CitizenPlanServiceImpl implements CitizenPlanService {

	// Autowire the CitizenPlanRepository to perform database operations
	@Autowired
	private CitizenPlanRepository planRepository;

	@Autowired
	private EmailUtils emailUtils;

	// Method to retrieve all available plan names from the repository
	@Override
	public List<String> getPlanNames() {
		return planRepository.getPlanNames(); // Fetch and return plan names
	}

	// Method to retrieve all available plan statuses from the repository
	@Override
	public List<String> getPlanStatus() {
		return planRepository.getPlanStatus(); // Fetch and return plan statuses
	}

	// Method to search for citizens based on provided search criteria
	@Override
	public List<CitizenPlan> searchCitizens(SearchCriteria criteria) {
		// Create a new CitizenPlan entity to hold search parameters
		CitizenPlan entity = new CitizenPlan();

		// Set plan name if provided in search criteria
		if (StringUtils.isNotBlank(criteria.getPlanNames())) {
			entity.setPlanNames(criteria.getPlanNames());
		}

		// Set plan status if provided in search criteria
		if (StringUtils.isNotBlank(criteria.getPlanStatus())) {
			entity.setPlanStatus(criteria.getPlanStatus());
		}

		// Set gender if provided in search criteria
		if (StringUtils.isNotBlank(criteria.getGender())) {
			entity.setGender(criteria.getGender());
		}

		// Set plan start date if provided
		if (criteria.getPlanStartDate() != null) {
			entity.setPlanStartDate(criteria.getPlanStartDate());
		}

		// Set plan end date if provided
		if (criteria.getPlanEndDate() != null) {
			entity.setPlanEndDate(criteria.getPlanEndDate());
		}

		// Create an Example object for Query By Example (QBE) to filter data
		Example<CitizenPlan> example = Example.of(entity);

		// Return the list of CitizenPlan entities matching the search criteria
		return planRepository.findAll(example);
	}

	// Method to generate an Excel file
	@Override
	public void generateExcellFile(HttpServletResponse response) throws IOException {
		// Retrieve all citizen plan records from the repository
		List<CitizenPlan> records = planRepository.findAll();

		// Create a new workbook and sheet for Excel
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Data");
		HSSFRow headerRow = sheet.createRow(0); // Create header row

		// Set data for header row cells
		headerRow.createCell(0).setCellValue("Name");
		headerRow.createCell(1).setCellValue("Email");
		headerRow.createCell(2).setCellValue("Plan Name");
		headerRow.createCell(3).setCellValue("Plan Status");
		headerRow.createCell(4).setCellValue("Gender");
		headerRow.createCell(5).setCellValue("SSN");

		int rowNum = 1; // Initialize row number for data rows
		// Populate the sheet with citizen plan records
		for (CitizenPlan record : records) {
			HSSFRow row = sheet.createRow(rowNum);
			row.createCell(0).setCellValue(record.getName());
			row.createCell(1).setCellValue(record.getEmail());
			row.createCell(2).setCellValue(record.getPlanNames());
			row.createCell(3).setCellValue(record.getPlanStatus());
			row.createCell(4).setCellValue(record.getGender());
			row.createCell(5).setCellValue(record.getSsn());
			rowNum++;
		}

		// to send file in email attachment
		File file = new File("data.xls");
		FileOutputStream fos = new FileOutputStream(file);
		workbook.write(fos);
		emailUtils.sendEmail(file);

		// to download file in browser
		try {
			ServletOutputStream outputStream = response.getOutputStream();
			workbook.write(outputStream);
			workbook.close(); // Close the workbook
			outputStream.close(); // Close the output stream
		} catch (Exception e) {
			e.printStackTrace(); // Handle exceptions during file generation
		}
	}

	@Override
	public void generatePdfFile(HttpServletResponse response) throws IOException {
		// Create a new PDF document with A4 page size for browser download
		Document myPdfDoc1 = new Document(PageSize.A4);
		ServletOutputStream outputStream = response.getOutputStream();
		PdfWriter.getInstance(myPdfDoc1, outputStream);
		myPdfDoc1.open(); // Open the document for writing

		// Create a new PDF document with A4 page size for email attachment
		Document myPdfDoc2 = new Document(PageSize.A4);
		File file = new File("data.pdf");
		FileOutputStream fos = new FileOutputStream(file);
		PdfWriter.getInstance(myPdfDoc2, fos);
		myPdfDoc2.open(); // Open the document for writing

		// Set up the title for both PDFs
		Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
		Paragraph title = new Paragraph("Citizen Plan Details", fontTitle);
		title.setAlignment(Element.ALIGN_CENTER);
		myPdfDoc1.add(title); // Add the title to the document for browser download
		myPdfDoc2.add(title); // Add the title to the document for email attachment

		// Create a table with 6 columns for both PDFs
		PdfPTable table = new PdfPTable(6);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 3, 3, 3, 3, 3, 3 });
		table.setSpacingBefore(5);

		// Set up the cell for table header
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(CMYKColor.CYAN);
		cell.setPadding(5);

		// Create and add header cells to the table
		Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Font.BOLD);
		font.setColor(CMYKColor.BLACK);

		cell.setPhrase(new Phrase("Name", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Email", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Plan Name", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Plan Status", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Gender", font));
		table.addCell(cell);
		cell.setPhrase(new Phrase("SSN", font));
		table.addCell(cell);

		// Retrieve the records
		List<CitizenPlan> records = planRepository.findAll();
		for (CitizenPlan record : records) {
			table.addCell(record.getName());
			table.addCell(record.getEmail());
			table.addCell(record.getPlanNames());
			table.addCell(record.getPlanStatus());
			table.addCell(record.getGender());
			table.addCell(String.valueOf(record.getSsn()));
		}

		// Now add the populated table to each document
		myPdfDoc1.add(table); // Add table for browser download
		myPdfDoc2.add(table); // Clone the table for email attachment

		myPdfDoc1.close();
		outputStream.close();

		// Send email with the attachment
		myPdfDoc2.close();
		fos.close();
		emailUtils.sendEmail(file);
	}

}
