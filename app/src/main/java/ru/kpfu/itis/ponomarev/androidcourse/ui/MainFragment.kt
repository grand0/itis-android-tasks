package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentMainBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.AirplaneModeNotifier
import ru.kpfu.itis.ponomarev.androidcourse.util.NotificationsUtil

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var airplaneModeCallbackId: Int? = null

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
        binding.apply {
            showNotifBtn.setOnClickListener {
                NotificationsUtil.sendNotification(
                    context = requireContext(),
                    title = etTitle.text.toString(),
                    text = etContents.text.toString(),
                )

                var avd = AppCompatResources.getDrawable(requireContext(), R.drawable.avd_notifications_checked) as AnimatedVectorDrawable
                showNotifBtn.icon = avd
                avd.start()
                Handler(Looper.getMainLooper()).postDelayed({
                    avd = AppCompatResources.getDrawable(requireContext(), R.drawable.avd_notifications_unchecked) as AnimatedVectorDrawable
                    showNotifBtn.icon = avd
                    avd.start()
                }, 1000)
            }

            airplaneModeCallbackId = AirplaneModeNotifier.registerCallback(requireContext()) { isOn ->
                showNotifBtn.isEnabled = !isOn
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
