package com.mps.notification.service;
import com.mps.notification.model.*; import com.mps.notification.repository.NotificationDeliveryRepository; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional; import java.time.Instant; import java.util.List;
@Component public class NotificationDeliveryWorker {
 private final NotificationDeliveryRepository repository;
 public NotificationDeliveryWorker(NotificationDeliveryRepository repository){this.repository=repository;}
 @Scheduled(fixedDelayString="${mps.notifications.worker-delay-ms:30000}") @Transactional
 public void process(){for(NotificationDelivery d:repository.findTop50ByStatusInAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(List.of(DeliveryStatus.QUEUED,DeliveryStatus.RETRYING),Instant.now())){try{deliver(d);}catch(RuntimeException e){d.failed(e.getMessage(),5);}}}
 private void deliver(NotificationDelivery d){if(d.getChannel()==DeliveryChannel.IN_APP){d.delivered();return;} throw new IllegalStateException(d.getChannel()+" provider is not configured.");}
}
