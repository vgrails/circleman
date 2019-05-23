import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaDomainBuilder

class MetaModel {
    static boolean initModel() {
        MetaDomain metaOrganization = new MetaDomainBuilder().domain(name: "Organization", locale: "组织", pkg: "com.circleman") {
            field name: 'name', locale: "名称"
            field name: 'description', locale: "简介"
        }

        MetaDomain metaEmployee = new MetaDomainBuilder().domain(name: "Employee", locale: "员工", pkg: "com.circleman") {
            field name: 'name', locale: "姓名"
            field name: 'description', locale: "简介"
        }

//        MetaDomain metaEmployee = new MetaDomainBuilder().domain(name: "Employee", locale: "员工") {
//            field name: 'name', locale: "姓名"
//            field name: 'description', locale: "简介"
//        }
//
//        MetaDomain metaArea = new MetaDomainBuilder().domain(name: "Area", locale: "区域") {
//            field name: 'name', locale: "名称"
//            field name: 'description', locale: "简介"
//            field name: 'parent', locale: "上级区域"
//        }

        metaOrganization.getMetaField('name')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)
            .decimalSize(3)

        println metaOrganization.getMetaField('name').validate()
        println metaOrganization.getMetaField('name').toField()
        println metaOrganization.getMetaField('name').toConstraint()

        metaOrganization.getMetaField('description')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)
        .nullable(true)
        .unique(true)

        println metaOrganization.getMetaField('description').validate()
        println metaOrganization.getMetaField('description').toField()
        println metaOrganization.getMetaField('description').toConstraint()


        metaEmployee.getMetaField('name')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)
            .decimalSize(3)

        println metaEmployee.getMetaField('name').validate()

        metaEmployee.getMetaField('description')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)

        println metaEmployee.getMetaField('description').validate()

        println "Initialization Successful!"
        return true
    }


//metaOrganization.getMetaField('description')
//    .metaType(MetaType.STRING)
//    .min(0)
//    .max(64)
//    .nullable(true)
//
//metaEmployee.getMetaField('name')
//    .metaType(MetaType.STRING)
//    .min(2)
//    .max(32)
//    .blank(false)
//metaEmployee.getMetaField('description')
//    .metaType(MetaType.STRING)
//    .min(0)
//    .max(64)
//    .nullable(true)
//
//metaArea.getMetaField('name')
//    .metaType(MetaType.STRING)
//    .min(2)
//    .max(32)
//    .blank(false)
//
//metaArea.getMetaField('description')
//    .metaType(MetaType.STRING)
//    .min(0)
//    .max(64)
//    .nullable(true)
//
//println metaDomainMap['Employee']
//println "current env: ${env}"
//
//        println ""
//
//    }

}