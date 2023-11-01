package ru.kpfu.itis.ponomarev.androidcourse.ui.holder

import androidx.recyclerview.widget.RecyclerView
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemGifDateBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.GifDateModel
import java.text.SimpleDateFormat

class GifDateViewHolder(
    private val binding: ItemGifDateBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(item: GifDateModel) {
        binding.tvDate.text = SimpleDateFormat.getDateInstance().format(item.date)
    }
}
