@file:Suppress("unused")

import java.io.File
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URI
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText



/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/${name}.txt").readText().trim()


/**
 * downloads the input from the website if needed
 */
fun loadAndReadInput(day: Int, year: Int): String {
    val name = "day${day.toString().padStart(2, '0')}"
    val inputFileName = "src/${name}_input.txt"
    val inputFile = File(inputFileName)
    if (!inputFile.exists()) {

        val sessionFile = File("src/session.txt")
        require(sessionFile.exists()) { "Session token file not found at $sessionFile" }
        val sessionToken = sessionFile.readText().trim()

        val uri = URI("https://adventofcode.com/$year/day/$day/input")
        val url = uri.toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Cookie", "session=$sessionToken")
        connection.setRequestProperty(
            "User-Agent",
            "Andrew's input grabber in Kotlin, kacheek30@gmail.com"
        )
        try {
            connection.connect()
            check(connection.responseCode == 200) { "${connection.responseCode} ${connection.responseMessage}" }
            connection.inputStream.use { input ->
                inputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
                inputFile.setReadOnly()
            }
        } finally {
            connection.disconnect()
        }

        //here, we prepare the code file for the next day as well
        if (day<25){
            val nextDay = day+1
            val nextDayPadded = nextDay.toString().padStart(2, '0')
            val nextDayFile = File("src/day${nextDayPadded}.kt")
            if (!nextDayFile.exists()) {
                val templateFile = File("src/day00.template")
                val template = templateFile.readText()
                    .replace("%%DAY%%", nextDay.toString())
                    .replace("%%0DAY%%", nextDayPadded)
                nextDayFile.writeText(template)
            }

        }
    }
    return readInput("${name}_input")
}



/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
