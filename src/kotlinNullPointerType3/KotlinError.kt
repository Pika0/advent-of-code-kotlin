
package kotlinNullPointerType3

//OK turns out there are 4 known ways to get a NullPointerException according to
// https://kotlinlang.org/docs/null-safety.html
// and this is the 3rd way

interface ChainingUpdate {
    val usedBy: MutableSet<ChainingUpdate> //not a nullable variable
    fun addUser(newUser: ChainingUpdate) {
        usedBy.add(newUser) //4 start1.usedBy.add(mid1)
        newUser.update() //5 mid1.update()
    }
    fun update(){
        //6  mid1.usedBy does not exist yet because middle is still being created
        //    Exception in thread "main" java.lang.NullPointerException:
        //    Cannot invoke "java.lang.Iterable.iterator()" because "$this$forEach$iv" is null
        usedBy.forEach { it.update() }
    }
}

class DataStart(): ChainingUpdate{
    override val usedBy: MutableSet<ChainingUpdate> = mutableSetOf()
}

data class DataMiddle(val in1:ChainingUpdate): ChainingUpdate {
    init {
        in1.addUser(this) //3 - call start1.addUser(mid1)
    }
    override val usedBy: MutableSet<ChainingUpdate> = mutableSetOf() //nullability error if this line is after 'init{..}'
}


fun main() {
    val start1 = DataStart() //1 start1 is created, finished, and ready
    val mid1 = DataMiddle(start1) //2 start making mid1
}