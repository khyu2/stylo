package project.stylo.common.config

import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.jooq.conf.RenderKeywordCase
import org.jooq.tools.StopWatch
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class JooqConfig {
    @Bean
    fun jooqDefaultConfigurationCustomizer(): DefaultConfigurationCustomizer =
        DefaultConfigurationCustomizer { c ->
            c.setExecuteListener(SqlExecutor())
            c.settings()
                .withRenderSchema(false) // 스키마 이름 렌더링 비활성화
                .withExecuteLogging(false) // SQL 실행 로그 비활성화
                .withRenderKeywordCase(RenderKeywordCase.UPPER) // 키워드 대문자 렌더링
        }
}

class SqlExecutor() : ExecuteListener {
    private lateinit var watch: StopWatch

    companion object {
        private val logger = LoggerFactory.getLogger(SqlExecutor::class.java)
        private val SLOW_QUERY_THRESHOLD_MS = Duration.ofSeconds(2)
    }

    override fun executeStart(ctx: ExecuteContext?) {
        watch = StopWatch()
    }

    override fun executeEnd(ctx: ExecuteContext) {
        val elapsedTime = watch.split()
        val elapsedTimeS = String.format("%.2f", elapsedTime / 1_000_000_000.0) // ns -> seconds

        if (elapsedTime > SLOW_QUERY_THRESHOLD_MS.toNanos()) {
            logger.warn("실행된 쿼리 중 ${SLOW_QUERY_THRESHOLD_MS.seconds}초가 지난 쿼리가 있습니다." +
                    " 실행시간: ${elapsedTimeS}s - SQL: ${ctx.query()}")
        } else {
            logger.info("SQL 실행 완료: ${elapsedTimeS}s - ${ctx.query()}")
        }
    }
}