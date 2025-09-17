package project.stylo.web.dao

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import project.stylo.web.domain.ActionLog
import project.stylo.web.dto.request.ActionLogSearchRequest

class ActionLogRepositoryImpl(private val mongoTemplate: MongoTemplate) : ActionLogRepositoryCustom {

    override fun search(request: ActionLogSearchRequest, pageable: Pageable): Page<ActionLog> {
        val query = Query().with(pageable)
        val criteria = mutableListOf<Criteria>()

        request.startDate?.let {
            criteria.add(Criteria.where("timestamp").gte(it.atStartOfDay()))
        }
        request.endDate?.let {
            criteria.add(Criteria.where("timestamp").lt(it.plusDays(1).atStartOfDay()))
        }
        request.userKeyword?.let {
            criteria.add(Criteria.where("memberId").`is`(it))
        }
        request.eventType?.let {
            criteria.add(Criteria.where("eventType").`is`(it.name))
        }
        request.action?.let {
            criteria.add(Criteria.where("action").`is`(it.name))
        }
        if (!request.ip.isNullOrBlank()) {
            criteria.add(Criteria.where("ip").`is`(request.ip))
        }

        if (criteria.isNotEmpty()) {
            query.addCriteria(Criteria().andOperator(*criteria.toTypedArray()))
        }

        val logs = mongoTemplate.find(query, ActionLog::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ActionLog::class.java)

        return PageImpl(logs, pageable, total)
    }
}
