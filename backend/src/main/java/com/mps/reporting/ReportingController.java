package com.mps.reporting;

import com.mps.auth.security.UserPrincipal;
import com.mps.common.ApiResponse;
import com.mps.reporting.dto.ReportingDtos.*;
import com.mps.reporting.service.ReportingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController @RequestMapping("/api/v1/reports")
public class ReportingController {
    private final ReportingService service;
    public ReportingController(ReportingService service){this.service=service;}

    @GetMapping("/dashboard") @PreAuthorize("isAuthenticated()")
    public ApiResponse<DashboardSummary> dashboard(@AuthenticationPrincipal UserPrincipal p){return ApiResponse.ok("Dashboard loaded.",service.dashboard(p));}

    @GetMapping @PreAuthorize("hasAuthority('reports.read')")
    public ApiResponse<OperationalReport> report(@AuthenticationPrincipal UserPrincipal p,@RequestParam(defaultValue="overview") String type,
      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to){return ApiResponse.ok("Report generated.",service.report(p,type,from,to));}

    @GetMapping("/export") @PreAuthorize("hasAuthority('reports.export')")
    public ResponseEntity<byte[]> export(@AuthenticationPrincipal UserPrincipal p,@RequestParam(defaultValue="overview") String type,@RequestParam(defaultValue="csv") String format,
      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to){
        OperationalReport r=service.report(p,type,from,to); String f=format.toLowerCase(); byte[] bytes; MediaType media; String ext;
        switch(f){case "xlsx"->{bytes=service.xlsx(r);media=MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");ext="xlsx";}case "pdf"->{bytes=service.pdf(r);media=MediaType.APPLICATION_PDF;ext="pdf";}default->{bytes=service.csv(r);media=MediaType.parseMediaType("text/csv");ext="csv";}}
        return ResponseEntity.ok().contentType(media).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"mps-"+type+"-report."+ext+"\"").body(bytes);
    }
}
