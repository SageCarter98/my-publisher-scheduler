package com.mps.attendance.repository;
import com.mps.attendance.model.AttendanceRegister; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AttendanceRegisterRepository extends JpaRepository<AttendanceRegister,UUID>{ Optional<AttendanceRegister> findByOrganizationIdAndScheduleId(UUID organizationId,UUID scheduleId); }
