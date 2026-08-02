package com.mps.attendance;
import com.mps.attendance.model.AttendanceStatus; import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class AttendanceStatusTests { @Test void supportsApprovedStatuses(){assertThat(AttendanceStatus.values()).extracting(Enum::name).containsExactly("PRESENT","ABSENT","EXCUSED","LATE");} }
