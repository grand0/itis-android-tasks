package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentNotificationSettingsBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.Importance
import ru.kpfu.itis.ponomarev.androidcourse.util.NotificationSettings
import ru.kpfu.itis.ponomarev.androidcourse.util.Visibility

class NotificationSettingsFragment : Fragment() {

    private var _binding: FragmentNotificationSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        binding.apply {
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Importance.values()
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spImportance.adapter = it
                spImportance.setSelection(NotificationSettings.importance.ordinal)
                spImportance.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val selected = parent.getItemAtPosition(position) as Importance
                        if (selected != NotificationSettings.importance) {
                            NotificationSettings.importance = selected
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        val selected = Importance.HIGH
                        if (selected != NotificationSettings.importance) {
                            NotificationSettings.importance = selected
                            parent.setSelection(selected.ordinal)
                        }
                    }
                }
            }

            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                Visibility.values()
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spVisibility.adapter = it
                spVisibility.setSelection(NotificationSettings.visibility.ordinal)
                spVisibility.onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        NotificationSettings.visibility =
                            parent.getItemAtPosition(position) as Visibility
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        NotificationSettings.visibility = Visibility.PUBLIC
                        parent.setSelection(NotificationSettings.visibility.ordinal)
                    }
                }
            }

            cbBigText.checkedState = if (NotificationSettings.isBigText)
                    MaterialCheckBox.STATE_CHECKED
                else
                    MaterialCheckBox.STATE_UNCHECKED
            cbBigText.addOnCheckedStateChangedListener { _, state ->
                NotificationSettings.isBigText = state == MaterialCheckBox.STATE_CHECKED
            }

            cbActions.checkedState = if (NotificationSettings.showActions)
                    MaterialCheckBox.STATE_CHECKED
                else
                    MaterialCheckBox.STATE_UNCHECKED
            cbActions.addOnCheckedStateChangedListener { _, state ->
                NotificationSettings.showActions = state == MaterialCheckBox.STATE_CHECKED
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
