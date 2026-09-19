# AI 预算预占与并发控制

`POST /api/aifinops/insights/budget-reservations` 在模型调用前按租户、预算中心和业务负载预占预测成本，支持幂等请求、软预算、硬限额、关键业务审批、并发上限以及低成本模型或缓存降本路由。

返回状态包括 `RESERVED`、`FALLBACK_RESERVED`、`REVIEW` 和 `DENIED`。成功预占后可调用 `DELETE /api/aifinops/insights/budget-reservations/{tenantId}/{budgetCode}/{reservationId}` 释放额度；重复释放不会重复扣减。

社区版使用线程安全的内存预占表，便于直接运行和测试。生产部署建议将幂等键和预占账本替换为带事务或原子脚本的共享存储。
