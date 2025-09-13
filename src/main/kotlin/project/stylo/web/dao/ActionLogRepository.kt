package project.stylo.web.dao

import org.springframework.data.mongodb.repository.MongoRepository
import project.stylo.web.domain.ActionLog

interface ActionLogRepository : MongoRepository<ActionLog, String>