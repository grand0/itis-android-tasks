package ru.kpfu.itis.ponomarev.androidcourse.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemAnswerBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.Answer
import ru.kpfu.itis.ponomarev.androidcourse.ui.holder.AnswerHolder

class AnswerAdapter(
    val answers: List<Answer>,
    private val onItemChecked: ((Int) -> Unit)? = null,
    private val onRootClicked: ((Int) -> Unit)? = null,
) : RecyclerView.Adapter<AnswerHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerHolder {
        return AnswerHolder(
            ItemAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemChecked,
            onRootClicked,
        )
    }

    override fun getItemCount(): Int = answers.size

    override fun onBindViewHolder(holder: AnswerHolder, position: Int) {
        holder.bindItem(answers[position])
    }
}
