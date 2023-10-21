package ru.kpfu.itis.ponomarev.androidcourse.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.kpfu.itis.ponomarev.androidcourse.model.Question
import ru.kpfu.itis.ponomarev.androidcourse.ui.QuestionFragment

class SurveyFragmentAdapter(
    private val questionsList: List<Question>,
    manager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(manager, lifecycle) {

    // + 2 for 2 fake pages (for infinite scrolling)
    override fun getItemCount(): Int = questionsList.size + 2

    override fun createFragment(position: Int): Fragment {
        // decide which question we should actually display
        val realPosition = if (position == 0) questionsList.size - 1 else (position - 1) % questionsList.size
        return QuestionFragment.newInstance(realPosition, questionsList[realPosition])
    }
}
