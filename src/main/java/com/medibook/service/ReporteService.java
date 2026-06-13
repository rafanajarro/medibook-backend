package com.medibook.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.medibook.entity.Cita;
import com.medibook.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final CitaRepository citaRepository;

    public byte[] generarPdfCitas() throws IOException {
        List<Cita> citas = citaRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Reporte de Citas Médicas")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18)
                .setBold());

        document.add(new Paragraph("Total de citas: " + citas.size())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));

        document.add(new Paragraph("\n"));

        Table table = new Table(new float[]{2, 2, 2, 2, 2, 2});
        table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

        String[] headers = {"Paciente", "Médico", "Fecha", "Hora", "Estado", "Motivo"};
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header).setBold());
            cell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
            table.addHeaderCell(cell);
        }

        for (Cita cita : citas) {
            table.addCell(cita.getPaciente().getUsuario().getNombre() + " " + cita.getPaciente().getUsuario().getApellido());
            table.addCell(cita.getMedico().getUsuario().getNombre() + " " + cita.getMedico().getUsuario().getApellido());
            table.addCell(cita.getHorario().getFecha().toString());
            table.addCell(cita.getHorario().getHoraInicio().toString() + " - " + cita.getHorario().getHoraFin().toString());
            table.addCell(cita.getEstado());
            table.addCell(cita.getMotivo() != null ? cita.getMotivo() : "-");
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    public byte[] generarExcelCitas() throws IOException {
        List<Cita> citas = citaRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Citas");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Paciente", "Médico", "Fecha", "Hora Inicio", "Hora Fin", "Estado", "Motivo"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (Cita cita : citas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cita.getId());
            row.createCell(1).setCellValue(cita.getPaciente().getUsuario().getNombre() + " " + cita.getPaciente().getUsuario().getApellido());
            row.createCell(2).setCellValue(cita.getMedico().getUsuario().getNombre() + " " + cita.getMedico().getUsuario().getApellido());
            row.createCell(3).setCellValue(cita.getHorario().getFecha().toString());
            row.createCell(4).setCellValue(cita.getHorario().getHoraInicio().toString());
            row.createCell(5).setCellValue(cita.getHorario().getHoraFin().toString());
            row.createCell(6).setCellValue(cita.getEstado());
            row.createCell(7).setCellValue(cita.getMotivo() != null ? cita.getMotivo() : "-");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }
}