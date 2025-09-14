package project.stylo.common.ua

import nl.basjes.parse.useragent.UserAgent
import nl.basjes.parse.useragent.UserAgentAnalyzer

object UserAgentParser {
    private val analyzer: UserAgentAnalyzer by lazy {
        UserAgentAnalyzer
            .newBuilder()
            .withFields(
                "AgentName",
                "OperatingSystemName",
                "DeviceClass"
            )
            .hideMatcherLoadStats()
            .immediateInitialization()
            .build()
    }

    fun parse(ua: String?): UserAgentInfo {
        if (ua.isNullOrBlank()) return UserAgentInfo(browser = null, os = null, device = null)

        val parsed: UserAgent = try {
            analyzer.parse(ua)
        } catch (e: Exception) {
            return UserAgentInfo(browser = null, os = null, device = null)
        }

        val browser = parsed["AgentName"]?.value?.takeIf { it.isNotBlank() }
        val os = parsed["OperatingSystemName"]?.value?.takeIf { it.isNotBlank() }
        val deviceClass = parsed["DeviceClass"]?.value?.takeIf { it.isNotBlank() }

        val device = when (deviceClass) {
            null -> null
            "Robot" -> "Bot"
            "Phone", "Mobile" -> "Mobile"
            "Tablet" -> "Tablet"
            "Desktop" -> "Desktop"
            else -> deviceClass
        }

        return UserAgentInfo(browser = browser, os = os, device = device)
    }
}