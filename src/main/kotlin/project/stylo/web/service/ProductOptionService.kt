package project.stylo.web.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.common.config.CacheConfig.Companion.CATEGORY_CACHE
import project.stylo.web.dao.OptionValueDao

@Service
@Transactional
class ProductOptionService(
    private val optionValueDao: OptionValueDao
) {
    @Cacheable(CATEGORY_CACHE, key = "'genderOptions'")
    @Transactional(readOnly = true)
    fun getGenderOptions(): List<String> = optionValueDao.findAllGenderOptions()

    @Cacheable(CATEGORY_CACHE, key = "'sizeOptions'")
    @Transactional(readOnly = true)
    fun getSizeOptions(): List<String> = optionValueDao.findAllSizeOptions()

    @Cacheable(CATEGORY_CACHE, key = "'colorOptions'")
    @Transactional(readOnly = true)
    fun getColorOptions(): List<String> = optionValueDao.findAllColorOptions()
}