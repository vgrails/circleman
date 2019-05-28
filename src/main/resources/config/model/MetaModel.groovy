package config.model

import com.circleman.domains.Organization
import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaDomainBuilder

class MetaModel {
    boolean define() {
        MetaDomain metaOrganization = new MetaDomainBuilder().domain(name: "Organization", locale: "组织", pkg: "com.circleman") {
            field name: 'name', locale: "名称"
            field name: 'description', locale: "简介"
        }

        MetaDomain metaEmployee = new MetaDomainBuilder().domain(name: "Employee", locale: "员工", pkg: "com.circleman") {
            field name: 'name', locale: "姓名"
            field name: 'description', locale: "简介"
        }


        metaOrganization.getMetaField('name')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)

        metaOrganization.getMetaField('description')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)
            .nullable(true)
            .unique(true)

        metaEmployee.getMetaField('name')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)

        metaEmployee.getMetaField('description')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)

        return true
    }
}