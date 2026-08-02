package com.mps.reporting.dto;

import java.time.Instant;
import java.util.List;

public final class ReportingDtos {
    private ReportingDtos() {}
    public record DashboardSummary(long activeUsers,long upcomingSchedules,long draftSchedules,long publishedSchedules,long openAssignments,long completedAssignments,long unreadNotifications,long activeDocuments,double attendanceRate,Instant generatedAt) {}
    public record ReportRow(String category,String label,long count,String status) {}
    public record OperationalReport(String reportType,Instant from,Instant to,Instant generatedAt,List<ReportRow> rows) {}
}
