package com.mps.reporting.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mps.auth.security.UserPrincipal;
import com.mps.reporting.dto.ReportingDtos.DashboardSummary;
import com.mps.reporting.dto.ReportingDtos.OperationalReport;
import com.mps.reporting.dto.ReportingDtos.ReportRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ReportingService {

    private final JdbcTemplate jdbc;

    public ReportingService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public DashboardSummary dashboard(UserPrincipal principal) {
        UUID organizationId = principal.organizationId();
        Instant now = Instant.now();

        long activeUsers = count(
                """
                select count(*)
                from app_user
                where organization_id = ?
                  and status = 'ACTIVE'
                """,
                organizationId
        );

        long upcomingSchedules = count(
                """
                select count(*)
                from schedule_event
                where organization_id = ?
                  and start_at >= ?
                  and status in ('DRAFT', 'PUBLISHED')
                """,
                organizationId,
                now
        );

        long draftSchedules = count(
                """
                select count(*)
                from schedule_event
                where organization_id = ?
                  and status = 'DRAFT'
                """,
                organizationId
        );

        long publishedSchedules = count(
                """
                select count(*)
                from schedule_event
                where organization_id = ?
                  and status = 'PUBLISHED'
                """,
                organizationId
        );

        long openAssignments = count(
                """
                select count(*)
                from assignment
                where organization_id = ?
                  and status in ('DRAFT', 'SCHEDULED', 'CONFIRMED')
                """,
                organizationId
        );

        long completedAssignments = count(
                """
                select count(*)
                from assignment
                where organization_id = ?
                  and status = 'COMPLETED'
                """,
                organizationId
        );

        long unreadNotifications = count(
                """
                select count(*)
                from notification
                where organization_id = ?
                  and recipient_id = ?
                  and is_read = false
                """,
                organizationId,
                principal.userId()
        );

        long activeDocuments = count(
                """
                select count(*)
                from document_record
                where organization_id = ?
                  and status = 'ACTIVE'
                """,
                organizationId
        );

        long attended = count(
                """
                select count(*)
                from attendance_entry ae
                join attendance_register ar
                  on ar.id = ae.register_id
                where ar.organization_id = ?
                  and ae.attendance_status in ('PRESENT', 'LATE')
                """,
                organizationId
        );

        long attendanceTotal = count(
                """
                select count(*)
                from attendance_entry ae
                join attendance_register ar
                  on ar.id = ae.register_id
                where ar.organization_id = ?
                """,
                organizationId
        );

        double attendanceRate = attendanceTotal == 0
                ? 0.0
                : Math.round((attended * 10000.0) / attendanceTotal) / 100.0;

        return new DashboardSummary(
                activeUsers,
                upcomingSchedules,
                draftSchedules,
                publishedSchedules,
                openAssignments,
                completedAssignments,
                unreadNotifications,
                activeDocuments,
                attendanceRate,
                now
        );
    }

    public OperationalReport report(
            UserPrincipal principal,
            String type,
            Instant from,
            Instant to
    ) {
        UUID organizationId = principal.organizationId();

        Instant start = from == null
                ? Instant.now().minusSeconds(30L * 86400)
                : from;

        Instant end = to == null
                ? Instant.now().plusSeconds(86400)
                : to;

        String normalizedType = type == null
                ? "overview"
                : type.toLowerCase(Locale.ROOT);

        List<ReportRow> rows = switch (normalizedType) {
            case "schedules" -> grouped(
                    """
                    select status, count(*)
                    from schedule_event
                    where organization_id = ?
                      and start_at >= ?
                      and start_at < ?
                    group by status
                    order by status
                    """,
                    organizationId,
                    start,
                    end,
                    "Schedules"
            );

            case "assignments" -> grouped(
                    """
                    select a.status, count(*)
                    from assignment a
                    join schedule_event s
                      on s.id = a.schedule_id
                    where a.organization_id = ?
                      and s.start_at >= ?
                      and s.start_at < ?
                    group by a.status
                    order by a.status
                    """,
                    organizationId,
                    start,
                    end,
                    "Assignments"
            );

            case "attendance" -> grouped(
                    """
                    select ae.attendance_status, count(*)
                    from attendance_entry ae
                    join attendance_register ar
                      on ar.id = ae.register_id
                    join schedule_event s
                      on s.id = ar.schedule_id
                    where ar.organization_id = ?
                      and s.start_at >= ?
                      and s.start_at < ?
                    group by ae.attendance_status
                    order by ae.attendance_status
                    """,
                    organizationId,
                    start,
                    end,
                    "Attendance"
            );

            case "users" -> grouped(
                    """
                    select status, count(*)
                    from app_user
                    where organization_id = ?
                    group by status
                    order by status
                    """,
                    organizationId,
                    "Users"
            );

            default -> List.of(
                    new ReportRow(
                            "Users",
                            "Active users",
                            count(
                                    """
                                    select count(*)
                                    from app_user
                                    where organization_id = ?
                                      and status = 'ACTIVE'
                                    """,
                                    organizationId
                            ),
                            "ACTIVE"
                    ),
                    new ReportRow(
                            "Schedules",
                            "Schedules in period",
                            count(
                                    """
                                    select count(*)
                                    from schedule_event
                                    where organization_id = ?
                                      and start_at >= ?
                                      and start_at < ?
                                    """,
                                    organizationId,
                                    start,
                                    end
                            ),
                            "ALL"
                    ),
                    new ReportRow(
                            "Assignments",
                            "Assignments in period",
                            count(
                                    """
                                    select count(*)
                                    from assignment a
                                    join schedule_event s
                                      on s.id = a.schedule_id
                                    where a.organization_id = ?
                                      and s.start_at >= ?
                                      and s.start_at < ?
                                    """,
                                    organizationId,
                                    start,
                                    end
                            ),
                            "ALL"
                    ),
                    new ReportRow(
                            "Documents",
                            "Active documents",
                            count(
                                    """
                                    select count(*)
                                    from document_record
                                    where organization_id = ?
                                      and status = 'ACTIVE'
                                    """,
                                    organizationId
                            ),
                            "ACTIVE"
                    )
            );
        };

        return new OperationalReport(
                normalizedType,
                start,
                end,
                Instant.now(),
                rows
        );
    }

    public byte[] csv(OperationalReport report) {
        StringBuilder output =
                new StringBuilder("Category,Label,Status,Count\n");

        report.rows().forEach(row ->
                output.append(quote(row.category()))
                        .append(',')
                        .append(quote(row.label()))
                        .append(',')
                        .append(quote(row.status()))
                        .append(',')
                        .append(row.count())
                        .append('\n')
        );

        return output.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] xlsx(OperationalReport report) {
        try (
                XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.createSheet("MPS Report");

            Row title = sheet.createRow(0);
            title.createCell(0).setCellValue(
                    "My Publisher Scheduler — "
                            + report.reportType()
                            + " report"
            );

            Row metadata = sheet.createRow(1);
            metadata.createCell(0).setCellValue("Generated");
            metadata.createCell(1).setCellValue(
                    report.generatedAt().toString()
            );

            Row header = sheet.createRow(3);
            String[] headers = {
                    "Category",
                    "Label",
                    "Status",
                    "Count"
            };

            for (int index = 0; index < headers.length; index++) {
                header.createCell(index).setCellValue(headers[index]);
            }

            int rowIndex = 4;

            for (ReportRow reportRow : report.rows()) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(reportRow.category());
                row.createCell(1).setCellValue(reportRow.label());
                row.createCell(2).setCellValue(reportRow.status());
                row.createCell(3).setCellValue(reportRow.count());
            }

            for (int index = 0; index < headers.length; index++) {
                sheet.autoSizeColumn(index);
            }

            workbook.write(output);
            return output.toByteArray();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to generate spreadsheet report.",
                    exception
            );
        }
    }

    public byte[] pdf(OperationalReport report) {
        try (ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, output);
            document.open();

            document.add(
                    new Paragraph(
                            "My Publisher Scheduler",
                            FontFactory.getFont(
                                    FontFactory.HELVETICA_BOLD,
                                    18
                            )
                    )
            );

            document.add(
                    new Paragraph(
                            report.reportType() + " report"
                    )
            );

            document.add(
                    new Paragraph(
                            "Generated: " + report.generatedAt()
                    )
            );

            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            for (String header : List.of(
                    "Category",
                    "Label",
                    "Status",
                    "Count"
            )) {
                table.addCell(header);
            }

            for (ReportRow row : report.rows()) {
                table.addCell(row.category());
                table.addCell(row.label());
                table.addCell(row.status());
                table.addCell(Long.toString(row.count()));
            }

            document.add(table);
            document.close();

            return output.toByteArray();

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to generate PDF report.",
                    exception
            );
        }
    }

    private List<ReportRow> grouped(
            String sql,
            Object firstArgument,
            Object secondArgument,
            Object thirdArgument,
            String category
    ) {
        return jdbc.query(
                sql,
                (resultSet, rowNumber) -> new ReportRow(
                        category,
                        resultSet.getString(1),
                        resultSet.getLong(2),
                        resultSet.getString(1)
                ),
                firstArgument,
                secondArgument,
                thirdArgument
        );
    }

    private List<ReportRow> grouped(
            String sql,
            Object firstArgument,
            String category
    ) {
        return jdbc.query(
                sql,
                (resultSet, rowNumber) -> new ReportRow(
                        category,
                        resultSet.getString(1),
                        resultSet.getLong(2),
                        resultSet.getString(1)
                ),
                firstArgument
        );
    }

    private long count(String sql, Object... arguments) {
        Long value = jdbc.queryForObject(
                sql,
                Long.class,
                arguments
        );

        return value == null ? 0 : value;
    }

    private String quote(String value) {
        String normalized = value == null ? "" : value;

        return "\""
                + normalized.replace("\"", "\"\"")
                + "\"";
    }
}
