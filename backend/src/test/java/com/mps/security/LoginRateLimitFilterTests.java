package com.mps.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import java.time.Clock; import java.time.Instant; import java.time.ZoneOffset;
import static org.assertj.core.api.Assertions.assertThat;
class LoginRateLimitFilterTests {
 @Test void rejectsAttemptsBeyondLimit() throws Exception {
  var filter=new LoginRateLimitFilter(new ObjectMapper(), Clock.fixed(Instant.parse("2026-08-02T00:00:00Z"), ZoneOffset.UTC),2,60);
  for(int i=0;i<2;i++){var req=request();var res=new MockHttpServletResponse();filter.doFilter(req,res,new MockFilterChain());assertThat(res.getStatus()).isEqualTo(200);}
  var res=new MockHttpServletResponse();filter.doFilter(request(),res,new MockFilterChain());
  assertThat(res.getStatus()).isEqualTo(429); assertThat(res.getHeader("Retry-After")).isEqualTo("60");
 }
 private MockHttpServletRequest request(){var r=new MockHttpServletRequest("POST","/api/v1/auth/login");r.setRemoteAddr("192.0.2.10");return r;}
}
