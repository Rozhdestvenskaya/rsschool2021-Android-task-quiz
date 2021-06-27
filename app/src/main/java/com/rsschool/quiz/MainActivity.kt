package com.rsschool.quiz

import android.content.Intent
import android.os.Bundle
import android.util.SparseIntArray
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity(), Navigator, AppInterraptor, ShareResult, OnAnswerCheckedListener {
    //читаем из gson
    private val questions by lazy {
        val questionsText = resources.openRawResource(R.raw.questions) .bufferedReader().use { it.readText() }
        val questionsType = object : TypeToken<List<Question>>() {}.type
        Gson().fromJson<List<Question>>(questionsText, questionsType)
    }
    private val answers = SparseIntArray()
    private var currentQuestion = 0
    private val theme = arrayListOf(R.style.Theme_Quiz_First, R.style.Theme_Quiz_Second,R.style.Theme_Quiz_Third,
        R.style.Theme_Quiz_Fourth, R.style.Theme_Quiz_Fifth, R.style.Theme_Quiz_Sixth, R.style.Theme_Quiz_Seventh)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openQuizFragment(currentQuestion)
    }

    private fun openQuizFragment(index: Int) {
        val quizFragment = QuizFragment.createInstance(
            questions[index],
            index,
            questions.last() == questions[index],
            answers.get(index, -1),
            theme[currentQuestion % theme.size]
        )
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, quizFragment)
            .addToBackStack(index.toString())
            .commit()
    }

    private fun openResultScreen(){
        val resultFragment = ResultFragment.createInstance(questions.size, calculateCorrectAnswers())
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, resultFragment)
            .commit()
    }

    private fun buildReport() : String {
        var report = getString(R.string.result, calculateCorrectAnswers(), questions.size)
        for(i in questions.indices) {
            report += "\n\n${i +  1}) ${questions[i].question}\nYour answer - ${questions[i].options[answers[i]]}"
        }
        return report
    }

    override fun moveNext() {
        if(currentQuestion < questions.size - 1) {
            currentQuestion++
            openQuizFragment(currentQuestion)
        }
        else openResultScreen()
    }

    override fun movePrev() {
        onBackPressed()
    }

    override fun moveStart() {
        currentQuestion = 0
        answers.clear()
        openQuizFragment(currentQuestion)
    }

    override fun exit() {
        finish()
    }

    override fun share() {
        val pm = packageManager
        val result = buildReport()
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, result)
        if (intent.resolveActivity(pm) != null) startActivity(intent)
    }

    override fun onAnswerChecked(index: Int, answer: Int) {
        answers.put(index, answer)
    }

    override fun onBackPressed() {
        if (currentQuestion == 0) {
            exit()
            return
        }
        if (currentQuestion > 0) currentQuestion--
        super.onBackPressed()
    }

    private fun calculateCorrectAnswers(): Int{
        var count = 0
        for (i in questions.indices){
            if (questions[i].correct == answers[i]) count++
        }
        return count
    }

}