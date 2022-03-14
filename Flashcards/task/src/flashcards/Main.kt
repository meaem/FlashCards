package flashcards

import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt

val cardList = mutableListOf<Card>()
val menu = listOf("add", "remove", "import", "export", "ask", "exit", "log", "hardest card", "reset stats")
val log = mutableListOf<String>()

class Card(val front: String, var back: String, var numOfMistakes: Int = 0) {
    fun check(answer: String) = back == answer

    override fun toString(): String {
        return "Card:\n$front\nDefinition:\n$back"
    }
}

fun findDuplicateTerm(term: String, cardList: List<Card>): List<Card> {
    return cardList.filter { it.front == term }
}

fun findDuplicateDefinition(definition: String, cardList: List<Card>): List<Card> {
    return cardList.filter { it.back == definition }
}

fun printMenuAndChooseAction(): String {
    printlnAndLog("Input the action (${menu.joinToString(", ")}):")
    var action = readlnAndLog().lowercase()
    while (action !in menu) {
        printlnAndLog("Invalid action")
        printlnAndLog("Input the action (${menu.joinToString(",")}):")
        action = readlnAndLog().lowercase()
    }
    return action
}

fun readlnAndLog(): String {

    val s = readln()
    log.add(s)
    return s
}

fun printlnAndLog(s: String) {
    log.add(s)
    println(s)
}



fun resetStats() {
    cardList.forEach { it.numOfMistakes = 0 }
    printlnAndLog("Card statistics have been reset.")

}

fun printHardestCard() {
    val hardest = cardList.filter { it.numOfMistakes > 0 }
        .groupBy { it.numOfMistakes }.maxByOrNull { it.key }
    if (hardest != null) {
        val list = hardest.value
        if (list.isEmpty()) {
            printlnAndLog("There are no cards with errors")
        } else {
            if (list.size == 1) {
                printlnAndLog("The hardest card is \"${list.first().front}\". You have ${list.first().numOfMistakes} errors answering it.")
            } else {
                val cardsStr = list.map { "\"${it.front}\"" }.joinToString(",")
                val cardsMistakesSum = list.sumOf { it.numOfMistakes }
                printlnAndLog("The hardest cards are $cardsStr. You have $cardsMistakesSum errors answering them.")
            }
        }
    } else {
        printlnAndLog("There are no cards with errors")
    }
}

fun log() {
    printlnAndLog("File name:")
    val filename = readlnAndLog()
    val file = File(filename)
    file.writeText(log.joinToString("\n"))
    printlnAndLog("The log has been saved.")
}

fun ask() {
    printlnAndLog("How many times to ask?")
    val howMany = readlnAndLog().toInt()
    for (i in 1..howMany) {
        val idx = Random.nextInt(0 until cardList.size)
        val card = cardList[idx]
        printlnAndLog("Print the definition of \"${card.front}\":")
        val answer = readlnAndLog()
        if (card.check(answer)) printlnAndLog("Correct!")
        else {
            card.numOfMistakes++
            printlnAndLog("Wrong. The right answer is \"${card.back}\"" +
                    findCorrectTerm(answer, cardList)?.let {
                        return@let ", but your definition is correct for \"${it.front}\""
                    } + "."
            )
        }

    }
}

fun export(fName:String?=null) {
    val file : File = if(fName.isNullOrEmpty()) {
        printlnAndLog("File name:")
        val filename = readlnAndLog()
        File(filename)

    }else
    {
        File(fName)
    }
    file.writeText(cardList.map { "${it.front}\n${it.back}\n${it.numOfMistakes}" }.joinToString("\n"))
    printlnAndLog("${cardList.size} cards have been saved.")
}

fun import(fName:String?=null) {
    val file : File = if(fName.isNullOrEmpty()) {
        printlnAndLog("File name:")
        val filename = readlnAndLog()
        File(filename)

    }else
    {
        File(fName)
    }
    if (!file.exists()) {
        printlnAndLog("File not found.")
        return
    }
    val lines = file.readLines()
    if (lines.isNotEmpty()) {
        for (line in 0..lines.lastIndex step 3) {
            merge(lines[line], lines[line + 1], lines[line + 2].toInt())
        }
    }
    printlnAndLog("${lines.size / 3} cards have been loaded")
}

fun merge(card: String, definition: String, numOfMistakes: Int) {
    val previousCard = findDuplicateTerm(card, cardList).firstOrNull()
    if (previousCard != null) {
        previousCard.back = definition
        previousCard.numOfMistakes += numOfMistakes
    } else {
        cardList.add(Card(card, definition, numOfMistakes))
    }
}

fun remove() {
    printlnAndLog("Which card?")
    val term = readlnAndLog()
    if (removeCard(term)) {
        printlnAndLog("The card has been removed.")
    } else {
        printlnAndLog("Can't remove \"Wakanda\": there is no such card.")
    }
}

fun removeCard(term: String): Boolean {
    return cardList.removeIf { it.front == term }
}

fun add() {
    printlnAndLog("The card:")
    val term = readlnAndLog()
    if (findDuplicateTerm(term, cardList).isNotEmpty()) {
        printlnAndLog("The card \"$term\" already exists.")
        return
    }
    printlnAndLog("The definition of the card:")
    val definition = readlnAndLog()
    if (findDuplicateDefinition(definition, cardList).isNotEmpty()) {
        printlnAndLog("The definition \"$definition\" already exists.")
        return
    }
    cardList.add(Card(term, definition))
    printlnAndLog("The pair (\"$term\":\"$definition\") has been added.")
}

fun findCorrectTerm(answer: String, cardList: List<Card>): Card? {
    return cardList.firstOrNull { it.back == answer }
}
fun main(args :Array<String>) {
    var idx =args.indexOf("-import")
    if (idx!= -1){
        val fileName = args[idx+1]
        import(fileName)
    }

     idx =args.indexOf("-export")
    val exportOnExit = if (idx!= -1){
        args[idx+1]

    }else {
        null
    }

    while (true) {
        when (printMenuAndChooseAction()) {
            "add" -> add()
            "remove" -> remove()
            "import" -> import()
            "export" -> export()
            "ask" -> ask()
            "log" -> log()
            "hardest card" -> printHardestCard()
            "reset stats" -> resetStats()
            "exit" -> {
                if (exportOnExit != null){
                    export(exportOnExit)
                }
                printlnAndLog("Bye bye!")
                break
            }

        }
    }
}