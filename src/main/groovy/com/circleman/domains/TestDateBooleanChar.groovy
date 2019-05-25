package com.circleman.domains

import grails.gorm.annotation.Entity
import groovy.transform.ToString
import org.grails.datastore.gorm.GormEntity

@ToString
@Entity
class TestDateBooleanChar implements GormEntity<TestDateBooleanChar>{
    Date date
    Boolean boolean1
    char char1

    static constraints = {
        date nullable: false
        boolean1 nullable: false
        char1 min: 'c' as char
    }
}
