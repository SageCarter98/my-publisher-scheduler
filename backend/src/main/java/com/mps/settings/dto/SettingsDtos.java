package com.mps.settings.dto; import jakarta.validation.constraints.*; import java.time.Instant; import java.util.*;
public final class SettingsDtos {private SettingsDtos(){}
 public record SettingView(UUID id,String key,String value,String valueType,String description,Instant updatedAt,UUID updatedBy){}
 public record UpdateSettingRequest(@NotBlank @Size(max=120) String key,@NotNull String value,@NotBlank @Pattern(regexp="STRING|BOOLEAN|INTEGER|DECIMAL|DURATION") String valueType,@Size(max=500) String description){}
}
