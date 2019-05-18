package com.circleman.core

import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy,prefix = "")
@ToString(ignoreNulls = true)
class MetaSearch{
    static Map<String, String> opMap=[
        lt:"<",
        le:"<=",
        eq:"=",
        ge:">=",
        gt:">",
        ne:"!=",
        like:"like"
    ]

    String field
    String op

    Object param1
    Object param2

    boolean CheckOp(){
        if(opMap[op]!=null){
            return true
        }

        return false
    }

    String GetOpMap(){
        return opMap[op]
    }
}