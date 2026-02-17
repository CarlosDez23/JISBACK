package com.jis.training.application.service;

import com.jis.training.domain.model.*;
import com.jis.training.infrastructure.adapter.in.rest.dto.BulkLoadResult;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BulkLoadService {

    private final UsuarioService usuarioService;
    private final ComunidadService comunidadService;
    private final MateriaService materiaService;
    private final TopicService topicService;
    private final QuestionService questionService;
    private final PasswordEncoder passwordEncoder;

    // ======================== PLANTILLAS ========================

    public byte[] generateUsuariosTemplate() throws IOException {
        List<Comunidad> comunidades = comunidadService.getAll();
        String[] nombresComunidades = comunidades.stream()
                .map(Comunidad::getNombre)
                .toArray(String[]::new);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Usuarios");

            // Header style
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Nombre", "Apellidos", "Correo Electrónico", "Es Administrador", "Comunidad"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 6000);
            }

            // Dropdown "Si/No" for Es Administrador (column 3), rows 1-100
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            DataValidationConstraint siNoConstraint = dvHelper.createExplicitListConstraint(new String[]{"Si", "No"});
            CellRangeAddressList adminRange = new CellRangeAddressList(1, 100, 3, 3);
            DataValidation adminValidation = dvHelper.createValidation(siNoConstraint, adminRange);
            adminValidation.setShowErrorBox(true);
            adminValidation.createErrorBox("Valor inválido", "Seleccione Si o No");
            sheet.addValidationData(adminValidation);

            // Dropdown for Comunidad (column 4), rows 1-100
            if (nombresComunidades.length > 0) {
                DataValidationConstraint comunidadConstraint = dvHelper.createExplicitListConstraint(nombresComunidades);
                CellRangeAddressList comunidadRange = new CellRangeAddressList(1, 100, 4, 4);
                DataValidation comunidadValidation = dvHelper.createValidation(comunidadConstraint, comunidadRange);
                comunidadValidation.setShowErrorBox(true);
                comunidadValidation.createErrorBox("Valor inválido", "Seleccione una comunidad de la lista");
                sheet.addValidationData(comunidadValidation);
            }

            return writeWorkbook(workbook);
        }
    }

    public byte[] generateTemasTemplate(Integer comunidadId) throws IOException {
        List<Materia> materias = materiaService.findByComunidadId(comunidadId);
        String[] nombresMaterias = materias.stream()
                .map(Materia::getNombreMateria)
                .toArray(String[]::new);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // === Sheet 1: Tema ===
            XSSFSheet temaSheet = workbook.createSheet("Tema");
            CellStyle headerStyle = createHeaderStyle(workbook);

            Row temaHeader = temaSheet.createRow(0);
            String[] temaHeaders = {"Nombre del Tema", "Materia"};
            for (int i = 0; i < temaHeaders.length; i++) {
                Cell cell = temaHeader.createCell(i);
                cell.setCellValue(temaHeaders[i]);
                cell.setCellStyle(headerStyle);
                temaSheet.setColumnWidth(i, 8000);
            }

            // Dropdown for Materia (column 1), row 1
            if (nombresMaterias.length > 0) {
                DataValidationHelper dvHelper = temaSheet.getDataValidationHelper();
                DataValidationConstraint materiaConstraint = dvHelper.createExplicitListConstraint(nombresMaterias);
                CellRangeAddressList materiaRange = new CellRangeAddressList(1, 1, 1, 1);
                DataValidation materiaValidation = dvHelper.createValidation(materiaConstraint, materiaRange);
                materiaValidation.setShowErrorBox(true);
                materiaValidation.createErrorBox("Valor inválido", "Seleccione una materia de la lista");
                temaSheet.addValidationData(materiaValidation);
            }

            // === Sheet 2: Preguntas ===
            XSSFSheet preguntasSheet = workbook.createSheet("Preguntas");

            Row preguntasHeader = preguntasSheet.createRow(0);
            String[] pregHeaders = {
                    "Enunciado",
                    "Respuesta 1", "Correcta 1", "Explicación 1",
                    "Respuesta 2", "Correcta 2", "Explicación 2",
                    "Respuesta 3", "Correcta 3", "Explicación 3",
                    "Respuesta 4", "Correcta 4", "Explicación 4"
            };
            for (int i = 0; i < pregHeaders.length; i++) {
                Cell cell = preguntasHeader.createCell(i);
                cell.setCellValue(pregHeaders[i]);
                cell.setCellStyle(headerStyle);
                preguntasSheet.setColumnWidth(i, i == 0 ? 12000 : 5000);
            }

            // Dropdowns "Si/No" for Correcta columns (2, 5, 8, 11), rows 1-200
            DataValidationHelper dvHelper2 = preguntasSheet.getDataValidationHelper();
            DataValidationConstraint siNoConstraint = dvHelper2.createExplicitListConstraint(new String[]{"Si", "No"});
            int[] correctaColumns = {2, 5, 8, 11};
            for (int col : correctaColumns) {
                CellRangeAddressList range = new CellRangeAddressList(1, 200, col, col);
                DataValidation validation = dvHelper2.createValidation(siNoConstraint, range);
                validation.setShowErrorBox(true);
                validation.createErrorBox("Valor inválido", "Seleccione Si o No");
                preguntasSheet.addValidationData(validation);
            }

            return writeWorkbook(workbook);
        }
    }

    // ======================== PROCESAMIENTO ========================

    @Transactional
    public BulkLoadResult processUsuariosExcel(MultipartFile file) throws IOException {
        List<String> errores = new ArrayList<>();
        int exitosos = 0;
        int totalProcesados = 0;

        List<Comunidad> comunidades = comunidadService.getAll();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                totalProcesados++;
                int fila = i + 1;

                try {
                    String nombre = getStringCell(row, 0);
                    String apellidos = getStringCell(row, 1);
                    String correo = getStringCell(row, 2);
                    String esAdmin = getStringCell(row, 3);
                    String comunidadNombre = getStringCell(row, 4);

                    // Validaciones
                    if (nombre == null || nombre.isBlank()) {
                        errores.add("Fila " + fila + ": El nombre es obligatorio");
                        continue;
                    }
                    if (apellidos == null || apellidos.isBlank()) {
                        errores.add("Fila " + fila + ": Los apellidos son obligatorios");
                        continue;
                    }
                    if (correo == null || correo.isBlank() || !correo.contains("@")) {
                        errores.add("Fila " + fila + ": El correo electrónico es inválido");
                        continue;
                    }
                    if (usuarioService.existsByCorreoElectronico(correo)) {
                        errores.add("Fila " + fila + ": El correo '" + correo + "' ya está registrado");
                        continue;
                    }
                    if (comunidadNombre == null || comunidadNombre.isBlank()) {
                        errores.add("Fila " + fila + ": La comunidad es obligatoria");
                        continue;
                    }

                    Optional<Comunidad> comunidad = comunidades.stream()
                            .filter(c -> c.getNombre().equalsIgnoreCase(comunidadNombre.trim()))
                            .findFirst();

                    if (comunidad.isEmpty()) {
                        errores.add("Fila " + fila + ": Comunidad '" + comunidadNombre + "' no encontrada");
                        continue;
                    }

                    Usuario usuario = new Usuario();
                    usuario.setNombre(nombre.trim());
                    usuario.setApellidos(apellidos.trim());
                    usuario.setCorreoElectronico(correo.trim().toLowerCase());
                    usuario.setUserPassword(passwordEncoder.encode("password"));
                    usuario.setAdmin("Si".equalsIgnoreCase(esAdmin != null ? esAdmin.trim() : "No"));
                    usuario.setPasswordChangeRequired(true);
                    usuario.setComunidad(comunidad.get());

                    usuarioService.create(usuario);
                    exitosos++;
                } catch (Exception e) {
                    errores.add("Fila " + fila + ": Error inesperado - " + e.getMessage());
                }
            }
        }

        return new BulkLoadResult(totalProcesados, exitosos, totalProcesados - exitosos, errores);
    }

    @Transactional
    public BulkLoadResult processTemasExcel(MultipartFile file, Integer comunidadId) throws IOException {
        List<String> errores = new ArrayList<>();
        int exitosos = 0;

        List<Materia> materias = materiaService.findByComunidadId(comunidadId);

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            // === Sheet 1: Tema ===
            Sheet temaSheet = workbook.getSheetAt(0);
            Row temaRow = temaSheet.getRow(1);

            if (temaRow == null || isRowEmpty(temaRow)) {
                errores.add("Hoja 'Tema': No se encontraron datos del tema en la fila 2");
                return new BulkLoadResult(0, 0, 0, errores);
            }

            String topicName = getStringCell(temaRow, 0);
            String materiaNombre = getStringCell(temaRow, 1);

            if (topicName == null || topicName.isBlank()) {
                errores.add("Hoja 'Tema': El nombre del tema es obligatorio");
                return new BulkLoadResult(0, 0, 0, errores);
            }
            if (materiaNombre == null || materiaNombre.isBlank()) {
                errores.add("Hoja 'Tema': La materia es obligatoria");
                return new BulkLoadResult(0, 0, 0, errores);
            }

            Optional<Materia> materia = materias.stream()
                    .filter(m -> m.getNombreMateria().equalsIgnoreCase(materiaNombre.trim()))
                    .findFirst();

            if (materia.isEmpty()) {
                errores.add("Hoja 'Tema': Materia '" + materiaNombre + "' no encontrada para esta comunidad");
                return new BulkLoadResult(0, 0, 0, errores);
            }

            // Create topic
            Topic topic = new Topic();
            topic.setTopicName(topicName.trim());
            topic.setMateria(materia.get());
            Topic savedTopic = topicService.create(topic);

            // === Sheet 2: Preguntas ===
            Sheet preguntasSheet = workbook.getSheetAt(1);
            int totalPreguntas = 0;

            for (int i = 1; i <= preguntasSheet.getLastRowNum(); i++) {
                Row row = preguntasSheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                totalPreguntas++;
                int fila = i + 1;

                try {
                    String enunciado = getStringCell(row, 0);
                    if (enunciado == null || enunciado.isBlank()) {
                        errores.add("Hoja 'Preguntas', Fila " + fila + ": El enunciado es obligatorio");
                        continue;
                    }

                    // Parse 4 answers
                    List<Answer> answers = new ArrayList<>();
                    boolean hasCorrectAnswer = false;

                    for (int a = 0; a < 4; a++) {
                        int baseCol = 1 + (a * 3); // 1, 4, 7, 10
                        String answerText = getStringCell(row, baseCol);
                        String correcta = getStringCell(row, baseCol + 1);
                        String explicacion = getStringCell(row, baseCol + 2);

                        if (answerText == null || answerText.isBlank()) {
                            errores.add("Hoja 'Preguntas', Fila " + fila + ": La respuesta " + (a + 1) + " es obligatoria");
                            break;
                        }

                        boolean isCorrect = "Si".equalsIgnoreCase(correcta != null ? correcta.trim() : "No");
                        if (isCorrect) hasCorrectAnswer = true;

                        Answer answer = new Answer();
                        answer.setAnswerText(answerText.trim());
                        answer.setCorrect(isCorrect);
                        answer.setExplanation(explicacion != null ? explicacion.trim() : "");
                        answers.add(answer);
                    }

                    if (answers.size() < 4) continue;

                    if (!hasCorrectAnswer) {
                        errores.add("Hoja 'Preguntas', Fila " + fila + ": Debe haber al menos una respuesta correcta");
                        continue;
                    }

                    Question question = new Question();
                    question.setQuestionText(enunciado.trim());
                    question.setTopic(savedTopic);

                    questionService.createWithAnswers(question, answers);
                    exitosos++;
                } catch (Exception e) {
                    errores.add("Hoja 'Preguntas', Fila " + fila + ": Error inesperado - " + e.getMessage());
                }
            }

            return new BulkLoadResult(totalPreguntas, exitosos, totalPreguntas - exitosos, errores);
        }
    }

    // ======================== UTILIDADES ========================

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private byte[] writeWorkbook(XSSFWorkbook workbook) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    private String getStringCell(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> cell.getBooleanCellValue() ? "Si" : "No";
            default -> null;
        };
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getStringCell(row, c);
                if (value != null && !value.isBlank()) return false;
            }
        }
        return true;
    }
}
