package project.stylo.web.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.common.config.CacheConfig.Companion.CATEGORY_CACHE
import project.stylo.web.dao.CategoryDao
import project.stylo.web.dao.OptionDao

@Service
@Transactional
class CategoryService(
    private val optionDao: OptionDao,
    private val categoryDao: CategoryDao
) {
    @Cacheable(CATEGORY_CACHE, key = "'categories'")
    @Transactional(readOnly = true)
    fun getAllCategories() = categoryDao.findAll()

    @Cacheable(CATEGORY_CACHE, key = "'genderOptions'")
    @Transactional(readOnly = true)
    fun getAllGenderOptions() = optionDao.findAllGenderOptions()

    @Cacheable(CATEGORY_CACHE, key = "'sizeOptions'")
    @Transactional(readOnly = true)
    fun getAllSizeOptions() = optionDao.findAllSizeOptions()

    @Cacheable(CATEGORY_CACHE, key = "'colorOptions'")
    @Transactional(readOnly = true)
    fun getAllColorOptions() = optionDao.findAllColorOptions()
}