package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.adapter.AnswerAdapter
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentQuestionBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.Question
import ru.kpfu.itis.ponomarev.androidcourse.util.QuestionAnsweredListener
import ru.kpfu.itis.ponomarev.androidcourse.util.ParamsKey

class QuestionFragment : Fragment(R.layout.fragment_question) {
    private var _viewBinding: FragmentQuestionBinding? = null
    private val viewBinding: FragmentQuestionBinding
        get() = _viewBinding!!

    private var answerAdapter: AnswerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentQuestionBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // using deprecated method to cover more android versions
        val question = requireArguments().getSerializable(ParamsKey.QUESTION_KEY) as Question

        with(viewBinding) {
            tvQuestion.text = question.question
            answerAdapter = AnswerAdapter(
                answers = question.answersList.toMutableList(),
                onItemChecked = { position ->
                    updateChecked(position)
                },
                onRootClicked = { position ->
                    updateChecked(position)
                }
            )
            rvAnswers.adapter = answerAdapter
            val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            rvAnswers.addItemDecoration(decoration)
        }
    }

    private fun updateChecked(position: Int) {
        answerAdapter?.answers?.let {
            it.forEach { answer ->
                answer.checked = false
            }
            it[position].checked = true
            answerAdapter?.notifyDataSetChanged()
            (requireActivity().supportFragmentManager.findFragmentByTag(SurveyFragment.SURVEY_FRAGMENT_TAG) as QuestionAnsweredListener)
                .questionAnswered(
                    questionPosition = requireArguments().getInt(ParamsKey.QUESTION_POSITION_KEY),
                    answerPosition = position
                )
        }
    }

    override fun onDestroy() {
        _viewBinding = null
        super.onDestroy()
    }

    companion object {
        const val QUESTION_FRAGMENT_TAG = "QUESTION_FRAGMENT_TAG"

        fun newInstance(questionPosition: Int, question: Question): QuestionFragment {
            val fragment = QuestionFragment()
            val args = Bundle()
            args.putInt(ParamsKey.QUESTION_POSITION_KEY, questionPosition)
            args.putSerializable(ParamsKey.QUESTION_KEY, question)
            fragment.arguments = args
            return fragment
        }
    }
}
