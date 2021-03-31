package personal.jweust.anagramsgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
import com.squareup.moshi.*
import okio.BufferedSource
import okio.Okio
import java.lang.Exception


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var wordsList: MutableList<Word>
    lateinit var userEntryLabel: TextView
    var userEntryString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userEntryLabel = findViewById(R.id.userEntryLabel)

        var letterSet = getLetterSequence()
        var buttonsList = gatherButtons()

        buttonsList.forEach {
            it.setOnClickListener(this)
        }

        //Set each button to be a letter
        //TODO: Add scramble button to scramble letter/button assignments
        assignLettersToButtons(letterSet, buttonsList)
        //Parse through json to generate a list of all Words
        wordsList = buildDictionary()
    }

    override fun onClick(v: View) {
        var clickedButton = v as Button

        clickedButton.isClickable = false
        userEntryString += clickedButton.text
        userEntryLabel.text = userEntryString.toUpperCase()
        println("Button clicked. UserEntryString: ${userEntryString}. Clicked button text: ${clickedButton.text}")
    }

    private fun gatherButtons(): List<Button> {
        var buttons = mutableListOf<Button>()
        var buttonsTableLayout = findViewById<TableLayout>(R.id.buttonsTableLayout)

        try {
            for (child in buttonsTableLayout.children) {
                if ((child as TableRow) != null) {
                    for (rowChild in child.children) {
                        if ((rowChild as Button) != null) {
                            buttons.add(rowChild)
                        }
                    }
                }
            }
        } catch (e: Exception) {

        }

        return buttons.toList()
    }

    private fun generateLetters(): String {
        var letters: String = ""

        for (i in 1..9) {
            letters += alphabet.random()
        }

        return letters
    }

    private fun getLetterSequence(): String {
        var letters = generateLetters()
        var needsVowels = true

        //Check to make sure string has at least one vowel
        //Will add future conditions for string, to make better letter combinations
        while (needsVowels) {
            letters = generateLetters()
            var hasVowels = letters.findAnyOf(vowels)
            if (hasVowels != null) {
                needsVowels = false
            }
        }

        return letters
    }

    private fun assignLettersToButtons(letters: String, buttons: List<Button>) {
        for (i in letters.indices) {
            buttons[i].text = letters[i].toString()
        }
    }

    private fun buildDictionary(): MutableList<Word> {
        //Parse dictionary.json using Moshi
        //Add each <Word, Definition> pair to words list as Word type
        var moshi = Moshi.Builder().build()
        var adapter: JsonAdapter<Word> = moshi.adapter(Word::class.java)
        var words = mutableListOf<Word>()
        val source = getBufferedSourceFromResource()

        var reader = JsonReader.of(source)

        reader.beginObject()
        while (reader.hasNext()) {
            var word: String = ""
            var definition: String = ""

            while (reader.hasNext()) {
                word = reader.nextName()
                definition = reader.nextString()

                if (word == "" || definition == "") {
                    throw JsonDataException("Missing required field")
                }

                val newWord = Word(word, definition)
                words.add(newWord)
            }
        }
        reader.endObject()

        return words
        println("Dictionary completed, contains ${words.count()} words.")
    }

    private fun getBufferedSourceFromResource(): BufferedSource {
        val source: BufferedSource?

        source = Okio.buffer(Okio.source(resources.openRawResource(R.raw.dictionary)))

        return source
    }

    fun generateWordsFromLetters(letters: String): List<String> {
        var wordList: List<String> = listOf()
        //loop through letters and generate all possible combinations
        //check each combination to see if it is in the dictionary
        //if so, add to wordList

        return wordList
    }

    companion object {
        const val alphabet = "abcdefghijklmnopqrstuvwxyz"
        val vowels = listOf("a", "e", "i", "o", "u")
    }
}

@JsonClass(generateAdapter = true)
data class Word(
        val word: String,
        val definition: String
)