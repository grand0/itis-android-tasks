package ru.kpfu.itis.ponomarev.androidcourse.presentation.view.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import ru.kpfu.itis.ponomarev.androidcourse.BuildConfig
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentDebugBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.toPx

class DebugFragment : Fragment() {

    private var _binding: FragmentDebugBinding? = null
    private val binding get() = _binding!!

    private val infoMap = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebugBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        infoMap["app_name"] = getString(R.string.app_name)
        infoMap["base_url"] = BuildConfig.OPENWEATHER_API_BASE_URL
        infoMap["version_name"] = BuildConfig.VERSION_NAME
        infoMap["version_code"] = BuildConfig.VERSION_CODE.toString()
        infoMap["device"] = "${Build.MANUFACTURER} ${Build.MODEL}"
        infoMap["os"] = String.format("Android %s (API %d)", Build.VERSION.RELEASE, Build.VERSION.SDK_INT)

        init()
    }

    private fun init() {
        val textPadding = requireContext().toPx(8)
        with (binding) {
            infoMap.forEach { (key, value) ->
                val row = TableRow(requireContext())
                row.layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                val keyView = TextView(requireContext())
                    .apply {
                        text = key
                        setPadding(
                            textPadding,
                            0,
                            textPadding,
                            0
                        )
                    }
                val valueView = TextView(requireContext())
                    .apply {
                        text = value
                        setPadding(
                            textPadding,
                            0,
                            textPadding,
                            0
                        )
                    }
                row.addView(keyView)
                row.addView(valueView)
                tableLayout.addView(row)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val DEBUG_FRAGMENT_TAG = "DEBUG_FRAGMENT_TAG"
    }
}