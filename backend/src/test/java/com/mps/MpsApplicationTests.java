package com.mps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.mps.announcement.service.AnnouncementService;
import com.mps.notification.service.NotificationService;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class MpsApplicationTests {

    @MockBean
    private AnnouncementService announcementService;

    @MockBean
    private NotificationService notificationService;

    @Test
    void contextLoads() {
    }
}
