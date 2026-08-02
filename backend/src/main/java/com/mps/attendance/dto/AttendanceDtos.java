package com.mps.attendance.dto;
import com.mps.attendance.model.*; import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*;
public final class AttendanceDtos { private AttendanceDtos(){}
 public record EntryRequest(@NotNull UUID memberId,@NotNull AttendanceStatus status,@Size(max=500) String remarks){}
 public record SaveRegisterRequest(@NotEmpty List<@Valid EntryRequest> entries){}
 public record CorrectionRequest(@NotNull AttendanceStatus status,@Size(max=500) String remarks,@NotBlank @Size(max=500) String reason){}
 public record EntryView(UUID id,UUID memberId,String memberName,AttendanceStatus status,String remarks,Instant correctedAt,String correctionReason){}
 public record RegisterView(UUID id,UUID scheduleId,String scheduleTitle,AttendanceRegisterStatus status,Instant finalizedAt,List<EntryView> entries){}
}
