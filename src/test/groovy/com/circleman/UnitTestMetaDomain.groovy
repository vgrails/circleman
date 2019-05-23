package com.circleman

import com.circleman.meta.MetaDomain
import com.circleman.meta.MetaDomainBuilder
import com.circleman.util.ParallelRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat

class UnitTestMetaDomain {

    @BeforeEach
    void Init(){
    }

    @Test
    void 正常_基础功能MetaDomain(){
        MetaDomain metaOrganization = new MetaDomainBuilder().domain(name: "Organization", locale: "组织", pkg: "com.circleman") {
            field name: 'name', locale: "名称", type: "String"
            field name: 'description', locale: "简介", type:"String"
        }

        MetaDomain metaEmployee = new MetaDomainBuilder().domain(name: "Employee", locale: "员工", pkg: "com.circleman") {
            field name: 'name', locale: "姓名", type: "String"
            field name: 'gender', locale: "性别", type: "boolean"
            field name: 'age', locale: "年龄", type: "int"
            field name: 'birthday', locale: "生日", type: "Date", format: "yyyy-MM-dd"
        }

        println metaOrganization.toString()
        println metaEmployee.toString()

        assert metaOrganization.fields.size() == 3
        assert metaOrganization.fields[1].type == "String"
        assert metaOrganization.fields[2].type == "String"

        assert metaEmployee.fields.size() == 5
        assert metaEmployee.fields[1].type == "String"
        assert metaEmployee.fields[2].type == "boolean"
        assert metaEmployee.fields[3].type == "int"
        assert metaEmployee.fields[4].type == "Date"
        assert metaEmployee.validate() == true
    }

    @Test
    void 异常_异常约束和值(){
        MetaDomain metaOrganization = new MetaDomainBuilder().domain(name: "Organization", locale: "组织", pkg: "com.circleman") {
            field name: 'name', locale: "名称", type: "String"
            field name: 'name', locale: "简介", type:"String"
        }

        println metaOrganization.toString()

        assert metaOrganization.fields.size() == 2
    }

    @Test
    void 并发_使用MetaDomain(){
        ParallelRunner runner=new ParallelRunner()
        runner.Run(4,100000, {int threadId, operationId->

            String domain = "Employee${threadId}_${operationId}"

            MetaDomain metaEmployee = new MetaDomainBuilder().domain(name: domain, locale: "员工", pkg: "com.circleman") {
                field name: 'name', locale: "姓名", type: "String"
                field name: 'gender', locale: "性别", type: "boolean"
                field name: 'age', locale: "年龄", type: "int"
                field name: 'birthday', locale: "生日", type: "Date", format: "yyyy-MM-dd"
            }

            assert metaEmployee.fields.size() == 5
            assert metaEmployee.fields[1].type == "String"
            assert metaEmployee.fields[2].type == "boolean"
            assert metaEmployee.fields[3].type == "int"
            assert metaEmployee.fields[4].type == "Date"
        })

        println runner.toString()
        assert runner.operationPerSecond > 100
    }
}
