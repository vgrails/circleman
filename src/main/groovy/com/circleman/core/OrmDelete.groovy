package com.circleman.core

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

import static com.circleman.core.BaseApp.*

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(ignoreNulls = true)
class OrmDelete {
    /** 模型 */
    String domain
    /** ID */
    long id

    private boolean checkValid(){
        if(domain == null || clazzMap[domain]==null || metaDomainMap[domain]==null){
            log.debug("更新的Domain无效")
            return false
        }

        if(id<=0){
            log.debug("更新的ID无效")
            return false
        }

        return true
    }

    //"update Person set age = :newAge where firstName = :firstNameToSearch and lastName = :lastNameToSearch",
    //    [newAge:15, firstNameToSearch:'John', lastNameToSearch:'Doe'])
    String toHql(){
        if(checkValid()==false){
            return null
        }

        String hql="delete from ${domain} where id=${id}"
        return hql
    }
}

