class MyGse {
    def run(String rootScript){
        def PACKAGE_ROOT_JARS   = "jars"

        def urlroots = new URL[1]
        urlroots[0] = new File(rootScript).toURL();
        GroovyScriptEngine gse = new GroovyScriptEngine(urlroots);

        new File("$rootScript/$PACKAGE_ROOT_JARS").eachFile { println "adding ${it.name} to classpath"; gse.groovyClassLoader.addURL(it.toURL()) }

        Class clazz = gse.loadScriptByName("LoadedScript.groovy");
        def o =  clazz.newInstance()
        println o.run("    sdkjhbzsfbhf    ")
    }
}

new MyGse().run("loader")





