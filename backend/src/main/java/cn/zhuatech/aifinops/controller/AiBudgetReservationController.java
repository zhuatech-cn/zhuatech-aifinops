/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aifinops.controller;

import cn.zhuatech.aifinops.common.ApiResponse;
import cn.zhuatech.aifinops.service.AiBudgetReservationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/aifinops/insights/budget-reservations")
public class AiBudgetReservationController {
    private final AiBudgetReservationService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AiBudgetReservationController(AiBudgetReservationService service) { this.service = service; }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping
    public ApiResponse<AiBudgetReservationService.ReservationResult> reserve(
            @Valid @RequestBody AiBudgetReservationService.ReservationRequest request) {
        return ApiResponse.ok("AI 预算预占评估完成", service.reserve(request));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @DeleteMapping("/{tenantId}/{budgetCode}/{reservationId}")
    public ApiResponse<AiBudgetReservationService.ReleaseResult> release(
            @PathVariable String tenantId, @PathVariable String budgetCode, @PathVariable String reservationId) {
        return ApiResponse.ok("AI 预算预占释放完成", service.release(tenantId, budgetCode, reservationId));
    }
}
