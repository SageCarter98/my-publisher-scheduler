package com.mps.reporting.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.mps.auth.security.UserPrincipal;
import com.mps.reporting.dto.ReportingDtos.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class ReportingService {
    private final JdbcTemplate jdbc;
    public ReportingService(JdbcTemplate jdbc){this.jdbc=jdbc;}

    public DashboardSummary dashboard(UserPrincipal p){
        UUID org=p.organizationId(); Instant now=Instant.now();
        long activeUsers=count("select count(*) from app_user where organization_id=? and status='ACTIVE'",org);
        long upcoming=count("select count(*) from schedule_event where organization_id=? and start_at>=? and status in ('DRAFT','PUBLISHED')",org,now);
        long draft=count("select count(*) from schedule_event where organization_id=? and status='DRAFT'",org);
        long published=count("select count(*) from schedule_event where organization_id=? and status='PUBLISHED'",org);
        long openAssignments=count("select count(*) from assignment where organization_id=? and status in ('DRAFT','SCHEDULED','CONFIRMED')",org);
        long completedAssignments=count("select count(*) from assignment where organization_id=? and status='COMPLETED'",org);
        long unread=count("select count(*) from notification where organization_id=? and recipient_id=? and is_read=false",org,p.userId());
        long docs=count("select count(*) from document_record where organization_id=? and status='ACTIVE'",org);
        long attended=count("select count(*) from attendance_entry ae join attendance_register ar on ar.id=ae.register_id where ar.organization_id=? and ae.attendance_status in ('PRESENT','LATE')",org);
        long attendanceTotal=count("select count(*) from attendance_entry ae join attendance_register ar on ar.id=ae.register_id where ar.organization_id=?",org);
        double rate=attendanceTotal==0?0d:Math.round((attended*10000d)/attendanceTotal)/100d;
        return new DashboardSummary(activeUsers,upcoming,draft,published,openAssignments,completedAssignments,unread,docs,rate,now);
    }

    public OperationalReport report(UserPrincipal p,String type,Instant from,Instant to){
        UUID org=p.organizationId(); Instant start=from==null?Instant.now().minusSeconds(30L*86400):from; Instant end=to==null?Instant.now().plusSeconds(86400):to;
        String normalized=type==null?"overview":type.toLowerCase(Locale.ROOT);
        List<ReportRow> rows=switch(normalized){
            case "schedules" -> grouped("select status,count(*) from schedule_event where organization_id=? and start_at>=? and start_at<? group by status order by status",org,start,end,"Schedules");
            case "assignments" -> grouped("select status,count(*) from assignment a join schedule_event s on s.id=a.schedule_id where a.organization_id=? and s.start_at>=? and s.start_at<? group by status order by status",org,start,end,"Assignments");
            case "attendance" -> grouped("select ae.attendance_status,count(*) from attendance_entry ae join attendance_register ar on ar.id=ae.register_id join schedule_event s on s.id=ar.schedule_id where ar.organization_id=? and s.start_at>=? and s.start_at<? group by ae.attendance_status order by ae.attendance_status",org,start,end,"Attendance");
            case "users" -> grouped("select status,count(*) from app_user where organization_id=? group by status order by status",org,"Users");
            default -> List.of(
                new ReportRow("Users","Active users",count("select count(*) from app_user where organization_id=? and status='ACTIVE'",org),"ACTIVE"),
                new ReportRow("Schedules","Schedules in period",count("select count(*) from schedule_event where organization_id=? and start_at>=? and start_at<?",org,start,end),"ALL"),
                new ReportRow("Assignments","Assignments in period",count("select count(*) from assignment a join schedule_event s on s.id=a.schedule_id where a.organization_id=? and s.start_at>=? and s.start_at<?",org,start,end),"ALL"),
                new ReportRow("Documents","Active documents",count("select count(*) from document_record where organization_id=? and status='ACTIVE'",org),"ACTIVE")
            );
        };
        return new OperationalReport(normalized,start,end,Instant.now(),rows);
    }

    public byte[] csv(OperationalReport report){
        StringBuilder b=new StringBuilder("Category,Label,Status,Count\n");
        report.rows().forEach(r->b.append(q(r.category())).append(',').append(q(r.label())).append(',').append(q(r.status())).append(',').append(r.count()).append('\n'));
        return b.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] xlsx(OperationalReport report){
        try(XSSFWorkbook wb=new XSSFWorkbook(); ByteArrayOutputStream out=new ByteArrayOutputStream()){
            Sheet sheet=wb.createSheet("MPS Report");
            Row title=sheet.createRow(0); title.createCell(0).setCellValue("My Publisher Scheduler — "+report.reportType()+" report");
            Row meta=sheet.createRow(1); meta.createCell(0).setCellValue("Generated"); meta.createCell(1).setCellValue(report.generatedAt().toString());
            Row h=sheet.createRow(3); String[] headers={"Category","Label","Status","Count"}; for(int i=0;i<headers.length;i++) h.createCell(i).setCellValue(headers[i]);
            int index=4; for(ReportRow rr:report.rows()){Row r=sheet.createRow(index++);r.createCell(0).setCellValue(rr.category());r.createCell(1).setCellValue(rr.label());r.createCell(2).setCellValue(rr.status());r.createCell(3).setCellValue(rr.count());}
            for(int i=0;i<4;i++)sheet.autoSizeColumn(i); wb.write(out); return out.toByteArray();
        }catch(Exception e){throw new IllegalStateException("Unable to generate spreadsheet report.",e);}
    }

    public byte[] pdf(OperationalReport report){
        try(ByteArrayOutputStream out=new ByteArrayOutputStream()){
            Document doc=new Document(PageSize.A4); PdfWriter.getInstance(doc,out); doc.open();
            doc.add(new Paragraph("My Publisher Scheduler",FontFactory.getFont(FontFactory.HELVETICA_BOLD,18)));
            doc.add(new Paragraph(report.reportType()+" report")); doc.add(new Paragraph("Generated: "+report.generatedAt())); doc.add(Chunk.NEWLINE);
            PdfPTable table=new PdfPTable(4); table.setWidthPercentage(100); for(String h:List.of("Category","Label","Status","Count"))table.addCell(h);
            for(ReportRow r:report.rows()){table.addCell(r.category());table.addCell(r.label());table.addCell(r.status());table.addCell(Long.toString(r.count()));}
            doc.add(table); doc.close(); return out.toByteArray();
        }catch(Exception e){throw new IllegalStateException("Unable to generate PDF report.",e);}
    }

    private List<ReportRow> grouped(String sql,Object a,Object b,Object c,String category){return jdbc.query(sql,(rs,n)->new ReportRow(category,rs.getString(1),rs.getLong(2),rs.getString(1)),a,b,c);}
    private List<ReportRow> grouped(String sql,Object a,String category){return jdbc.query(sql,(rs,n)->new ReportRow(category,rs.getString(1),rs.getLong(2),rs.getString(1)),a);}
    private long count(String sql,Object...args){Long value=jdbc.queryForObject(sql,Long.class,args);return value==null?0:value;}
    private String q(String value){String v=value==null?"":value;return '"'+v.replace("\"","\"\"")+'"';}
}
