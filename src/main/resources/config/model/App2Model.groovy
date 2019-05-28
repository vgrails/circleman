package config.model

import com.circleman.domains.Organization
import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaDomainBuilder

class App2Model {
    boolean define() {
        MetaDomain metaTestDateBooleanChar = new MetaDomainBuilder().domain(name: "TestDateBooleanChar", locale: "杂项测试", pkg: "com.circleman.domains") {
            field name: 'date', nullable: false, type: "Date", locale: "D"
            field name: 'boolean1', nullable: false, type: "Boolean", locale: "B"
            field name: 'char1', min:  ('c' as char), type: "char", locale: "C"
        }

        return true
    }
}