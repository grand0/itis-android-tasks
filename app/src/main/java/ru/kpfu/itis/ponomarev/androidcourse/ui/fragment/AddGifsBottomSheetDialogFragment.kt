package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.DialogAddGifsBinding

class AddGifsBottomSheetDialogFragment(
    private val onAddClicked: (Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogAddGifsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddGifsBinding.inflate(inflater)
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
                    onAddClicked(etGifsCount.text.toString().toInt())
                    dismiss()
                }
            }
        }
    }

    private fun validateGifsCount() : Boolean {
        return (binding.etGifsCount.text.toString().toIntOrNull() ?: -1) in 0..5
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ADD_GIFS_BOTTOM_SHEET_DIALOG_FRAGMENT_TAG = "ADD_GIFS_BOTTOM_SHEET_DIALOG_FRAGMENT_TAG"
    }
}
