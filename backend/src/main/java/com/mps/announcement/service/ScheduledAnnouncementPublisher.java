package com.mps.announcement.service;
import com.mps.announcement.model.*; import com.mps.announcement.repository.AnnouncementRepository; import com.mps.auth.model.AppUser; import com.mps.auth.repository.AppUserRepository; import com.mps.notification.service.NotificationService; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional; import java.time.Instant; import java.util.*;
@Component public class ScheduledAnnouncementPublisher{
 private final AnnouncementRepository announcements; private final AppUserRepository users; private final NotificationService notifications;
 public ScheduledAnnouncementPublisher(AnnouncementRepository a,AppUserRepository u,NotificationService n){announcements=a;users=u;notifications=n;}
 @Scheduled(fixedDelayString="${mps.announcements.worker-delay-ms:60000}") @Transactional public void process(){
  for(Announcement a:announcements.findAll()){
   if(a.getStatus()==AnnouncementStatus.PUBLISHED&&a.getExpiresAt()!=null&&!a.getExpiresAt().isAfter(Instant.now())){a.expire();continue;}
   if(a.getStatus()==AnnouncementStatus.SCHEDULED&&a.getPublishAt()!=null&&!a.getPublishAt().isAfter(Instant.now())){a.publish();notifications.notifyUsers(a.getOrganization(),recipients(a),"ANNOUNCEMENT_PUBLISHED",a.getTitle(),a.getContent(),"Announcement",a.getId(),true);}
  }
 }
 private List<AppUser> recipients(Announcement a){return users.findAllByOrganizationIdOrderByLastNameAscFirstNameAsc(a.getOrganization().getId()).stream().filter(u->switch(a.getAudienceType()){case ORGANIZATION->true;case USER->u.getId().equals(a.getAudienceReferenceId());case DEPARTMENT->u.getDepartment()!=null&&u.getDepartment().getId().equals(a.getAudienceReferenceId());case GROUP->u.getGroup()!=null&&u.getGroup().getId().equals(a.getAudienceReferenceId());case ROLE->u.getRoles().stream().anyMatch(r->r.getId().equals(a.getAudienceReferenceId()));}).toList();}
}
