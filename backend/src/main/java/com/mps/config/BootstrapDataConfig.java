package com.mps.config;

import com.mps.auth.model.AppUser;
import com.mps.auth.repository.AppUserRepository;
import com.mps.auth.repository.RoleRepository;
import com.mps.organization.model.Organization;
import com.mps.organization.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BootstrapDataConfig implements ApplicationRunner {
    private final OrganizationRepository organizations; private final AppUserRepository users; private final RoleRepository roles; private final PasswordEncoder encoder;
    @Value("${mps.bootstrap.enabled:false}") boolean enabled;
    @Value("${mps.bootstrap.organization-name:My Publisher Scheduler Demo}") String organizationName;
    @Value("${mps.bootstrap.admin-email:admin@mps.local}") String adminEmail;
    @Value("${mps.bootstrap.admin-password:ChangeThisPassword123!}") String adminPassword;
    public BootstrapDataConfig(OrganizationRepository organizations, AppUserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.organizations=organizations; this.users=users; this.roles=roles; this.encoder=encoder;
    }
    @Override @Transactional public void run(ApplicationArguments args) {
        if (!enabled || users.findFirstByEmailIgnoreCase(adminEmail).isPresent()) return;
        Organization org = organizations.findByNameIgnoreCase(organizationName).orElseGet(() -> organizations.save(new Organization(organizationName, "UTC")));
        AppUser admin = new AppUser(org, adminEmail, encoder.encode(adminPassword), "System", "Administrator");
        admin.addRole(roles.findByCode("SUPER_ADMIN").orElseThrow()); users.save(admin);
    }
}
