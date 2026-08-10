/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.aifinops.service;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiSpendAnomalyService {
    public Result forecast(Request request) {
        int remainingDays = Math.max(0, request.periodDays() - request.daysElapsed());
        BigDecimal forecastSpend = request.currentMonthSpend()
            .add(request.currentDailySpend().multiply(BigDecimal.valueOf(remainingDays)))
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal budgetUsage = forecastSpend.divide(request.monthlyBudget(), 4, RoundingMode.HALF_UP);
        BigDecimal dailyVariance = request.baselineDailySpend().signum() == 0 ? BigDecimal.ZERO
            : request.currentDailySpend().subtract(request.baselineDailySpend())
                .divide(request.baselineDailySpend(), 4, RoundingMode.HALF_UP);
        String decision = budgetUsage.compareTo(new BigDecimal("1.20")) > 0 ? "THROTTLE"
            : budgetUsage.compareTo(BigDecimal.ONE) > 0 || dailyVariance.compareTo(new BigDecimal("0.30")) > 0
                ? "OPTIMIZE" : "NORMAL";
        List<String> actions = new ArrayList<>();
        if (request.cacheHitRate().compareTo(new BigDecimal("0.40")) < 0) actions.add("提高提示词和检索结果缓存命中率");
        if ("THROTTLE".equals(decision)) actions.add("限制非关键调用并启用低成本模型路由");
        if ("OPTIMIZE".equals(decision)) actions.add("下钻模型、团队和功能维度定位成本增量");
        if ("NORMAL".equals(decision)) actions.add("保持预算策略并按日复核预测偏差");
        return new Result(request.applicationCode(), forecastSpend, budgetUsage,
            dailyVariance, decision, actions);
    }

    public record Request(@NotBlank String applicationCode,
                          @DecimalMin("0") BigDecimal currentMonthSpend,
                          @DecimalMin("0") BigDecimal currentDailySpend,
                          @DecimalMin("0") BigDecimal baselineDailySpend,
                          @DecimalMin("0.01") BigDecimal monthlyBudget,
                          @Min(0) int daysElapsed, @Min(1) int periodDays,
                          @DecimalMin("0") @DecimalMax("1") BigDecimal cacheHitRate) {}
    public record Result(String applicationCode, BigDecimal forecastSpend,
                         BigDecimal budgetUsageRate, BigDecimal dailyVarianceRate,
                         String decision, List<String> actions) {}
}
