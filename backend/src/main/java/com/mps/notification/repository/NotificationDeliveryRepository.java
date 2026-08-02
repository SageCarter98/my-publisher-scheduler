package com.mps.notification.repository;
import com.mps.notification.model.*; import org.springframework.data.jpa.repository.JpaRepository; import java.time.Instant; import java.util.*;
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery,UUID>{
 List<NotificationDelivery> findTop50ByStatusInAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(Collection<DeliveryStatus> statuses,Instant due);
}
