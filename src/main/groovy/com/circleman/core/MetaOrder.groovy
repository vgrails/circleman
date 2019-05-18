package com.circleman.core

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(ignoreNulls = true)
class MetaOrder {
    String field
    String direction="asc"

    boolean CheckDirection(){
        if(direction in ["asc", "desc"]){
            return true
        }

        return false
    }
}