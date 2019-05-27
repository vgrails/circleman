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

        println metaOrganization.validate()
        println metaEmployee.validate()


        MetaDomain metaTestNumberic = new MetaDomainBuilder().domain(name: "TestNumberic", locale: "数字测试", pkg: "com.circleman.domains") {
            field type: "byte", name: 'byte1', min: (byte) 0, locale: "比特"
            field type: "short", name: 'short1', min: (short) 0,  locale: "短整"
            field type: "int", name: 'int1', min:  0,  locale: "整数"
            field type: "Integer", name: 'int2', nullable:false,  locale: "整对"
            field type: "long", name: 'long1', min: 0l,  locale: "长整"
            field type: "Long", name: 'long2', nullable:false,  locale: "长对"
            field type: "float", name: 'float1', min: 0f,  locale: "浮点"
            field type: "Float", name: 'float2', nullable:false,  locale: "浮对"
            field type: "double", name: 'double1', min:0d,  locale: "长浮"
            field type: "Double", name: 'double2', nullable:false,  locale: "长浮对"
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
            field name: 'date', nullable: false, type: "Date", locale: "D"
            field name: 'boolean1', nullable: false, type: "Boolean", locale: "B"
            field name: 'char1', min:  ('c' as char), type: "char", locale: "C"
        }

        return true
    }
}