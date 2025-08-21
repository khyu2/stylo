package project.stylo.common.config

import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.jooq.conf.RenderKeywordCase
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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

class SqlExecutor : ExecuteListener {
    companion object {
        private val logger = LoggerFactory.getLogger(SqlExecutor::class.java)
    }

    override fun executeEnd(ctx: ExecuteContext) {
        logger.info("SQL 실행: ${ctx.query()}")
    }
}