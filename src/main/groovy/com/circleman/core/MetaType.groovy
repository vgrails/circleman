package com.circleman.core

enum MetaType {

    //基础类型
    BOOLEAN(1),
    CHAR(2),
    STRING(3),
    DATE(4),

    //数字类型
    BYTE(11),
    SHORT(12),
    INTEGER(13),
    LONG(14),
    FLOAT(15),
    DOUBLE(16),
    BIGINTEGER(17),
    BIGDECIMAL(18),

    //关系类型
    ONE2ONE(101),
    ONE2MANY(102),
    MANY2MANY(103)

    private int value

    MetaType(int v){
        value = v
    }

    MetaType(String s){
        try{
            value = (s.toUpperCase() as MetaType).value
        }catch(Exception e){}
    }

    int getValue(){
        value
    }

    boolean isNumberic(){
        if( value >= BYTE.value && value <= BIGDECIMAL.value){
            return true
        }

        return false
    }

    boolean isRelation(){
        if( value >= ONE2ONE.value && value <= MANY2MANY.value){
            return true
        }

        return false
    }
}