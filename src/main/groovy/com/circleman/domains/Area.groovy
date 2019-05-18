package com.circleman.domains

import grails.gorm.annotation.Entity
import org.grails.datastore.gorm.GormEntity

@Entity
class Area implements GormEntity<Area>{
    String name
    String description

    Area parent
    static constraints = {
        name blank: false, size: 3..20
        description blank: true, size:0..256
        parent nullable: true
    }
}
