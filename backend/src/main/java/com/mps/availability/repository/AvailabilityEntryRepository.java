package com.mps.availability.repository;
import com.mps.availability.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.time.Instant; import java.util.*;
public interface AvailabilityEntryRepository extends JpaRepository<AvailabilityEntry,UUID>{
 List<AvailabilityEntry> findAllByOrganizationIdAndUserIdAndStartAtLessThanAndEndAtGreaterThanOrderByStartAtAsc(UUID org,UUID user,Instant end,Instant start);
 boolean existsByOrganizationIdAndUserIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(UUID org,UUID user,AvailabilityStatus status,Instant end,Instant start);
}
