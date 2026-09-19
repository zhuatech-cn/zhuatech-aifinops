/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aifinops;

import cn.zhuatech.aifinops.service.AiBudgetReservationService;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class AiBudgetReservationServiceTests {
    private final AiBudgetReservationService service = new AiBudgetReservationService();

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void reservesAndReplaysIdempotently() {
        var first = service.reserve(request("R-1", "100", "400", false, "0", 1, 10, false));
        var replay = service.reserve(request("R-1", "100", "400", false, "0", 1, 10, false));
        assertThat(first.status()).isEqualTo(AiBudgetReservationService.Status.RESERVED);
        assertThat(replay.idempotentReplay()).isTrue();
        assertThat(service.release("T-1", "B-1", "R-1").released()).isTrue();
        assertThat(service.release("T-1", "B-1", "R-1").released()).isFalse();
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void appliesFallbackBeforeReservation() {
        var result = service.reserve(request("R-2", "400", "800", true, "0.50", 1, 10, false));
        assertThat(result.status()).isEqualTo(AiBudgetReservationService.Status.FALLBACK_RESERVED);
        assertThat(result.effectiveReservedCost()).isEqualByComparingTo("200.00");
        assertThat(result.projectedSpend()).isEqualByComparingTo("1000.00");
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void deniesConcurrencyAndNoncriticalHardLimitOverrun() {
        var concurrent = service.reserve(request("R-3", "100", "100", false, "0", 10, 10, false));
        var overrun = service.reserve(request("R-4", "600", "900", false, "0", 1, 10, false));
        assertThat(concurrent.status()).isEqualTo(AiBudgetReservationService.Status.DENIED);
        assertThat(overrun.status()).isEqualTo(AiBudgetReservationService.Status.DENIED);
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    private AiBudgetReservationService.ReservationRequest request(String id, String predicted,
            String committed, boolean fallback, String saving, int current, int max, boolean critical) {
        return new AiBudgetReservationService.ReservationRequest(id, "T-1", "B-1", "SEARCH",
                new BigDecimal(predicted), new BigDecimal(committed), new BigDecimal("1000"),
                new BigDecimal("1.20"), fallback, new BigDecimal(saving), critical, current, max);
    }
}
