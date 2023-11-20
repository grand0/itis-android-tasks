package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentMainBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.AirplaneModeNotifier
import ru.kpfu.itis.ponomarev.androidcourse.util.CreateNotificationSettings
import ru.kpfu.itis.ponomarev.androidcourse.util.NotificationSettings
import ru.kpfu.itis.ponomarev.androidcourse.util.NotificationsUtil
import ru.kpfu.itis.ponomarev.androidcourse.util.setIcon
import ru.kpfu.itis.ponomarev.androidcourse.util.setOnSurfaceTint

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var airplaneModeCallbackId: Int? = null

    private var showedBigTextWarning = false

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
            etTitle.setText(CreateNotificationSettings.title)
            etTitle.addTextChangedListener {
                CreateNotificationSettings.title = it?.toString() ?: ""
            }
            etContents.setText(CreateNotificationSettings.contents)
            etContents.addTextChangedListener {
                val text = it?.toString() ?: ""
                CreateNotificationSettings.contents = text
                if ((text.length >= NotificationsUtil.NOTIFICATION_BIG_TEXT_THRESHOLD || text.lines().size > 2) && !NotificationSettings.isBigText && !showedBigTextWarning) {
                    showedBigTextWarning = true

                    val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.baseline_lightbulb_24)
                    icon?.setOnSurfaceTint(requireContext())

                    Snackbar.make(root, getString(R.string.text_getting_long_message), Snackbar.LENGTH_SHORT)
                        .setIcon(icon)
                        .show()
                } else if (!(text.length >= NotificationsUtil.NOTIFICATION_BIG_TEXT_THRESHOLD || text.lines().size > 2)) {
                    showedBigTextWarning = false
                }
            }

            showNotifBtn.setOnClickListener {
                NotificationsUtil.sendNotification(requireContext())

                var avd = AppCompatResources.getDrawable(requireContext(), R.drawable.avd_notifications_checked) as AnimatedVectorDrawable
                showNotifBtn.icon = avd
                avd.start()
                Handler(Looper.getMainLooper()).postDelayed({
                    context?.let {
                        avd = AppCompatResources.getDrawable(it, R.drawable.avd_notifications_unchecked) as AnimatedVectorDrawable
                        showNotifBtn.icon = avd
                        avd.start()
                    }
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
