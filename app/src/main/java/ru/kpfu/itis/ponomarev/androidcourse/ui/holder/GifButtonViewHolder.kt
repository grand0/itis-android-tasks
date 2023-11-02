package ru.kpfu.itis.ponomarev.androidcourse.ui.holder

import androidx.recyclerview.widget.RecyclerView
import ru.kpfu.itis.ponomarev.androidcourse.databinding.ItemGifButtonBinding

class GifButtonViewHolder(
    binding: ItemGifButtonBinding,
    onClick: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.btnAdd.setOnClickListener {
            onClick()
        }
    }
}
