/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.aifinops.controller;

import cn.zhuatech.aifinops.common.ApiResponse;
import cn.zhuatech.aifinops.service.AiSpendAnomalyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aifinops/insights")
public class AiSpendAnomalyController {
    private final AiSpendAnomalyService service;
    public AiSpendAnomalyController(AiSpendAnomalyService service) { this.service = service; }
    @PostMapping("/spend-anomaly")
    public ApiResponse<AiSpendAnomalyService.Result> forecast(
        @Valid @RequestBody AiSpendAnomalyService.Request request) {
        return ApiResponse.ok(service.forecast(request));
    }
}
