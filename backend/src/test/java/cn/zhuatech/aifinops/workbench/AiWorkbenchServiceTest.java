/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aifinops.workbench;
import org.junit.jupiter.api.Test;
import java.math.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.security.authentication.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
class AiWorkbenchServiceTest {
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void coversAdditionalBoundaryOne(){var row=new HashMap<>((Map<String,Object>)((List<?>)sample().get("records")).get(0));row.put("requestId","r2");row.put("currency","USD");var r=service.execute(Map.of("records",List.of(((List<?>)sample().get("records")).get(0),row)));assertEquals(2,((List<?>)r.get("allocations")).size());}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void coversAdditionalBoundaryTwo(){var row=new HashMap<>((Map<String,Object>)((List<?>)sample().get("records")).get(0));row.put("inputTokens",0);row.put("outputTokens",0);var r=service.execute(Map.of("records",List.of(row)));assertEquals(BigDecimal.ZERO,((Map<?,?>)((List<?>)r.get("allocations")).get(0)).get("amount"));}
 private final AiWorkbenchService service=new AiWorkbenchService();
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 private Map<String,Object> sample(){return Map.<String,Object>ofEntries(Map.entry("records",List.of(Map.<String,Object>ofEntries(Map.entry("requestId","r1"),Map.entry("department","support"),Map.entry("currency","CNY"),Map.entry("inputTokens",1000),Map.entry("outputTokens",500),Map.entry("inputRatePer1k",new BigDecimal("0.01")),Map.entry("outputRatePer1k",new BigDecimal("0.02"))),Map.<String,Object>ofEntries(Map.entry("requestId","r1"),Map.entry("department","support"),Map.entry("currency","CNY"),Map.entry("inputTokens",1000),Map.entry("outputTokens",500),Map.entry("inputRatePer1k",new BigDecimal("0.01")),Map.entry("outputRatePer1k",new BigDecimal("0.02"))))));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void computesBusinessResult(){var result=service.execute(sample());assertEquals(1,result.get("duplicateRecords"));assertEquals(new BigDecimal("0.02"),((Map<?,?>)((List<?>)result.get("allocations")).get(0)).get("amount"));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void coversFeatureBoundary(){var bad=new HashMap<>(sample());var row=new HashMap<>((Map<String,Object>)((List<?>)bad.get("records")).get(0));row.put("outputTokens",999);bad.put("records",List.of(((List<?>)sample().get("records")).get(0),row));assertThrows(IllegalArgumentException.class,()->service.execute(bad));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void deterministicAndDoesNotMutateInput(){var input=sample();String before=input.toString();assertEquals(service.execute(input),service.execute(input));assertEquals(before,input.toString());}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void rejectsNull(){assertThrows(IllegalArgumentException.class,()->service.execute(null));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void rejectsMissingFields(){assertThrows(IllegalArgumentException.class,()->service.execute(Map.of()));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void rejectsUnknownFields(){var input=new HashMap<>(sample());input.put("__unknown",true);assertThrows(IllegalArgumentException.class,()->service.execute(input));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void rejectsOversizedText(){assertThrows(IllegalArgumentException.class,()->service.execute(Map.of("payload","x".repeat(64001))));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void rejectsDeepInput(){Map<String,Object> v=Map.of("x",1);for(int i=0;i<14;i++)v=Map.of("x",v);final var input=v;assertThrows(IllegalArgumentException.class,()->service.execute(input));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void httpRequiresAuthentication()throws Exception{var mvc=MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();mvc.perform(post("/api/workbench/usage-reconciliation").contentType(MediaType.APPLICATION_JSON).content("{\"records\":[{\"requestId\":\"r1\",\"department\":\"support\",\"currency\":\"CNY\",\"inputTokens\":1000,\"outputTokens\":500,\"inputRatePer1k\":0.01,\"outputRatePer1k\":0.02},{\"requestId\":\"r1\",\"department\":\"support\",\"currency\":\"CNY\",\"inputTokens\":1000,\"outputTokens\":500,\"inputRatePer1k\":0.01,\"outputRatePer1k\":0.02}]}")).andExpect(status().isUnauthorized());}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void httpAllowsAuthenticatedComputation()throws Exception{var mvc=MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();mvc.perform(post("/api/workbench/usage-reconciliation").principal(new TestingAuthenticationToken("test-user","unused","ROLE_OPERATOR")).contentType(MediaType.APPLICATION_JSON).content("{\"records\":[{\"requestId\":\"r1\",\"department\":\"support\",\"currency\":\"CNY\",\"inputTokens\":1000,\"outputTokens\":500,\"inputRatePer1k\":0.01,\"outputRatePer1k\":0.02},{\"requestId\":\"r1\",\"department\":\"support\",\"currency\":\"CNY\",\"inputTokens\":1000,\"outputTokens\":500,\"inputRatePer1k\":0.01,\"outputRatePer1k\":0.02}]}")).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void httpRejectsInvalidInput()throws Exception{var mvc=MockMvcBuilders.standaloneSetup(new AiWorkbenchController(service)).build();mvc.perform(post("/api/workbench/usage-reconciliation").principal(new TestingAuthenticationToken("test-user","unused","ROLE_OPERATOR")).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.success").value(false));}
 /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
 @Test void rejectsAnonymousAuthenticationObject(){var actor=new AnonymousAuthenticationToken("test-key","anonymous",List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ANONYMOUS")));assertEquals(401,new AiWorkbenchController(service).run(sample(),actor).getStatusCode().value());}
}
