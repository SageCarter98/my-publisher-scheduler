package com.mps.auth.repository;
import com.mps.auth.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection; import java.util.List; import java.util.Optional; import java.util.UUID;
public interface RoleRepository extends JpaRepository<Role, UUID> { Optional<Role> findByCode(String code); List<Role> findAllByCodeIn(Collection<String> codes); }
