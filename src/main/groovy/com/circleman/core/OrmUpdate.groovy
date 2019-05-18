package com.circleman.core

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import static com.circleman.core.BaseApp.*

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(ignoreNulls = true)
class OrmUpdate {
    /** 模型 */
    String domain
    /** ID */
    long id

    Map<String, Object> attributes = [:]


    private boolean checkValid(){
        if(domain == null || clazzMap[domain]==null || metaDomainMap[domain]==null){
            log.debug("更新的Domain无效")
            return false
        }

        if(id<=0){
            log.debug("更新的ID无效")
            return false
        }

        MetaDomain metaDomain = metaDomainMap[domain]

        attributes.each{String field, Object value->
            if(metaDomain.GetMetaField(field)==null){
                log.debug("字段无效")
                return false
            }
        }

        return true
    }

    //"update Person set age = :newAge where firstName = :firstNameToSearch and lastName = :lastNameToSearch",
    //    [newAge:15, firstNameToSearch:'John', lastNameToSearch:'Doe'])
    String toHql(){
        if(checkValid()==false){
            return null
        }

        MetaDomain metaDomain = metaDomainMap[domain]
        String hql="update from ${domain} set"

        attributes.eachWithIndex{ String field, Object value, int index ->
            MetaType type = metaDomain.GetMetaField(field).metaType

            hql = "${hql} ${field} = :${field}"

            if(index < attributes.size()-1) hql = "${hql},"
        }

        hql = "${hql} where id=${id}"

        return hql
    }
}

