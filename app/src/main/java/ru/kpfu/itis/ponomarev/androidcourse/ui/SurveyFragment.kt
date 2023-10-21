package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.snackbar.Snackbar
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.adapter.SurveyFragmentAdapter
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentSurveyBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.QuestionAnsweredListener
import ru.kpfu.itis.ponomarev.androidcourse.util.ParamsKey
import ru.kpfu.itis.ponomarev.androidcourse.util.QuestionGenerator

class SurveyFragment : Fragment(R.layout.fragment_survey), QuestionAnsweredListener {
    private var _viewBinding: FragmentSurveyBinding? = null
    private val viewBinding: FragmentSurveyBinding
        get() = _viewBinding!!

    private var answers: HashMap<Int, Int> = hashMapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentSurveyBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val questionsNumber = requireArguments().getInt(ParamsKey.NUMBER_OF_QUESTIONS_KEY)
        val questionList = QuestionGenerator.generateList(questionsNumber)
        val vpAdapter = SurveyFragmentAdapter(questionList, childFragmentManager, lifecycle)
        viewBinding.viewPager.apply {
            adapter = vpAdapter
            registerOnPageChangeCallback(object: OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val realPos = when (position) {
                        0 -> questionsNumber
                        questionsNumber + 1 -> 1
                        else -> position
                    }
                    viewBinding.tvCurrentPage.text = "$realPos/$questionsNumber"
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    // for infinite scrolling
                    if (position == questionsNumber + 1 && positionOffset == 0.0f) {
                        setCurrentItem(1, false)
                    } else if (position == 0 && positionOffset == 0.0f) {
                        setCurrentItem(questionsNumber, false)
                    }
                }
            })
            setCurrentItem(1, false)
        }
        viewBinding.btnFinish.setOnClickListener {
            // there is no requirement to check if answers are actually correct :)
            // but i prepared a map of given answers just in case
            Snackbar.make(view, R.string.test_finished_msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun questionAnswered(questionPosition: Int, answerPosition: Int) {
        answers[questionPosition] = answerPosition
        val numberOfQuestions = requireArguments().getInt(ParamsKey.NUMBER_OF_QUESTIONS_KEY)
        if (answers.count() == numberOfQuestions) {
            viewBinding.btnFinish.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        _viewBinding = null
        super.onDestroy()
    }

    companion object {
        const val SURVEY_FRAGMENT_TAG = "SURVEY_FRAGMENT_TAG"
        fun newInstance(questionsNumber: Int) = SurveyFragment().apply {
            arguments = bundleOf(ParamsKey.NUMBER_OF_QUESTIONS_KEY to questionsNumber)
        }
    }
}
