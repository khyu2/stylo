package project.stylo.common.ua

/**
 * 파싱된 User-Agent 정보
 */
data class UserAgentInfo(
    val browser: String?,
    val os: String?,
    val device: String?, // Desktop, Mobile, Tablet, Bot 등
) {
    override fun toString(): String {
        return listOfNotNull(browser, os, device).joinToString(" / ")
    }
}