/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 为模型调用预占预算，支持幂等重放、降本路由、并发上限和释放。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class AiBudgetReservationService {
    private final ConcurrentMap<String, ReservationResult> reservations = new ConcurrentHashMap<>();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ReservationResult reserve(ReservationRequest request) {
        String key = key(request.tenantId(), request.budgetCode(), request.reservationId());
        ReservationResult existing = reservations.get(key);
        if (existing != null) return replay(existing);

        BigDecimal requested = money(request.predictedCost());
        BigDecimal effective = requested;
        BigDecimal projected = money(request.committedSpend().add(effective));
        BigDecimal hardLimit = money(request.monthlyBudget().multiply(request.hardLimitRate()));
        List<String> controls = new ArrayList<>();
        Status status;

        if (request.currentConcurrentReservations() >= request.maxConcurrentReservations()) {
            status = Status.DENIED;
            controls.add("并发预算预占已达上限，等待已有调用完成或释放");
        } else {
            if (projected.compareTo(request.monthlyBudget()) > 0 && request.fallbackEnabled()) {
                effective = money(requested.multiply(BigDecimal.ONE.subtract(request.fallbackSavingRate())));
                projected = money(request.committedSpend().add(effective));
                controls.add("启用低成本模型或缓存路由，预计节省 " + money(requested.subtract(effective)));
            }
            if (projected.compareTo(hardLimit) > 0) {
                status = request.criticalWorkload() ? Status.REVIEW : Status.DENIED;
                controls.add(request.criticalWorkload() ? "关键业务超硬限额，转预算责任人紧急审批" : "超过预算硬限额，拒绝预占");
            } else if (projected.compareTo(request.monthlyBudget()) > 0) {
                status = Status.REVIEW;
                controls.add("超过月度软预算，转成本中心负责人审批");
            } else if (effective.compareTo(requested) < 0) {
                status = Status.FALLBACK_RESERVED;
                controls.add("按降本后的预测金额完成预算预占");
            } else {
                status = Status.RESERVED;
                controls.add("完成预算预占，实际结算后应释放差额");
            }
        }

        ReservationResult result = new ReservationResult(request.reservationId(), status, requested,
                effective, projected, hardLimit, false, List.copyOf(controls));
        if (status == Status.RESERVED || status == Status.FALLBACK_RESERVED) {
            ReservationResult raced = reservations.putIfAbsent(key, result);
            if (raced != null) return replay(raced);
        }
        return result;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public ReleaseResult release(String tenantId, String budgetCode, String reservationId) {
        ReservationResult removed = reservations.remove(key(tenantId, budgetCode, reservationId));
        return new ReleaseResult(reservationId, removed != null,
                removed == null ? BigDecimal.ZERO.setScale(2) : removed.effectiveReservedCost());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private ReservationResult replay(ReservationResult existing) {
        return new ReservationResult(existing.reservationId(), existing.status(), existing.requestedCost(),
                existing.effectiveReservedCost(), existing.projectedSpend(), existing.hardLimit(),
                true, existing.controls());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private String key(String tenantId, String budgetCode, String reservationId) {
        return tenantId + "|" + budgetCode + "|" + reservationId;
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ReservationRequest(@NotBlank String reservationId, @NotBlank String tenantId,
            @NotBlank String budgetCode, @NotBlank String workloadCode,
            @DecimalMin("0.01") BigDecimal predictedCost,
            @DecimalMin("0") BigDecimal committedSpend,
            @DecimalMin("0.01") BigDecimal monthlyBudget,
            @DecimalMin("1.0") @DecimalMax("2.0") BigDecimal hardLimitRate,
            boolean fallbackEnabled,
            @DecimalMin("0") @DecimalMax("0.90") BigDecimal fallbackSavingRate,
            boolean criticalWorkload, @Min(0) int currentConcurrentReservations,
            @Min(1) int maxConcurrentReservations) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ReservationResult(String reservationId, Status status, BigDecimal requestedCost,
            BigDecimal effectiveReservedCost, BigDecimal projectedSpend, BigDecimal hardLimit,
            boolean idempotentReplay, List<String> controls) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record ReleaseResult(String reservationId, boolean released, BigDecimal releasedAmount) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public enum Status { RESERVED, FALLBACK_RESERVED, REVIEW, DENIED }
}
