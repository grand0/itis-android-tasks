package ru.kpfu.itis.ponomarev.androidcourse

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentSecondBinding

class SecondFragment : Fragment(R.layout.fragment_second) {
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
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
                text = context?.getString(R.string.second_fragment_empty_text)
                setTypeface(null, Typeface.ITALIC)
            }
        } else {
            binding.tvText.text = message
        }

        binding.btnGoTo1.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnGoTo3.setOnClickListener {
            parentFragmentManager.apply {
                popBackStack()

                beginTransaction()
                    .add(
                        R.id.fragment_container,
                        ThirdFragment.newInstance(message),
                        ThirdFragment.THIRD_FRAGMENT_TAG
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val SECOND_FRAGMENT_TAG = "SECOND_FRAGMENT_TAG"

        fun newInstance(message: String?): SecondFragment = SecondFragment().apply {
            arguments = bundleOf(ParamsKey.MESSAGE_TEXT_KEY to message)
        }
    }
}
