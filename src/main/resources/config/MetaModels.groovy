import com.circleman.domains.TestDateBooleanChar
import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaDomainBuilder
import org.grails.datastore.gorm.GormEntity

class MetaModel {
    boolean initModel() {
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
            .decimalSize(3)

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
            .decimalSize(3)

        metaEmployee.getMetaField('description')
            .type("String")
            .min(2)
            .max(32)
            .blank(false)

        println metaEmployee.getMetaField('description').validate()


        MetaDomain metaTestNumberic = new MetaDomainBuilder().domain(name: "TestNumberic", locale: "数字测试", pkg: "com.circleman.domains") {
            field name: 'byte1', min: (byte) 0
            field name: 'short1', min: (short) 0
            field name: 'int1', min:  0
            field name: 'int2', nullable:false
            field name: 'long1', min: 0l
            field name: 'long2', nullable:false
            field name: 'float1', min: 0f
            field name: 'float2', nullable:false
            field name: 'double1', min:0d
            field name: 'double2', nullable:false
        }

        println "Initialization Successful!"


//        class TestDateBooleanChar implements GormEntity<TestDateBooleanChar> {
//            Date date
//            Boolean boolean1
//            char char1
//
//            static constraints = {
//                date nullable: false
//                boolean1 nullable: false
//                char1 min: 'c' as char
//            }
//        }

        MetaDomain metaTestDateBooleanChar = new MetaDomainBuilder().domain(name: "TestDateBooleanChar", locale: "杂项测试", pkg: "com.circleman.domains") {
            field name: 'date', nullable: false
            field name: 'boolean1', nullable: false
            field name: 'char1', min:  'c' as char
        }

        return true
    }
}