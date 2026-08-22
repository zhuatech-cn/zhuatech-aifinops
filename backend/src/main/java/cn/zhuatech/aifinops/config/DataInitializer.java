/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.aifinops.config;

import cn.zhuatech.aifinops.model.*;
import cn.zhuatech.aifinops.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(OperatingUnitRepository operatingUnits, WorkRecordRepository orders,
                           ResourceRegisterRepository resources, ReviewRecordRepository reviewRecords,
                           UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (operatingUnits.count() > 0) return;
            OperatingUnit primaryUnit = operatingUnits.save(new OperatingUnit("AIFINOPS-PLATFORM", "AI 平台成本组", "技术平台中心", 180));
            OperatingUnit secondaryUnit = operatingUnits.save(new OperatingUnit("AIFINOPS-BIZ", "业务价值分析组", "数字化办公室", 120));
            OperatingUnit tertiaryUnit = operatingUnits.save(new OperatingUnit("AIFINOPS-INFRA", "算力运营组", "云基础设施部", 96));

            WorkRecord t1 = orders.save(new WorkRecord("COST-260801-018", "CS-COPILOT", "客服助手推理成本优化", primaryUnit, 24, 16, 1, LocalDate.now().plusDays(1), WorkRecord.Status.RUNNING, "BUD-240K"));
            WorkRecord t2 = orders.save(new WorkRecord("COST-260801-021", "DOC-OCR", "票据识别 GPU 批处理优化", tertiaryUnit, 18, 8, 0, LocalDate.now().plusDays(1), WorkRecord.Status.RUNNING, "BUD-120K"));
            WorkRecord t3 = orders.save(new WorkRecord("COST-260802-006", "SALES-AGENT", "销售 Agent 单位成果核算", secondaryUnit, 12, 0, 0, LocalDate.now().plusDays(3), WorkRecord.Status.RELEASED, "BUD-80K"));
            WorkRecord t4 = orders.save(new WorkRecord("COST-260728-015", "SEARCH-RAG", "知识检索缓存策略复盘", primaryUnit, 20, 20, 1, LocalDate.now(), WorkRecord.Status.COMPLETED, "BUD-65K"));

            resources.saveAll(List.of(
                new ResourceRegister("COST-MODEL-03", "模型 API 账单采集", primaryUnit, ResourceRegister.Status.RUNNING, 96),
                new ResourceRegister("COST-GPU-02", "GPU 利用率采集", tertiaryUnit, ResourceRegister.Status.IDLE, 78),
                new ResourceRegister("COST-TAG-05", "项目标签覆盖", secondaryUnit, ResourceRegister.Status.RUNNING, 91),
                new ResourceRegister("COST-BUDGET-08", "预算告警策略", primaryUnit, ResourceRegister.Status.ALARM, 64)
            ));
            reviewRecords.saveAll(List.of(
                new ReviewRecord("BUD-260801-032", t1, "路由降本验证", 6, 0, ReviewRecord.Result.PASSED, "何谨"),
                new ReviewRecord("BUD-260801-011", t2, "GPU 批处理效果", 3, 0, ReviewRecord.Result.PASSED, "陆遥"),
                new ReviewRecord("BUD-260801-018", t4, "缓存命中率复核", 5, 1, ReviewRecord.Result.FAILED, "何谨"),
                new ReviewRecord("BUD-260802-003", t3, "业务成果口径确认", 4, 0, ReviewRecord.Result.PENDING, "陆遥")
            ));
            String demo = encoder.encode("Demo@2026");
            users.saveAll(List.of(
                new UserAccount("operator", demo, "陆遥", UserAccount.Role.DOMAIN_USER, "AIFINOPS-PLATFORM"),
                new UserAccount("planner", demo, "何谨", UserAccount.Role.DOMAIN_OPERATOR, null),
                new UserAccount("quality", demo, "顾清", UserAccount.Role.QUALITY, null),
                new UserAccount("admin", encoder.encode("ZhuaTech@2026"), "系统管理员", UserAccount.Role.ADMIN, null)
            ));
        };
    }
}
