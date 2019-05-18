package com.circleman.core

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import static com.circleman.core.BaseApp.*
import static com.circleman.core.BaseApp.clazzMap
import static com.circleman.core.BaseApp.metaDomainMap

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(ignoreNulls = true)
class OrmQuery {
    /** 模型 */
    String domain
    /** 分页 */
    int max
    /** 跳过记录 */
    int offset
    /** 查询 */
    MetaSearch search
    /** 排序 */
    MetaOrder order

    private boolean checkValid(){
        if(domain == null || clazzMap[domain]==null || metaDomainMap[domain]==null){
            log.debug("搜索的Domain无效")
            return false
        }

        if(max > 1000 || max <=0 || offset <0){
            log.debug("搜索的分页参数无效")
            return false
        }

        MetaDomain metaDomain = metaDomainMap[domain]


        if(search != null) {
            if (metaDomain.GetMetaField(search.field) == null) {
                log.debug("搜索的字段无效")
                return false
            }

            if (search.CheckOp()) {
                log.debug("搜索的OP无效")
                return false
            }
        }


        if(order!=null){
            if(metaDomain.GetMetaField(order.field)==null){
                log.debug("排序字段无效")
                return false
            }

            if(order.CheckDirection()!=true){
                log.debug("排序参数无效")
                return false
            }
        }

        return true
    }

    //"from Organization where name like '研发1%'"
    String toHql(){
        if(checkValid()==false){
            return null
        }

        String hql="from ${domain}"
        if(search) {
            hql = "${hql} where ${search.field} ${search.GetOpMap()}"

            MetaDomain metaDomain = metaDomainMap[domain]

            MetaType type = metaDomain.GetMetaField(search.field).metaType

            if (type.isNumberic()) {
                hql = "${hql} ${search.param1}"
            } else if (type.toString() == "STRING") {
                if (search.op == 'like') {
                    hql = "${hql} \"%${search.param1}%\""
                } else {
                    hql = "${hql} \"${search.param1}\""
                }
            }
        }

        return hql
    }

    String toCountHql(){

        String hql="select count(*) from ${domain}"

        if(search) {
            hql = "${hql} where ${search.field} ${search.GetOpMap()}"

            MetaDomain metaDomain = metaDomainMap[domain]

            MetaType type = metaDomain.GetMetaField(search.field).metaType

            if (type.isNumberic()) {
                hql = "${hql} ${search.param1}"
            } else if (type.toString() == "STRING") {
                if (search.op == 'like') {
                    hql = "${hql} \"%${search.param1}%\""
                } else {
                    hql = "${hql} \"${search.param1}\""
                }
            }
        }

        return hql
    }
}

