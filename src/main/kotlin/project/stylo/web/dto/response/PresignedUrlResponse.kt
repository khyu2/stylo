package project.stylo.web.dto.response

data class PresignedUrlResponse(val url: String) {
    companion object {
        fun from(url: String) = PresignedUrlResponse(url)
    }
}
