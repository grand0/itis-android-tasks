package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import ru.kpfu.itis.ponomarev.androidcourse.R
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
                R.layout.list_item,
                Importance.values()
            ).also {
                actvImportance.setAdapter(it)
                actvImportance.setText(NotificationSettings.importance.text, false)
                actvImportance.onItemClickListener =
                    OnItemClickListener { parent, _, position, _ ->
                        NotificationSettings.importance =
                            parent.getItemAtPosition(position) as Importance
                    }
            }

            ArrayAdapter(
                requireContext(),
                R.layout.list_item,
                Visibility.values()
            ).also {
                actvVisibility.setAdapter(it)
                actvVisibility.setText(NotificationSettings.visibility.text, false)
                actvVisibility.onItemClickListener =
                    OnItemClickListener { parent, _, position, _ ->
                        NotificationSettings.visibility =
                            parent.getItemAtPosition(position) as Visibility
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
