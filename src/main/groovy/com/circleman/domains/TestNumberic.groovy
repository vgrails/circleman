package com.circleman.domains

import grails.gorm.annotation.Entity
import groovy.transform.ToString
import org.grails.datastore.gorm.GormEntity

@ToString
@Entity
class TestNumberic implements GormEntity<TestNumberic>{
    byte byte1
    short short1
    int int1
    Integer int2
    long long1
    Long long2
    float float1
    Float float2
    double double1
    Double double2

    static constraints = {
        byte1 min: (byte)0
        short1 min: (short)0
        int1 min: 0
        int2 nullable:false
        long1 min:0l

        long2 nullable:false
        float1 min:0.0f
        float2 nullable:false
        double1 min:0.00d
        double2 nullable:false
    }
}
