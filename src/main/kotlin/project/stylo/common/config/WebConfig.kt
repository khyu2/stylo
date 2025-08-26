package project.stylo.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import project.stylo.web.interceptor.CartInterceptor

@Configuration
class WebConfig(
    private val cartInterceptor: CartInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(cartInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error")
    }
}