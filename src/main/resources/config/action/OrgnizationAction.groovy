package config.action

import com.circleman.domains.Organization
import spark.Request
import spark.Response
import static com.circleman.Bootstrap.*
import static com.circleman.util.RouteUtil.*

class OrgnizationAction {
    boolean define() {

        GET "organization/count", { Request request, Response response, Map params ->


            return json([code: 200, msg: "Hello World!"])
        }

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

        log.info "服务注册很成功!"

        return true
    }
}