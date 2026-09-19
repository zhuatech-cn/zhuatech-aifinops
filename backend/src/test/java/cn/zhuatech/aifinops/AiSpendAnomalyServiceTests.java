/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aifinops;

import cn.zhuatech.aifinops.service.AiSpendAnomalyService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class AiSpendAnomalyServiceTests {
    private final AiSpendAnomalyService service = new AiSpendAnomalyService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void throttlesSevereBudgetOverrun() {
        var result = service.forecast(new AiSpendAnomalyService.Request(
            "AGENT-HUB", new BigDecimal("8000"), new BigDecimal("500"),
            new BigDecimal("250"), new BigDecimal("10000"), 15, 30, new BigDecimal("0.2")));
        assertEquals(new BigDecimal("15500.00"), result.forecastSpend());
        assertEquals("THROTTLE", result.decision());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void keepsStableSpendNormal() {
        var result = service.forecast(new AiSpendAnomalyService.Request(
            "SEARCH", new BigDecimal("3000"), new BigDecimal("180"),
            new BigDecimal("170"), new BigDecimal("8000"), 15, 30, new BigDecimal("0.6")));
        assertEquals("NORMAL", result.decision());
    }
}
