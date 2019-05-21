package com.circleman.core

import com.circleman.meta.MetaDomain
import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import static com.circleman.core.BaseApp.*

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(ignoreNulls = true)
class OrmCreate {
    /** 模型 */
    String domain
    Map<String, Object> attributes = [:]

    private boolean checkValid(){
        if(domain == null || clazzMap[domain]==null || metaDomainMap[domain]==null){
            log.debug("创建的Domain无效")
            return false
        }

        MetaDomain metaDomain = metaDomainMap[domain]

        attributes.each{String field, Object value->
            if(metaDomain.getMetaField(field)==null){
                log.debug("字段无效")
                return false
            }
        }

        return true
    }
}

