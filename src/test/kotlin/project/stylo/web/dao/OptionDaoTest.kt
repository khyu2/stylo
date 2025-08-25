package project.stylo.web.dao

import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import kotlin.collections.emptyList

@SpringBootTest
class OptionDaoTest(
    private val optionDao: OptionDao
): StringSpec({
    "findAllOptionItems test" {
        val optionItems = optionDao.findAllSizeOptions()
        println("Option Items Count: ${optionItems.size}")
        println("Option Items: $optionItems")
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}