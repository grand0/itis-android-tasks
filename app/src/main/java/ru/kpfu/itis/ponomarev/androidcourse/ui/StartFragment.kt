package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentStartBinding
import kotlin.math.min

class StartFragment : Fragment(R.layout.fragment_start) {

    private var _viewBinding: FragmentStartBinding? = null
    private val viewBinding: FragmentStartBinding
        get() = _viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentStartBinding.inflate(inflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        with(viewBinding) {
            etPhone.apply {
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus && etPhone.text.isNullOrEmpty()) {
                        etPhone.setText("79")
                    } else if (!hasFocus && !validatePhoneNumber()) {
                        etPhone.error = context.getString(R.string.invalid_phone_number_error)
                    }
                }
                addTextChangedListener(object : TextWatcher {
                    var needToRemoveDigit = false

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        needToRemoveDigit = (after == 0 && s?.substring(start, start + count)
                            ?.matches(Regex("\\D")) ?: false)
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (phoneNeedsFormatting(s.toString())) {
                            s?.replace(0, s.length, formatPhone(s.toString(), needToRemoveDigit))
                        }
                        validateAll()
                    }

                    private fun phoneNeedsFormatting(phone: String): Boolean {
                        val regex =
                            Regex("^\\+7 \\(9(\\d(\\d\\)-(\\d(\\d(\\d-(\\d(\\d-(\\d{0,2})?)?)?)?)?)?)?)?\$")
                        return !phone.matches(regex)
                    }

                    private fun formatPhone(phone: String, needToRemoveDigit: Boolean): String {
                        // +7 (9##)-###-##-##
                        var phoneDigits = phone.replace(Regex("\\D"), "")
                        if (needToRemoveDigit && phoneDigits.isNotEmpty()) {
                            phoneDigits = phoneDigits.substring(0, phoneDigits.length - 1)
                        }
                        val sb = StringBuilder()
                        if (phoneDigits.isEmpty()) {
                            return ""
                        }
                        sb.append("+7 (9")
                        if (phoneDigits.length < 3) {
                            return sb.toString()
                        } else if (phoneDigits.length == 3) {
                            sb.append(phoneDigits[2])
                            return sb.toString()
                        } else {
                            sb.append(phoneDigits.substring(2, 4)).append(")-")
                        }
                        if (phoneDigits.length == 4) {
                            return sb.toString()
                        } else {
                            sb.append(phoneDigits.substring(4, min(7, phoneDigits.length)))
                        }
                        if (phoneDigits.length < 7) {
                            return sb.toString()
                        } else {
                            sb.append("-")
                        }
                        if (phoneDigits.length == 7) {
                            return sb.toString()
                        } else {
                            sb.append(phoneDigits.substring(7, min(9, phoneDigits.length)))
                        }
                        if (phoneDigits.length < 9) {
                            return sb.toString()
                        } else {
                            sb.append("-")
                        }
                        if (phoneDigits.length == 9) {
                            return sb.toString()
                        } else {
                            sb.append(phoneDigits.substring(9, min(11, phoneDigits.length)))
                        }
                        return sb.toString()
                    }
                })
            }
            etQuestionsNumber.addTextChangedListener {
                if (!validateNumberOfQuestions()) {
                    etQuestionsNumber.error = getString(R.string.invalid_number_of_questions_error)
                }
                validateAll()
            }
            btnStart.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        SurveyFragment.newInstance(etQuestionsNumber.text.toString().toInt()),
                        SurveyFragment.SURVEY_FRAGMENT_TAG,
                    )
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun validateAll(): Boolean {
        viewBinding.btnStart.isEnabled = validatePhoneNumber() && validateNumberOfQuestions()
        return viewBinding.btnStart.isEnabled
    }

    private fun validatePhoneNumber(): Boolean {
        return viewBinding.etPhone.text.matches(Regex("^\\+7 \\(9\\d{2}\\)-\\d{3}(-\\d{2}){2}\$"))
    }

    private fun validateNumberOfQuestions(): Boolean {
        return (viewBinding.etQuestionsNumber.text.toString().toIntOrNull() ?: 0) in 1..1000
    }

    override fun onDestroyView() {
        _viewBinding = null
        super.onDestroyView()
    }
}
