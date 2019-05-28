package config.action

import com.circleman.domains.Organization
import spark.Request
import spark.Response
import static com.circleman.Bootstrap.*

class DataInit {
    boolean init() {

        Organization.withTransaction {
            for(int i=0;i<100;i++){
                new Organization(name: "组织${i+1}", description: "组织的描述${i+1}").save(true)
            }
        }

        log.info "数据已然初始化>>>>"

        return true
    }
}