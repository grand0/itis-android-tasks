package ru.kpfu.itis.ponomarev.androidcourse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentFirstBinding

class FirstFragment : Fragment(R.layout.fragment_first) {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        with(binding) {
            btnSubmit.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(
                        R.id.fragment_container,
                        SecondFragment.newInstance(binding.etText.text.toString()),
                        SecondFragment.SECOND_FRAGMENT_TAG
                    )
                    .addToBackStack(null)
                    .commit()
                parentFragmentManager.beginTransaction()
                    .add(
                        R.id.fragment_container,
                        ThirdFragment.newInstance(binding.etText.text.toString()),
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
}
