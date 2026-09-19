/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aifinops.controller;

import cn.zhuatech.aifinops.common.ApiResponse;
import cn.zhuatech.aifinops.service.AiSpendAnomalyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestController
@RequestMapping("/api/aifinops/insights")
public class AiSpendAnomalyController {
    private final AiSpendAnomalyService service;
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public AiSpendAnomalyController(AiSpendAnomalyService service) { this.service = service; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @PostMapping("/spend-anomaly")
    public ApiResponse<AiSpendAnomalyService.Result> forecast(
        @Valid @RequestBody AiSpendAnomalyService.Request request) {
        return ApiResponse.ok(service.forecast(request));
    }
}
