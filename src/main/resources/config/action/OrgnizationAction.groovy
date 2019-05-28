package config.action

import com.circleman.domains.Organization
import spark.Request
import spark.Response
import static com.circleman.Bootstrap.*

class OrgnizationAction {
    boolean define() {

        GET "organization", { Request request, Response response, Map params ->
            Map output = [:]

            Organization.withTransaction {
                output['total_count'] = Organization.count
                output['pos'] = params['offset']
                output['data'] = Organization.findAll([max:params['max'], offset: params['offset']])
            }

            return json(output)
        }

        GET "organization/:id", { Request request, Response response, Map params ->

            println "organization/:id ${params}"

            def instance

            Organization.withTransaction {
                instance = Organization.get(params['id'] as Long)
            }

            return json(instance)
        }

        return true
    }
}