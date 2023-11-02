package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentMainBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.GifRepository

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        with (binding) {
            etGifsCount.addTextChangedListener {
                tilGifsCount.error = (if (!validateGifsCount()) getString(R.string.gifs_count_et_error) else null)
            }
            btnShow.setOnClickListener {
                if (validateGifsCount()) {
                    GifRepository.clear()

                    parentFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            FeedFragment.newInstance(etGifsCount.text.toString().toInt()),
                            FeedFragment.FEED_FRAGMENT_TAG
                        )
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun validateGifsCount() : Boolean {
        return (binding.etGifsCount.text.toString().toIntOrNull() ?: -1) in 0..45
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
