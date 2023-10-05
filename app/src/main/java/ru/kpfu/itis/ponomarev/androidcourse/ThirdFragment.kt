package ru.kpfu.itis.ponomarev.androidcourse

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentThirdBinding

class ThirdFragment : Fragment(R.layout.fragment_third) {
    private var _binding: FragmentThirdBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val message = arguments?.getString(ParamsKey.MESSAGE_TEXT_KEY)
        if (message.isNullOrEmpty()) {
            binding.tvText.apply {
                text = context?.getString(R.string.third_fragment_empty_text)
                setTypeface(null, Typeface.ITALIC)
            }
        } else {
            binding.tvText.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val THIRD_FRAGMENT_TAG = "THIRD_FRAGMENT_TAG"

        fun newInstance(message: String?): ThirdFragment = ThirdFragment().apply {
            arguments = bundleOf(ParamsKey.MESSAGE_TEXT_KEY to message)
        }
    }
}
