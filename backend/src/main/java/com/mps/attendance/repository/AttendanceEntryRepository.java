package com.mps.attendance.repository;
import com.mps.attendance.model.AttendanceEntry; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AttendanceEntryRepository extends JpaRepository<AttendanceEntry,UUID>{ List<AttendanceEntry> findAllByRegisterIdOrderByMemberLastNameAscMemberFirstNameAsc(UUID registerId); Optional<AttendanceEntry> findByRegisterIdAndMemberId(UUID registerId,UUID memberId); }
