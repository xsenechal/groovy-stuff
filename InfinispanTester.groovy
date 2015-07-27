import org.infinispan.manager.DefaultCacheManager
import org.infinispan.Cache
import org.infinispan.notifications.Listener
import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent
import java.lang.management.*
import java.util.concurrent.TimeUnit

/*
 * groovy -Djava.net.preferIPv4Stack=true InfispanTester.groovy
 */
@Grapes(@Grab('org.infinispan:infinispan-embedded:7.1.0.Final'))

@Listener (clustered = true)
class CacheListener {
    @CacheEntryCreated
    public void entryCreated(CacheEntryCreatedEvent e) {
        if (!e.isOriginLocal()) {
            println "-- Entry (${e.key}, ${e.value} created by another node in the cluster"
        }
    }
}

class MySpan{

    void main(){
        def pid = ManagementFactory.getRuntimeMXBean().getName()
        def configStr =
"""<?xml version="1.0" encoding="UTF-8"?>
<infinispan xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xmlns="urn:infinispan:config:7.1"
            xsi:schemaLocation="urn:infinispan:config:7.1 http://www.infinispan.org/schemas/infinispan-config-7.1.xsd">
    <jgroups/>

    <cache-container default-cache="mycache">
         <transport/>
         <replicated-cache name="mycache" mode="SYNC" />
         <!--<distributed-cache name="mycache" owners="1" mode="SYNC" />-->
    </cache-container>
</infinispan>"""

        DefaultCacheManager m = new DefaultCacheManager(new ByteArrayInputStream(configStr.getBytes()));
        Cache<String, String> cache = m.getCache();
        cache.addListener(new CacheListener())

        def welcome =
"""
Infinispan tester.
Run this script serveral times on the same device/network and do some testing.
Useful to test different configuration. (dist, repl, sync, async, expiration, numOwner...)

Commands are:
   put k v [ttl]
   get k
   rm k
   ls
   quit
This script is a more generic fork of Ticket booking system:
http://www.mastertheboss.com/jboss-frameworks/infinispan/infinispan-tutorial-part-2?showall=&start=1

Configs:
http://infinispan.org/docs/7.2.x/getting_started/getting_started.html
"""
        println welcome
        while (true){
            println '> '
            def input = new BufferedReader(new InputStreamReader(System.in)).readLine()
            def args = input.split(' ')
            def cmd = args[0]
            switch (cmd){
                case 'put':
                        def key     = args[1]
                        def value   = args[2]
                        def ttl     = args.size() == 4 ? args[3] : null
                        if(ttl) cache.put(key, value + " - $pid", ttl as int, TimeUnit.SECONDS);
                        else    cache.put(key, value + " - $pid");
                        println("Put: "+input);
                    break
                case 'get':
                        def key     = args[1]
                        println "Entry:($key, ${cache.get(key)})"
                    break
                case 'rm':
                        def key     = args[1]
                        println("Removed: "+cache.remove(key));
                    break
                case 'ls':
                        Set <String> keys = cache.keySet();
                        for (String key: keys) {
                            println "Entry:($key, ${cache.get(key)})"
                        }
                    break
                case 'quit':
                        m.stop();
                        System.exit(0)
                    break
                default:
                    println "Unknown command: $cmd"
            }

        }

    }
}
new MySpan().main()
