package com.mps.announcement.repository; import com.mps.announcement.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AnnouncementRepository extends JpaRepository<Announcement,UUID>{List<Announcement> findAllByOrganizationIdOrderByCreatedAtDesc(UUID org);Optional<Announcement> findByIdAndOrganizationId(UUID id,UUID org);}
