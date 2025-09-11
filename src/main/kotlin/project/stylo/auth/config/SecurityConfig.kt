package project.stylo.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import project.stylo.auth.handler.CustomFormLoginSuccessHandler
import project.stylo.auth.handler.CustomOAuth2SuccessHandler
import project.stylo.auth.resolver.AuthArgumentResolver
import project.stylo.auth.service.MemberDetailsService

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
class SecurityConfig(
    private val authArgumentResolver: AuthArgumentResolver,
    private val memberDetailsService: MemberDetailsService,
    private val customOAuth2SuccessHandler: CustomOAuth2SuccessHandler,
    private val customFormLoginSuccessHandler: CustomFormLoginSuccessHandler
) : WebMvcConfigurer {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/products/*",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/swagger-ui/**",
                    "/api-docs/**",
                    "/error",
                    "/oauth2/**",
                    "/login/oauth2/**",
                    "/oauth2/authorization/**"
                ).permitAll()
                it.requestMatchers("/admin/**").hasRole("ADMIN")
                it.anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/login")
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .successHandler(customFormLoginSuccessHandler)
                    .failureUrl("/login?error=true")
                    .permitAll()
            }
            .oauth2Login {
                it.loginPage("/login")
                    .successHandler(customOAuth2SuccessHandler)
            }
            .rememberMe {
                it.key("REMEMBER_ME_KEY")
                    .rememberMeParameter("remember-me")
                    .tokenValiditySeconds(60 * 60 * 24 * 14) // 14 days
                    .alwaysRemember(false)
                    .useSecureCookie(false) // HTTPS 환경에서만 쿠키 사용
                    .userDetailsService(memberDetailsService)
                    // cartCount 동기화를 위해 성공 핸들러 재설정. 일반적으로 필요 X
                    .authenticationSuccessHandler(customFormLoginSuccessHandler)
            }
            .logout {
                it.logoutUrl("/logout")
                it.logoutSuccessUrl("/")
                it.invalidateHttpSession(true)
                it.deleteCookies("JSESSIONID", "REMEMBER_ME")
            }
            .httpBasic { it.disable() }
            .headers { it.frameOptions { config -> config.sameOrigin() } }
            .userDetailsService(memberDetailsService)
            .build()
    }

    // TODO: 실서버 배포 시 CORS 설정 변경 필요
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration().apply {
            addAllowedOriginPattern("*")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(authArgumentResolver)
    }
}