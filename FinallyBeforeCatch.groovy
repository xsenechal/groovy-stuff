/**
 * This is troll
 * Yes, finally can be executed BEFORE the catch block
 * It is actually true only true when catch only contains a single 'return' statement
 * Indeed, finally is always executed Before the method returns
 */
class Test{
    public String myfunction(){
        try{
            throw new RuntimeException('toto')
        }
        catch (Throwable t){
            return 'catch'
        }
        finally{
            return 'finally'
        }
    }
}

println 'Which one is returned... Answer: ' + new Test().myfunction()
