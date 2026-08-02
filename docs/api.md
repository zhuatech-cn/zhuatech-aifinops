# AI FinOps API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | AI 成本与预算总览 |
| GET | `/api/admin/work-orders` | 成本优化任务 |
| GET | `/api/shopfloor/dashboard` | 成本分析师工作台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交优化和收益验证结果 |
| POST | `/api/shopfloor/cost-allocation` | 归集 Token、GPU 成本并计算预算状态 |

金额采用 `BigDecimal` 计算。请求价格单位由调用方维护，演示接口不代表任何云或模型服务商实际价格。
