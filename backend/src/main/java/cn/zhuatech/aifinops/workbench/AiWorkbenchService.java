/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aifinops.workbench;
import org.springframework.stereotype.Service;
import java.math.*;
import java.util.*;
import static cn.zhuatech.aifinops.workbench.WorkbenchInput.*;
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
@Service
public class AiWorkbenchService {
    /** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
    public Map<String,Object> execute(Map<String,Object> r){
        validate(r);

        keys(r,"records");var items=rows(r,"records",1,200);Map<String,String> fingerprints=new HashMap<>();Map<String,BigDecimal> totals=new TreeMap<>();Map<String,Integer> counts=new TreeMap<>();int duplicate=0;
        for(var row:items){keys(row,"requestId","department","currency","inputTokens","outputTokens","inputRatePer1k","outputRatePer1k");String id=id(row,"requestId"),department=id(row,"department"),currency=text(row,"currency",3);if(!currency.matches("[A-Z]{3}"))throw new IllegalArgumentException("币种须为三位大写代码");
            int in=integer(row,"inputTokens",0,1000000000),out=integer(row,"outputTokens",0,1000000000);BigDecimal ir=decimal(row,"inputRatePer1k",0,1000000),or=decimal(row,"outputRatePer1k",0,1000000);
            String print=department+"|"+currency+"|"+in+"|"+out+"|"+ir.stripTrailingZeros().toPlainString()+"|"+or.stripTrailingZeros().toPlainString();String previous=fingerprints.putIfAbsent(id,print);if(previous!=null){if(!previous.equals(print))throw new IllegalArgumentException("相同请求编号存在账单冲突");duplicate++;continue;}
            BigDecimal amount=ir.multiply(BigDecimal.valueOf(in)).add(or.multiply(BigDecimal.valueOf(out))).divide(BigDecimal.valueOf(1000));String key=currency+"|"+department;totals.merge(key,amount,BigDecimal::add);counts.merge(key,1,Integer::sum);
        }
        List<Map<String,Object>> allocation=new ArrayList<>();for(var e:totals.entrySet()){String[] key=e.getKey().split("\\|",2);allocation.add(Map.of("currency",key[0],"department",key[1],"amount",e.getValue().stripTrailingZeros(),"requests",counts.get(e.getKey())));}
        return Map.of("uniqueRequests",fingerprints.size(),"duplicateRecords",duplicate,"allocations",List.copyOf(allocation),"initiatesPayments",false);

    }
}
