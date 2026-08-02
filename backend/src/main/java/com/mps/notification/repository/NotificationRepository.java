package com.mps.notification.repository;
import com.mps.notification.model.Notification; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,UUID>{
 List<Notification> findAllByOrganizationIdAndRecipientIdOrderByCreatedAtDesc(UUID org,UUID recipient);
 Optional<Notification> findByIdAndOrganizationIdAndRecipientId(UUID id,UUID org,UUID recipient);
 long countByOrganizationIdAndRecipientIdAndReadAtIsNull(UUID org,UUID recipient);
}
