package com.mps.assignment.repository;
import com.mps.assignment.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.time.Instant; import java.util.*;
public interface AssignmentRepository extends JpaRepository<Assignment,UUID>{
 List<Assignment> findAllByOrganizationIdAndScheduleIdOrderByAssignmentTypeAsc(UUID org,UUID schedule);
 Optional<Assignment> findByIdAndOrganizationId(UUID id,UUID org);
 boolean existsByOrganizationIdAndAssigneeIdAndStatusNotInAndScheduleStartAtLessThanAndScheduleEndAtGreaterThan(UUID org,UUID user,Collection<AssignmentStatus> excluded,Instant end,Instant start);
 long countByOrganizationIdAndScheduleIdAndStatus(UUID org,UUID schedule,AssignmentStatus status);
}
