package com.circleman.domains

import grails.gorm.annotation.Entity
import org.grails.datastore.gorm.GormEntity

@Entity
class Employee implements GormEntity<Employee>{
    String name
    String description

    static constraints = {
        name blank: false, size: 3..20
        description blank: true, size:0..256
    }
}
