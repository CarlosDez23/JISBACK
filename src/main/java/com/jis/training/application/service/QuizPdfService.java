package com.jis.training.application.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.jis.training.domain.model.Answer;
import com.jis.training.domain.model.QuestionWithAnswers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class QuizPdfService {

    private final byte[] logoBytes;
    private static final String WATERMARK_TEXT = "Este test es propiedad el grupo de formación JIS su distribución está prohibida";

    public QuizPdfService() throws IOException {
        this.logoBytes = new ClassPathResource("assets/img.png").getContentAsByteArray();
    }

    public String generateQuizPdf(List<QuestionWithAnswers> questions) {
        // Aumentamos el margen superior (tercer parámetro) a 130 para que el logo grande tenga espacio
        Document document = new Document(PageSize.A4, 50, 50, 130, 50);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            PdfHeaderAndWatermark event = new PdfHeaderAndWatermark(logoBytes);
            writer.setPageEvent(event);

            document.open();

            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font fontRegular = FontFactory.getFont(FontFactory.HELVETICA, 10);

            int questionNumber = 1;
            for (QuestionWithAnswers question : questions) {
                Paragraph p = new Paragraph();
                p.setSpacingBefore(10f);
                p.setKeepTogether(true);
                p.add(new Chunk(questionNumber + ". " + question.getQuestionText(), fontBold));
                document.add(p);

                char label = 'a';
                for (Answer answer : question.getAnswers()) {
                    Paragraph a = new Paragraph();
                    a.setIndentationLeft(20f);
                    a.add(new Chunk("   " + label + ") " + answer.getAnswerText(), fontRegular));
                    document.add(a);
                    label++;
                }
                questionNumber++;
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error fatal al generar PDF", e);
        }

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private static class PdfHeaderAndWatermark extends PdfPageEventHelper {
        private final byte[] logoBytes;
        private PdfTemplate totalPagesPlaceholder;
        private BaseFont bf;

        public PdfHeaderAndWatermark(byte[] logoBytes) {
            this.logoBytes = logoBytes;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            totalPagesPlaceholder = writer.getDirectContent().createTemplate(30, 16);
            try {
                bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            } catch (Exception e) {
               log.info("Error fatal generando el test");
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // 1. Logo (Header) - Aumentado a 200 de ancho
            try {
                Image logo = Image.getInstance(logoBytes);
                // Tamaño aumentado significativamente
                logo.scaleToFit(200, 120);
                // Posicionamiento centrado en la parte superior
                float x = (PageSize.A4.getWidth() - logo.getScaledWidth()) / 2;
                float y = PageSize.A4.getHeight() - logo.getScaledHeight() - 20;
                logo.setAbsolutePosition(x, y);
                cb.addImage(logo);
            } catch (Exception e) { e.printStackTrace(); }

            // 2. Marca de Agua
            cb.saveState();
            cb.beginText();
            cb.setColorFill(new java.awt.Color(200, 200, 200, 45));
            cb.setFontAndSize(bf, 22);
            cb.showTextAligned(Element.ALIGN_CENTER, WATERMARK_TEXT,
                    PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2, 45);
            cb.endText();
            cb.restoreState();

            // 3. Numeración
            String text = "Página " + writer.getPageNumber() + " de ";
            float textWidth = bf.getWidthPoint(text, 9);
            float xPos = PageSize.A4.getWidth() - 80;
            float yPos = 30;

            cb.beginText();
            cb.setFontAndSize(bf, 9);
            cb.setTextMatrix(xPos, yPos);
            cb.showText(text);
            cb.endText();
            cb.addTemplate(totalPagesPlaceholder, xPos + textWidth, yPos);
        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            totalPagesPlaceholder.beginText();
            totalPagesPlaceholder.setFontAndSize(bf, 9);
            totalPagesPlaceholder.setTextMatrix(0, 0);
            totalPagesPlaceholder.showText(String.valueOf(writer.getPageNumber() - 1));
            totalPagesPlaceholder.endText();
        }
    }
}