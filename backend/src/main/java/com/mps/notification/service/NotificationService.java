package com.mps.notification.service;
import com.mps.auth.model.AppUser; import com.mps.auth.repository.AppUserRepository; import com.mps.auth.security.UserPrincipal; import com.mps.notification.dto.NotificationDtos.*; import com.mps.notification.model.*; import com.mps.notification.repository.*; import com.mps.organization.model.Organization; import org.springframework.http.HttpStatus; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.server.ResponseStatusException; import java.util.*;
@Service public class NotificationService {
 private final NotificationRepository notifications; private final NotificationDeliveryRepository deliveries; private final AppUserRepository users;
 public NotificationService(NotificationRepository n,NotificationDeliveryRepository d,AppUserRepository u){notifications=n;deliveries=d;users=u;}
 @Transactional public void notifyUsers(Organization org,Collection<AppUser> recipients,String type,String title,String message,String entityType,UUID entityId,boolean external){
  for(AppUser user:recipients){Notification n=notifications.save(new Notification(org,user,type,title,message,entityType,entityId)); deliveries.save(new NotificationDelivery(n,DeliveryChannel.IN_APP)); if(external){deliveries.save(new NotificationDelivery(n,DeliveryChannel.EMAIL)); deliveries.save(new NotificationDelivery(n,DeliveryChannel.PUSH));}}
 }
 @Transactional public void notifyOrganization(Organization org,String type,String title,String message,String entityType,UUID entityId,boolean external){notifyUsers(org,users.findAllByOrganizationIdOrderByLastNameAscFirstNameAsc(org.getId()),type,title,message,entityType,entityId,external);}
 @Transactional(readOnly=true) public List<NotificationView> list(UserPrincipal p){return notifications.findAllByOrganizationIdAndRecipientIdOrderByCreatedAtDesc(p.organizationId(),p.userId()).stream().map(this::view).toList();}
 @Transactional(readOnly=true) public NotificationSummary summary(UserPrincipal p){return new NotificationSummary(notifications.countByOrganizationIdAndRecipientIdAndReadAtIsNull(p.organizationId(),p.userId()));}
 @Transactional public NotificationView markRead(UserPrincipal p,UUID id){Notification n=notifications.findByIdAndOrganizationIdAndRecipientId(id,p.organizationId(),p.userId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Notification not found."));n.markRead();return view(n);}
 private NotificationView view(Notification n){return new NotificationView(n.getId(),n.getType(),n.getTitle(),n.getMessage(),n.getRelatedEntityType(),n.getRelatedEntityId(),n.getReadAt()!=null,n.getCreatedAt());}
}
