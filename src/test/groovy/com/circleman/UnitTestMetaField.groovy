package com.circleman


import com.circleman.meta.MetaField
import com.circleman.util.ParallelRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.text.SimpleDateFormat

class UnitTestMetaField {

    @BeforeEach
    void Init(){
    }

    @Test
    void 正常_基础功能String(){
        MetaField metaField = new MetaField(name: "name", locale: "名称")
        metaField.with {
            type("String")
            nullable(true)
            inList(["武大郎", "武松", "西门大官人", "潘金莲"])
            min(2)
            max(6)
            mask(false)
            unique(true)
            matches(~/^(\\d+)[\u4E00-\u9FA5]{3}$/)
            email(false)
            mobile(false)
            blank(false)
        }

        assert metaField.validate() == true
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null
    }

    @Test
    void 正常_基础功能Int(){
        MetaField metaField = new MetaField(name: "age", locale: "年龄")
        metaField.with {
            type("int")
            min(1)
            max(6)
            unique(true)
        }

        assert metaField.validate() == true
        assert metaField.numberic == true
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null

        metaField.with {
            type("Integer")
            nullable(false)
            min(1)
            max(6)
            unique(true)
        }

        assert metaField.validate() == true
        assert metaField.numberic == true
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null
    }

    @Test
    void 正常_基础功能Float(){
        MetaField metaField = new MetaField(name: "price", locale: "房价")
        metaField.with {
            type("float")
            min(1.0f)
            max(6.0f)
            decimalSize(2)
        }

        assert metaField.validate() == true
        assert metaField.numberic == true
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null

        metaField.with {
            type("Float")
            nullable(false)
            min(1.0f)
            max(6.0f)
            decimalSize(2)
        }

        assert metaField.validate() == true
        assert metaField.numberic == true
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null
    }

    @Test
    void 正常_基础功能Boolean(){
        MetaField metaField = new MetaField(name: "billionare", locale: "富一代")
        metaField.with {
            type("boolean")
        }

        assert metaField.validate() == true
        assert metaField.numberic == false
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null

        metaField.with {
            type("Boolean")
            nullable(true)
        }

        assert metaField.validate() == true
        assert metaField.numberic == false
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null
    }

    @Test
    void 正常_基础功能Date(){
        MetaField metaField = new MetaField(name: "birthday", locale: "生日")
        metaField.with {
            type("Date")
            format("yyyy-MM-dd HH:mm:ss")
            min(new SimpleDateFormat("yyyy-MM-dd").parse("1970-01-01"))
            max(new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01"))
            nullable(true)
            unique(false)
        }

        assert metaField.validate() == true
        assert metaField.numberic == false
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null
    }

    @Test
    void 异常_异常约束和值(){
        MetaField metaField = new MetaField(name: "name", locale: "名称")
        metaField.with {
            type("String")
            flex(100)
            nullable(true)
            inList([1, 2])
            min(7)
            max(6)
            mask(false)
            unique(true)
            matches(~/^(\\d+)[\u4E00-\u9FA5]{3}$/)
            email(false)
            mobile(false)
            blank(false)
            decimalSize(2)
            format("yyyy")
        }

        println metaField.toString()
        println metaField.toField()
        println metaField.toConstraint()

        assert metaField.validate() == false
        assert metaField.toString() != null
        assert metaField.toField() != null
        assert metaField.toConstraint() != null
    }

    @Test
    void 异常_空转换(){
        println new MetaField().toString()
        println new MetaField().toField()
        println new MetaField().toConstraint()
    }

    @Test
    void 并发_使用MetaField(){
        ParallelRunner runner=new ParallelRunner()
        runner.Run(4,100000, {int threadId, operationId->
            MetaField metaField = new MetaField(name: "name", locale: "名称")
            metaField.with {
                type("String")
                nullable(true)
                inList(["武大郎", "武松", "西门大官人", "潘金莲"])
                min(2)
                max(6)
                mask(false)
                unique(true)
                matches(~/^(\\d+)[\u4E00-\u9FA5]{3}$/)
                email(false)
                mobile(false)
                blank(false)
            }

            assert metaField.validate() == true
            assert metaField.toString() != null
            assert metaField.toField() != null
            assert metaField.toConstraint() != null
        })

        println runner.toString()
        assert runner.operationPerSecond > 100
    }
}
