package ru.kpfu.itis.ponomarev.androidcourse.ui.holder

import androidx.recyclerview.widget.RecyclerView
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemAnswerBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.Answer

class AnswerHolder(
    private val viewBinding: ItemAnswerBinding,
    private val onItemChecked: ((Int) -> Unit)? = null,
    private val onRootClicked: ((Int) -> Unit)? = null,
) : RecyclerView.ViewHolder(viewBinding.root) {

    init {
        // setOnCheckedChangeListener is a trap. using setOnClickListener to only listen to user
        //   changing state, otherwise it will invoke onItemChecked any time it wants, even when
        //   fragment is not there and it randomly decided that it's time to change some states.
        //   hours of confusion and debugging went here...
        viewBinding.rbAnswer.setOnClickListener {
            onItemChecked?.invoke(adapterPosition)
        }
        viewBinding.root.setOnClickListener {
            onRootClicked?.invoke(adapterPosition)
        }
    }

    fun bindItem(answer: Answer) {
        with(viewBinding) {
            tvAnswer.text = answer.answer.toString()
            rbAnswer.isChecked = answer.checked
            rbAnswer.isEnabled = !answer.checked
            if (answer.checked) {
                root.foreground = null
            }
        }
    }
}
