package com.mps.security;
import org.junit.jupiter.api.Test; import org.springframework.mock.web.*;
import static org.assertj.core.api.Assertions.assertThat;
class CorrelationIdFilterTests {
 @Test void createsCorrelationIdWhenMissing() throws Exception {var req=new MockHttpServletRequest();var res=new MockHttpServletResponse();new CorrelationIdFilter().doFilter(req,res,new MockFilterChain());assertThat(res.getHeader(CorrelationIdFilter.HEADER)).isNotBlank();}
 @Test void preservesValidCorrelationId() throws Exception {var req=new MockHttpServletRequest();req.addHeader(CorrelationIdFilter.HEADER,"request-12345");var res=new MockHttpServletResponse();new CorrelationIdFilter().doFilter(req,res,new MockFilterChain());assertThat(res.getHeader(CorrelationIdFilter.HEADER)).isEqualTo("request-12345");}
}
