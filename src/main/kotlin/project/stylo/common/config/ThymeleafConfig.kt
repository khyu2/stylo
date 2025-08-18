package project.stylo.common.config

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode

@Configuration
class ThymeleafConfig {

    @Bean
    fun templateResolver(): SpringResourceTemplateResolver {
        val templateResolver = SpringResourceTemplateResolver()
        templateResolver.prefix = "classpath:/templates/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.isCacheable = false // 개발 환경에서는 캐시 비활성화
        return templateResolver
    }

    @Bean
    fun templateEngine(): SpringTemplateEngine {
        val templateEngine = SpringTemplateEngine()
        templateEngine.setTemplateResolver(templateResolver())
        templateEngine.addDialect(LayoutDialect())
        templateEngine.addDialect(SpringSecurityDialect())
        return templateEngine
    }
}
