	package org.springframework.samples.petclinic.owner;

	import com.lowagie.text.*;
	import com.lowagie.text.Font;
	import com.lowagie.text.pdf.PdfPCell;
	import com.lowagie.text.pdf.PdfPTable;
	import com.lowagie.text.pdf.PdfWriter;
	import com.lowagie.text.pdf.draw.LineSeparator;
	import org.springframework.stereotype.Service;

	import java.awt.*;
	import java.io.ByteArrayOutputStream;
	import java.util.stream.Stream;

	@Service
	public class OwnerReportPdfService {

		public byte[] generateOwnerReport(Owner owner) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Document document = new Document();
				PdfWriter.getInstance(document, baos);
				document.open();

				Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
				Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
				Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

				LineSeparator linha = new LineSeparator();

				// título
				Paragraph title = new Paragraph("Owner's Report", titleFont);
				title.setAlignment(Element.ALIGN_CENTER);
				title.setSpacingAfter(10);
				document.add(title);

				// dados do owner
				document.add(paragraphCentered("Name: " + owner.getFirstName() + " " + owner.getLastName(), normalFont));
				document.add(paragraphCentered("Address: " + owner.getAddress(), normalFont));
				document.add(paragraphCentered("City: " + owner.getCity(), normalFont));
				document.add(paragraphCentered("Telephone: " + owner.getTelephone(), normalFont));
				document.add(new Paragraph(" "));

				// pets
				if (!owner.getPets().isEmpty()) {
					for (Pet pet : owner.getPets()) {
						document.add(linha);

						document.add(new Paragraph("Pet: " + pet.getName(), boldFont));
						document.add(new Paragraph("Type: " + pet.getType().getName() + " | Birth Date: " + pet.getBirthDate(), normalFont));
						document.add(new Paragraph("Number of visits: " + pet.getVisits().size(), normalFont));
						document.add(new Paragraph(" "));

						// tabela para visitas de um pet
						if (!pet.getVisits().isEmpty()) {
							PdfPTable table = new PdfPTable(2);
							Stream.of("Visit Date", "Description")
								.forEach(header -> {
									PdfPCell cell = new PdfPCell(new Phrase(header, boldFont));
									cell.setHorizontalAlignment(Element.ALIGN_CENTER);
									cell.setBackgroundColor(new Color(230, 230, 230));
									cell.setPadding(5);
									table.addCell(cell);
								});

							for (Visit visit : pet.getVisits()) {
								table.addCell(normalCell(visit.getDate().toString()));
								table.addCell(normalCell(visit.getDescription()));
							}
							document.add(table);
						}
						document.add(new Paragraph(" "));
					}
				}

				document.close();
				return baos.toByteArray();
			} catch (DocumentException e) {
				throw new RuntimeException("Error generating PDF", e);
			}
		}

		private Paragraph paragraphCentered(String text, Font font)  {
			Paragraph p = new Paragraph(text, font);
			p.setAlignment(Element.ALIGN_CENTER);
			return p;
		}

		private PdfPCell normalCell(String text) {
			Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 11);
			PdfPCell cell = new PdfPCell(new Phrase(text, fontNormal));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setPadding(5);
			return cell;
		}
	}
