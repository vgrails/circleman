package com.circleman

import com.circleman.core.MetaType
import static spark.Spark.*

class CirclemanIntegrationTest extends GroovyTestCase{

    void testInitialization(){
        Bootstrap.main()
        List<MetaMethod> methods = this.metaClass.methods

        int total=0
        int success=0
        int failure=0

        for(int i=0;i<methods.size();i++){
            if(methods[i].name.startsWith("测试")){

                total++

                try {
                    log.info(">>>> ${methods[i].name}")
                    new CirclemanIntegrationTest()."${methods[i].name}"()
                    log.info("<<<< 正常结束")
                    success++
                }catch(Exception e){
                    log.info("<<<< 异常结束")
                    failure++
                }
            }
        }
        stop()

        log.info("测试：${total}项 完成：${success} 异常：${failure}")
    }

    void 测试MetaField1(){

        MetaType metaType = "STRING" as MetaType
        assert metaType.toString() == "STRING"
        assert metaType.value == 3

        assert metaType.next().toString() == "DATE"
        assert metaType.previous().toString() == "CHAR"

        metaType = "FLOAT"

        assert  metaType.toString() == "FLOAT"

        assert  metaType.isNumberic() == true

        metaType = "ONE2ONE"
        assert metaType.isRelation() == true
        assert metaType.isNumberic() == false
    }

    void 测试MetaField2(){
        MetaType metaType = "STRING" as MetaType
        assert metaType.toString() == "STRING"
        assert metaType.value == 3

        assert metaType.next().toString() == "DATE"
        assert metaType.previous().toString() == "CHAR"

        metaType = "FLOAT"

        assert  metaType.toString() == "FLOAT"

        assert  metaType.isNumberic() == true

        metaType = "ONE2ONE"
        assert metaType.isRelation() == true
        assert metaType.isNumberic() == false
    }
}
