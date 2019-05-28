package config.model

import com.circleman.domains.Organization
import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaDomainBuilder

class App1Model {
    boolean define() {

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

        return true
    }
}