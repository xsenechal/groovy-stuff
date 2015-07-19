import org.apache.commons.lang.StringUtils


class MyLoadedClass {

    def run(str){
        println "MyLoadedClass.run() with import use from jar"
        return StringUtils.trim(str)
    }
}

