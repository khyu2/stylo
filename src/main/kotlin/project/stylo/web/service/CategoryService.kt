package project.stylo.web.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.common.config.CacheConfig.Companion.CATEGORY_CACHE
import project.stylo.web.dao.CategoryDao

@Service
@Transactional
class CategoryService(
    private val categoryDao: CategoryDao
) {
    @Cacheable(CATEGORY_CACHE, key = "'categories'")
    @Transactional(readOnly = true)
    fun getAllCategories() = categoryDao.findAll()
}