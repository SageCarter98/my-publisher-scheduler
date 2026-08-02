package com.mps.assignment.dto;
import com.mps.assignment.model.AssignmentStatus; import jakarta.validation.constraints.*; import java.util.*;
public final class AssignmentDtos { private AssignmentDtos(){}
 public record CreateAssignmentRequest(@NotNull UUID scheduleId,@NotBlank @Size(max=120) String assignmentType,@NotBlank @Size(max=200) String title,UUID assigneeId,@Size(max=1000) String notes,@Size(max=500) String overrideReason){}
 public record ReassignRequest(@NotNull UUID assigneeId,@Size(max=500) String overrideReason){}
 public record ConflictView(String code,String message,boolean blocking){}
 public record AssignmentView(UUID id,UUID scheduleId,UUID assigneeId,String assigneeName,String assignmentType,String title,String notes,AssignmentStatus status,String overrideReason,List<ConflictView> conflicts,long version){}
 public record ReadinessView(boolean ready,long unfilledDraftAssignments,List<String> blockers){}
}
