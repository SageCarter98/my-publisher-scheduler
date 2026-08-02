package com.mps.auth.security;
import com.mps.auth.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class MpsUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;
    public MpsUserDetailsService(AppUserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) {
        var user = users.findFirstByEmailIgnoreCase(username).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials."));
        user.unlockIfExpired();
        return UserPrincipal.from(user);
    }
}
