package project.stylo.web.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.web.dao.ActionLogRepository
import project.stylo.web.dao.MemberDao
import project.stylo.web.dao.OrdersDao
import project.stylo.web.dao.PaymentDao
import project.stylo.web.dao.ProductDao
import project.stylo.web.domain.enums.OrderStatus
import project.stylo.web.dto.request.ActionLogSearchRequest
import project.stylo.web.dto.response.ActionLogResponse
import project.stylo.web.dto.response.DashboardStatsResponse
import java.math.BigDecimal
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class AdminService(
    private val ordersDao: OrdersDao,
    private val paymentDao: PaymentDao,
    private val memberDao: MemberDao,
    private val productDao: ProductDao,
    private val actionLogRepository: ActionLogRepository
) {
    fun getDashboardStats(): DashboardStatsResponse {
        val totalSales = paymentDao.totalSalesDone()
        val totalOrders = ordersDao.countAll()
        val totalMembers = memberDao.countAll()

        val startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay()
        val startOfNextMonth = startOfMonth.plusMonths(1)

        val monthlyOrderCount = ordersDao.countCreatedBetween(startOfMonth, startOfNextMonth)
        val monthlyCancelledCount =
            ordersDao.countByStatusBetween(OrderStatus.CANCELLED, startOfMonth, startOfNextMonth)
        val monthlyCompletedCount =
            ordersDao.countByStatusBetween(OrderStatus.COMPLETED, startOfMonth, startOfNextMonth)
        val monthlySales = paymentDao.monthlySalesDone(startOfMonth, startOfNextMonth)

        // 상품 통계
        val totalProducts = productDao.countAll()
        val outOfStockProducts = productDao.countOutOfStock()

        // Category 별 상품 수
        val categoryItemCounts = productDao.productCountsByCategory()

        // 최근 7일 매출 통계
        val since = LocalDate.now().minusDays(6)
        val salesByDayMap = paymentDao.salesByDaySince(since)
        val salesLast7Days = (0..6).map { i ->
            val day = since.plusDays(i.toLong())
            day.toString() to (salesByDayMap[day] ?: BigDecimal.ZERO)
        }

        return DashboardStatsResponse(
            totalSales = totalSales,
            totalOrders = totalOrders,
            totalMembers = totalMembers,
            monthlyOrderCount = monthlyOrderCount,
            monthlyCancelledCount = monthlyCancelledCount,
            monthlyCompletedCount = monthlyCompletedCount,
            monthlySales = monthlySales,
            totalProducts = totalProducts,
            outOfStockProducts = outOfStockProducts,
            categoryItemCounts = categoryItemCounts,
            salesLast7Days = salesLast7Days,
        )
    }

    fun getActionLogs(request: ActionLogSearchRequest, pageable: Pageable): Page<ActionLogResponse> {
        val actionLogs = actionLogRepository.search(request, pageable)
        return actionLogs.map { ActionLogResponse.from(it) }
    }
}
