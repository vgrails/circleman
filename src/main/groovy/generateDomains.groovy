import com.circleman.Bootstrap
import com.circleman.meta.MetaDomain
import static spark.Spark.*
import static com.circleman.Bootstrap.*

initConfig()
initMetaModels()

Bootstrap.env = DEVELOPMENT

println "Generating Domains"
new File().mkdirs()
metaDomainMap.each{ String name, MetaDomain domain ->
    println domain.toDomain()
}
stop()