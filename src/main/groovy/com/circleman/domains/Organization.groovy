package com.circleman.domains

import grails.gorm.annotation.Entity
import groovy.transform.ToString
import org.grails.datastore.gorm.GormEntity

@ToString
@Entity
class Organization implements GormEntity<Organization>{
    String name
    String description

    static constraints = {
        name blank: false, size: 2..20
        description blank: true, size:0..256
    }
}
