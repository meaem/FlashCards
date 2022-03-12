package flashcards

class Card(val front: String, val back: String) {
    fun check(answer: String) = back == answer

    override fun toString(): String {
        return "Card:\n$front\nDefinition:\n$back"
    }
}

fun main() {
    println("Input the number of cards:")
    val cardList = List(readln().toInt()){
        println("Card #${it+1}:")
        val f = readln()
        println("The definition for card #${it+1}:")
        val b = readln()
        Card(f, b)
    }
//    println("Input (a term, then a definition, and, finally, an answer):")
   for (card in cardList) {
       println("Print the definition of \"${card.front}\":")
       println(if (card.check(readln())) "Correct!" else "Wrong. The right answer is \"${card.back}\".")
   }
}
