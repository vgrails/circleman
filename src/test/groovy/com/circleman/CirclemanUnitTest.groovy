package com.circleman

import com.circleman.core.MetaType
import com.circleman.util.EnvConfig
import org.junit.*

class CirclemanUnitTest extends GroovyTestCase{

    void testMetaType(){
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

    void testMetaField(){
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

    void testLoadConfig(){
        MetaType metaType = "STRING" as MetaType

        EnvConfig.LoadConfig()

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
