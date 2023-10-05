package ru.kpfu.itis.ponomarev.androidcourse

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentMainBinding
import java.util.logging.Logger

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        val orientation = resources.configuration.orientation
        Logger.getLogger(this.javaClass.name).info("orientation: $orientation")
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.fcvRightPane.visibility = View.GONE
        } else {
            binding.fcvRightPane.visibility = View.VISIBLE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFragments()
    }

    private fun initFragments() {
        if (childFragmentManager.findFragmentByTag(FirstFragment.FIRST_FRAGMENT_TAG) == null) {
            childFragmentManager.beginTransaction()
                .add(binding.fcvLeftPane.id, FirstFragment(), FirstFragment.FIRST_FRAGMENT_TAG)
                .add(binding.fcvRightPane.id, FourthFragment(), FourthFragment.FOURTH_FRAGMENT_TAG)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG"
    }
}
