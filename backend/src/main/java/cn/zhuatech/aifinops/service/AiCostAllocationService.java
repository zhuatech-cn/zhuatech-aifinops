/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.aifinops.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** 将模型 token 与 GPU 使用量归集到业务项目，并给出预算状态。 */
@Service
public class AiCostAllocationService {
    private static final BigDecimal MILLION = new BigDecimal("1000000");
    public record Request(@NotBlank String provider, @NotBlank String model, @NotBlank String project,
                          @PositiveOrZero long inputTokens, @PositiveOrZero long outputTokens,
                          @PositiveOrZero long gpuMinutes,
                          @DecimalMin("0.0") BigDecimal inputRatePerMillion,
                          @DecimalMin("0.0") BigDecimal outputRatePerMillion,
                          @DecimalMin("0.0") BigDecimal gpuRatePerMinute,
                          @DecimalMin("0.01") BigDecimal monthlyBudget,
                          @PositiveOrZero long businessOutcomeUnits) {}
    public record Result(String project, BigDecimal tokenCost, BigDecimal gpuCost,
                         BigDecimal totalCost, BigDecimal costPerOutcome,
                         BigDecimal budgetUtilization, String budgetState,
                         List<String> optimizationHints) {}

    public Result allocate(Request r) {
        BigDecimal input = BigDecimal.valueOf(r.inputTokens()).multiply(r.inputRatePerMillion()).divide(MILLION, 6, RoundingMode.HALF_UP);
        BigDecimal output = BigDecimal.valueOf(r.outputTokens()).multiply(r.outputRatePerMillion()).divide(MILLION, 6, RoundingMode.HALF_UP);
        BigDecimal token = input.add(output).setScale(4, RoundingMode.HALF_UP);
        BigDecimal gpu = BigDecimal.valueOf(r.gpuMinutes()).multiply(r.gpuRatePerMinute()).setScale(4, RoundingMode.HALF_UP);
        BigDecimal total = token.add(gpu).setScale(4, RoundingMode.HALF_UP);
        BigDecimal utilization = total.multiply(new BigDecimal("100")).divide(r.monthlyBudget(), 2, RoundingMode.HALF_UP);
        BigDecimal perOutcome = r.businessOutcomeUnits() == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(r.businessOutcomeUnits()), 4, RoundingMode.HALF_UP);
        String state = utilization.compareTo(new BigDecimal("100")) >= 0 ? "OVER_BUDGET" : utilization.compareTo(new BigDecimal("80")) >= 0 ? "WATCH" : "HEALTHY";
        List<String> hints = new ArrayList<>();
        if (r.outputTokens() > r.inputTokens()) hints.add("检查输出长度上限并启用结构化短响应");
        if (gpu.compareTo(token) > 0) hints.add("评估 GPU 批处理、弹性关停与规格降级");
        if (utilization.compareTo(new BigDecimal("80")) >= 0) hints.add("启用项目预算告警和调用配额");
        if (hints.isEmpty()) hints.add("成本结构健康，建议持续按业务成果核算单位经济性");
        return new Result(r.project(), token, gpu, total, perOutcome, utilization, state, hints);
    }
}
