package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import ru.kpfu.itis.ponomarev.androidcourse.MainActivity
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentCoroutinesBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.AirplaneModeNotifier

class CoroutinesFragment : Fragment() {

    private var _binding: FragmentCoroutinesBinding? = null
    private val binding get() = _binding!!

    private var airplaneModeCallbackId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoroutinesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        binding.apply {
            sbCoroutinesCount.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    tvCoroutinesCount.text = progress.toString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) { }

                override fun onStopTrackingTouch(seekBar: SeekBar?) { }
            })

            tvCoroutinesCount.text = sbCoroutinesCount.progress.toString()

            btnExecute.setOnClickListener {
                val async = cbAsync.checkedState == MaterialCheckBox.STATE_CHECKED
                val stopOnBackground = cbStopOnBackground.checkedState == MaterialCheckBox.STATE_CHECKED
                (requireActivity() as MainActivity).startCoroutines(
                    sbCoroutinesCount.progress,
                    async,
                    stopOnBackground,
                )
            }

            airplaneModeCallbackId = AirplaneModeNotifier.registerCallback(requireContext()) { isOn ->
                btnExecute.isEnabled = !isOn
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        airplaneModeCallbackId?.let {
            AirplaneModeNotifier.unregisterCallback(it)
        }
        super.onDestroyView()
    }
}
