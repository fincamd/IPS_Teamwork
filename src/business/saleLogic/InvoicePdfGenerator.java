package business.saleLogic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import common.Conf;
import common.Dates;
import dtos.ClientDto;
import dtos.SaleDto;
import factories.ServiceFactory;
import wrappers.ProductDtoWrapper;

public class InvoicePdfGenerator {

	// Fields
	// ------------------------------------------------------------------------

	private List<ProductDtoWrapper> productsOnBreakdown;
	private SaleDto saleToInvoice;
	private Conf conf;

	// Constructor
	// ------------------------------------------------------------------------

	public InvoicePdfGenerator(SaleDto saleToInvoice, List<ProductDtoWrapper> productsOnBreakdown) {
		this.saleToInvoice = saleToInvoice;
		this.productsOnBreakdown = productsOnBreakdown;
	}

	// Basic methods
	// ------------------------------------------------------------------------

	public String generate() {
		String companyData = getCompanyData();
		String clientData = getClientData();
		String fileName = getFileName(clientData);

		Font font1 = new Font(FontFamily.HELVETICA, 10);
		Font font2 = new Font(FontFamily.HELVETICA, 20);
		Font font3 = new Font(FontFamily.HELVETICA, 16);
		Font font4 = new Font(FontFamily.HELVETICA, 13);

		try {
			// Initialize document
			Document document = new Document();
			String pathToFile = getPathToFile(fileName);
			PdfWriter.getInstance(document, new FileOutputStream(new File(pathToFile)));
			document.open();

			addMetadata(document);
			addInvoiceTitle(document, font2);
			addCompanyLogo(document);
			addTransactionPartsData(companyData, clientData, document, font1);
			addProductBreakdown(document, font1, font3, font4);
			addInvoiceTotal(font1, font3, document);

			document.close();
		} catch (IOException | DocumentException | URISyntaxException ex) {
			return "Algo salió mal en la creación de la factura";
		}

		return fileName;
	}

	// Algorithm breakdown
	// ------------------------------------------------------------------------

	private void addInvoiceTotal(Font font1, Font font3, Document document) throws DocumentException {
		Paragraph categoryTitle = new Paragraph("Total de la factura:", font3);
		document.add(categoryTitle);
		PdfPTable invoiceTotalTable = new PdfPTable(4);
		insertCell(invoiceTotalTable, "Total factura: ", Element.ALIGN_RIGHT, 3, font1);
		insertCell(invoiceTotalTable, getInvoiceTotal() + "€", Element.ALIGN_LEFT, 1, font1);
		Paragraph invoiceTotalParagraph = new Paragraph();
		invoiceTotalParagraph.add(invoiceTotalTable);
		document.add(invoiceTotalParagraph);
	}

	private void addProductBreakdown(Document document, Font font1, Font font3, Font font4) throws DocumentException {
		Paragraph productBreakdownTitle = new Paragraph("Desglose de productos por categorías:", font3);
		document.add(productBreakdownTitle);
		for (String category : getUniqueCategories()) {
			Paragraph categoryTitle = new Paragraph(category + ":", font4);
			document.add(categoryTitle);
			PdfPTable productBreakdownTableForCategory = new PdfPTable(4);
			writeTableHeaders(productBreakdownTableForCategory);
			for (ProductDtoWrapper product : productsOnBreakdown) {
				if (product.getDto().category.equalsIgnoreCase(category)) {
					Stream.of(product.getDto().name, product.getDto().publicPrice + "€", product.quantityOrdered,
							product.quantityOrdered * product.getDto().publicPrice + "€").forEach(member -> {
								insertCell(productBreakdownTableForCategory, String.valueOf(member), Element.ALIGN_LEFT,
										1, font1);
							});
				}
			}
			Paragraph paragraphProductBreakdown = new Paragraph();
			paragraphProductBreakdown.add(productBreakdownTableForCategory);
			document.add(paragraphProductBreakdown);
		}
	}

	private void addTransactionPartsData(String companyData, String clientData, Document document, Font font1)
			throws DocumentException {
		PdfPTable companyDataAndLogoTable = new PdfPTable(2);
		insertCell(companyDataAndLogoTable, "Datos del vendedor:" + '\n' + companyData, Element.ALIGN_LEFT, 1, font1);
		insertCell(companyDataAndLogoTable, "Datos del cliente:" + '\n' + clientData, Element.ALIGN_LEFT, 1, font1);
		Paragraph paragraphTransactionData = new Paragraph();
		paragraphTransactionData.add(companyDataAndLogoTable);
		document.add(paragraphTransactionData);
	}

	private void addCompanyLogo(Document document)
			throws URISyntaxException, BadElementException, MalformedURLException, IOException, DocumentException {
		Path path = Paths.get(ClassLoader.getSystemResource("resources/appicon.png").toURI());
		Image img = Image.getInstance(path.toAbsolutePath().toString());
		img.scalePercent(15f);
		document.add(img);
	}

	private void addInvoiceTitle(Document document, Font font2) throws DocumentException {
		Paragraph paragraphTitle = new Paragraph("Factura para la venta con identificador: " + saleToInvoice.id, font2);
		document.add(paragraphTitle);
	}

	private void addMetadata(Document document) {
		document.addCreationDate();
	}

	// Auxiliary methods
	// ------------------------------------------------------------------------

	private String getFileName(String clientData) {
		StringBuilder fileName = new StringBuilder();
		fileName.append(clientData.split("\n")[0].replaceAll(" ", "-"));
		fileName.append('_');
		fileName.append(Dates.toString(new Date()).replaceAll("/", "-"));
		fileName.append(".pdf");
		return fileName.toString();
	}

	private String getPathToFile(String fileName) throws IOException {
		String pathToFile = new File(".").getCanonicalPath();
		pathToFile += "/invoices/ventaNo" + saleToInvoice.id + '_';
		pathToFile += fileName;
		return pathToFile;
	}

	private String getCompanyData() {
		StringBuilder companyData = new StringBuilder();
		conf = Conf.getInstance("configs/companyData.properties");
		companyData.append(conf.getProperty("NAME") + '\n');
		companyData.append(conf.getProperty("STREET") + '\n');
		companyData.append(conf.getProperty("CITY") + '\n');
		companyData.append("CIF/NIF: " + conf.getProperty("CIF") + '\n');
		companyData.append("CP: " + conf.getProperty("CP") + '\n');
		return companyData.toString();
	}

	private String getClientData() {
		ClientDto client = ServiceFactory.createClientService().findById(saleToInvoice.clientId);
		StringBuilder clientData = new StringBuilder();
		clientData.append(client.name + '\n');
		clientData.append("NIF: " + client.dni + '\n');
		clientData.append("Calle: " + client.street + '\n');
		clientData.append("CP: " + client.postCode);
		return clientData.toString();
	}

	private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
		cell.setHorizontalAlignment(align);
		cell.setColspan(colspan);
		if (text.trim().equalsIgnoreCase("")) {
			cell.setMinimumHeight(10f);
		}
		table.addCell(cell);
	}

	private void writeTableHeaders(PdfPTable table) {
		Stream.of("Nombre", "Precio por unidad", "Unidades", "Total producto").forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setPhrase(new Phrase(columnTitle));
			table.addCell(header);
		});
	}

	private List<String> getUniqueCategories() {
		List<String> uniqueCategories = new ArrayList<String>();

		for (ProductDtoWrapper product : productsOnBreakdown) {
			if (!uniqueCategories.contains(product.getDto().category)) {
				uniqueCategories.add(product.getDto().category);
			}
		}

		return uniqueCategories;
	}

	private String getInvoiceTotal() {
		DecimalFormat format = new DecimalFormat("0.00");
		double totalInvoice = 0.0d;

		for (ProductDtoWrapper product : productsOnBreakdown) {
			totalInvoice += product.quantityOrdered * product.getDto().publicPrice;
		}

		return String.valueOf(format.format(totalInvoice));
	}

}
