package ru.kpfu.itis.ponomarev.androidcourse.util

import ru.kpfu.itis.ponomarev.androidcourse.model.Answer
import ru.kpfu.itis.ponomarev.androidcourse.model.Question
import java.util.Random

object QuestionGenerator {
    private val rng = Random()

    fun generateList(questionsNumber: Int): List<Question> {
        val list = mutableListOf<Question>()
        for (i in 0 until questionsNumber) {
            list.add(generate())
        }
        return list
    }

    fun generate(): Question {
        val firstOperand = rng.nextInt(100)
        val secondOperand = rng.nextInt(100)
        val operator: String
        val answer: Int
        if (rng.nextBoolean()) {
            operator = "+"
            answer = firstOperand + secondOperand
        } else {
            operator = "-"
            answer = firstOperand - secondOperand
        }
        val answersList = ArrayList<Answer>()
        val answersListSize = rng.nextInt(6) + 5
        val answerIndex = rng.nextInt(answersListSize)
        for (i in 0 until answersListSize) {
            if (i == answerIndex) {
                answersList.add(Answer(answer))
            } else {
                while (true) {
                    var delta = rng.nextInt(30) + 1
                    if (rng.nextBoolean()) delta = -delta
                    val fakeAnswer = Answer(answer + delta)
                    if (!answersList.contains(fakeAnswer)) {
                        answersList.add(fakeAnswer)
                        break
                    }
                }
            }
        }

        return Question(
            "$firstOperand $operator $secondOperand",
            answersList,
            answerIndex
        )
    }
}
