package project.stylo.web.dto.response

import java.math.BigDecimal

data class DashboardStatsResponse(
    val totalSales: BigDecimal,
    val totalOrders: Int,
    val totalMembers: Int,
    val monthlyOrderCount: Int,
    val monthlyCancelledCount: Int,
    val monthlyCompletedCount: Int,
    val monthlySales: BigDecimal,
    val totalProducts: Int,
    val outOfStockProducts: Int,
    val categoryItemCounts: List<Pair<String, Int>>,
    val salesLast7Days: List<Pair<String, BigDecimal>>
)
