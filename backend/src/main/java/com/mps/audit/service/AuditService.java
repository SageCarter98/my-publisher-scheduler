package com.mps.audit.service;
import com.fasterxml.jackson.core.JsonProcessingException; import com.fasterxml.jackson.databind.ObjectMapper;
import com.mps.audit.model.AuditLog; import com.mps.audit.repository.AuditLogRepository; import com.mps.organization.model.Organization;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Propagation; import org.springframework.transaction.annotation.Transactional;
import java.util.Map; import java.util.UUID;
@Service public class AuditService {
 private final AuditLogRepository repository; private final ObjectMapper mapper;
 public AuditService(AuditLogRepository repository,ObjectMapper mapper){this.repository=repository;this.mapper=mapper;}
 @Transactional(propagation=Propagation.REQUIRES_NEW)
 public void record(Organization org,UUID actor,String action,String type,UUID entity,String outcome,Map<String,?> details){repository.save(new AuditLog(org,actor,action,type,entity,outcome,json(details)));}
 private String json(Map<String,?> details){try{return mapper.writeValueAsString(details==null?Map.of():details);}catch(JsonProcessingException e){return "{}";}}
}
