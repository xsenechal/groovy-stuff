import org.apache.commons.lang.StringUtils


class MyLoadedClass {

    def run(){
        println "MyLoadedClass.run() with import use from jar"
        println StringUtils.trim("  rgrg  ")
    }
}

