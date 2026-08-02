package com.mps.audit;

import com.mps.auth.security.UserPrincipal;
import com.mps.common.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@RestController @RequestMapping("/api/v1/audit")
public class AuditController {
 private final JdbcTemplate jdbc;
 public AuditController(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public record AuditView(UUID id,UUID actorUserId,String action,String entityType,UUID entityId,String outcome,String details,Instant occurredAt){}

 @GetMapping @PreAuthorize("hasAuthority('audit.read')")
 public ApiResponse<List<AuditView>> list(@AuthenticationPrincipal UserPrincipal p,
   @RequestParam(required=false) String action,@RequestParam(required=false) String entityType,@RequestParam(required=false) String outcome,
   @RequestParam(required=false) UUID actorUserId,
   @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
   @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to,
   @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="50") int size){
   Query q=query(p.organizationId(),action,entityType,outcome,actorUserId,from,to,Math.max(0,page),Math.min(Math.max(1,size),100));
   List<AuditView> data=jdbc.query(q.sql(),(rs,n)->new AuditView(rs.getObject("id",UUID.class),rs.getObject("actor_user_id",UUID.class),rs.getString("action"),rs.getString("entity_type"),rs.getObject("entity_id",UUID.class),rs.getString("outcome"),rs.getString("details"),rs.getTimestamp("occurred_at").toInstant()),q.args().toArray());
   return ApiResponse.ok("Audit events loaded.",data);
 }

 @GetMapping("/export") @PreAuthorize("hasAuthority('audit.read')")
 public ResponseEntity<byte[]> export(@AuthenticationPrincipal UserPrincipal p,@RequestParam(required=false) String action,@RequestParam(required=false) String entityType,@RequestParam(required=false) String outcome,
   @RequestParam(required=false) UUID actorUserId,@RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
   @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to){
   Query q=query(p.organizationId(),action,entityType,outcome,actorUserId,from,to,0,10000); StringBuilder b=new StringBuilder("Occurred At,Actor,Action,Entity Type,Entity ID,Outcome,Details\n");
   jdbc.query(q.sql(),rs->{b.append(csv(rs.getTimestamp("occurred_at").toInstant().toString())).append(',').append(csv(Objects.toString(rs.getObject("actor_user_id"),""))).append(',').append(csv(rs.getString("action"))).append(',').append(csv(rs.getString("entity_type"))).append(',').append(csv(Objects.toString(rs.getObject("entity_id"),""))).append(',').append(csv(rs.getString("outcome"))).append(',').append(csv(rs.getString("details"))).append('\n');},q.args().toArray());
   return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/csv")).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"mps-audit-export.csv\"").body(b.toString().getBytes(StandardCharsets.UTF_8));
 }

 private Query query(UUID org,String action,String entityType,String outcome,UUID actor,Instant from,Instant to,int page,int size){
   StringBuilder sql=new StringBuilder("select id,actor_user_id,action,entity_type,entity_id,outcome,details::text,occurred_at from audit_log where organization_id=?"); List<Object> args=new ArrayList<>(); args.add(org);
   if(action!=null&&!action.isBlank()){sql.append(" and lower(action) like lower(?)");args.add("%"+action.trim()+"%");}
   if(entityType!=null&&!entityType.isBlank()){sql.append(" and lower(entity_type)=lower(?)");args.add(entityType.trim());}
   if(outcome!=null&&!outcome.isBlank()){sql.append(" and lower(outcome)=lower(?)");args.add(outcome.trim());}
   if(actor!=null){sql.append(" and actor_user_id=?");args.add(actor);}
   if(from!=null){sql.append(" and occurred_at>=?");args.add(from);}
   if(to!=null){sql.append(" and occurred_at<?");args.add(to);}
   sql.append(" order by occurred_at desc limit ? offset ?");args.add(size);args.add(page*size);return new Query(sql.toString(),args);
 }
 private String csv(String v){String x=v==null?"":v;return '"'+x.replace("\"","\"\"")+'"';}
 private record Query(String sql,List<Object> args){}
}
