package com.mps.availability.dto;
import com.mps.availability.model.AvailabilityStatus; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.UUID;
public final class AvailabilityDtos { private AvailabilityDtos(){}
 public record CreateAvailabilityRequest(@NotNull Instant startAt,@NotNull Instant endAt,@NotNull AvailabilityStatus status,@Size(max=500) String notes){}
 public record AvailabilityView(UUID id,Instant startAt,Instant endAt,AvailabilityStatus status,String notes){}
}
