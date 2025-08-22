package project.stylo.web.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import project.stylo.web.dao.CategoryDao
import project.stylo.web.dao.OptionDao

@Service
@Transactional
class CategoryService(
    private val optionDao: OptionDao,
    private val categoryDao: CategoryDao
) {
    @Cacheable("categoryCache", key = "'categories'")
    @Transactional(readOnly = true)
    fun getAllCategories() = categoryDao.findAll()

    @Cacheable("categoryCache", key = "'genderOptions'")
    @Transactional(readOnly = true)
    fun getAllGenderOptions() = optionDao.findAllGenderOptions()

    @Cacheable("categoryCache", key = "'sizeOptions'")
    @Transactional(readOnly = true)
    fun getAllSizeOptions() = optionDao.findAllSizeOptions()

    @Cacheable("categoryCache", key = "'colorOptions'")
    @Transactional(readOnly = true)
    fun getAllColorOptions() = optionDao.findAllColorOptions()
}