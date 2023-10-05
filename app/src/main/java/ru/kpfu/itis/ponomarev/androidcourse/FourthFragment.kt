package ru.kpfu.itis.ponomarev.androidcourse

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentFourthBinding
import java.lang.IllegalArgumentException

class FourthFragment : Fragment(R.layout.fragment_fourth) {

    private var _binding: FragmentFourthBinding? = null
    private val binding get() = _binding!!

    private val messagesViewModel: MessagesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFourthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        messagesViewModel.messages.observe(viewLifecycleOwner) { list ->
            updateMessages(list)
        }

        if (messagesViewModel.messages.value != null) {
            updateMessages(messagesViewModel.messages.value!!)
        }
    }

    private fun updateMessages(messages: List<String?>) {
        binding.apply {
            val textViews = listOf(tvText1, tvText2, tvText3)

            if (messages.size < textViews.size) {
                throw IllegalArgumentException("List must have at least ${textViews.size} elements.")
            }

            for (i in textViews.indices) {
                if (messages[i] == null) {
                    textViews[i].apply {
                        text = context.getString(R.string.empty_text)
                        setTypeface(null, Typeface.ITALIC)
                    }
                } else {
                    textViews[i].apply {
                        text = messages[i]
                        setTypeface(null, Typeface.NORMAL)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val FOURTH_FRAGMENT_TAG = "FOURTH_FRAGMENT_TAG"
    }
}
