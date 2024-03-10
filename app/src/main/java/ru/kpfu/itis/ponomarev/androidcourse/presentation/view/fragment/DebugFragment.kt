package ru.kpfu.itis.ponomarev.androidcourse.presentation.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentDebugBinding
import ru.kpfu.itis.ponomarev.androidcourse.di.qualifier.DebugInfoMap
import ru.kpfu.itis.ponomarev.androidcourse.util.toPx
import javax.inject.Inject

@AndroidEntryPoint
class DebugFragment : Fragment() {

    private var _binding: FragmentDebugBinding? = null
    private val binding get() = _binding!!

    @DebugInfoMap
    @Inject
    lateinit var infoMap: Map<String, String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebugBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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