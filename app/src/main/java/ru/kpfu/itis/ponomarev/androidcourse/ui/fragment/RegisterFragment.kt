package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentRegisterBinding
import ru.kpfu.itis.ponomarev.androidcourse.model.UserModel
import ru.kpfu.itis.ponomarev.androidcourse.service.UserService
import ru.kpfu.itis.ponomarev.androidcourse.util.Validators
import ru.kpfu.itis.ponomarev.androidcourse.util.clearErrorOnType
import ru.kpfu.itis.ponomarev.androidcourse.util.setupPhoneInput

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        with (binding) {
            tilName.clearErrorOnType()
            tilPhone.clearErrorOnType()
            tilEmail.clearErrorOnType()
            tilPassword.clearErrorOnType()

            etPhone.setupPhoneInput()

            btnRegister.setOnClickListener {
                val name = etName.text
                val phone = etPhone.text
                val email = etEmail.text
                val password = etPassword.text
                var valid = true
                if (name.isNullOrEmpty()) {
                    tilName.error = getString(R.string.required_error_text)
                    valid = false
                }
                if (phone.isNullOrEmpty()) {
                    tilPhone.error = getString(R.string.required_error_text)
                    valid = false
                }
                if (!Validators.validatePhone(phone.toString())) {
                    tilPhone.error = getString(R.string.invalid_phone_number_text)
                    valid = false
                }
                if (email.isNullOrEmpty()) {
                    tilEmail.error = getString(R.string.required_error_text)
                    valid = false
                }
                if (!Validators.validateEmail(email.toString())) {
                    tilEmail.error = getString(R.string.invalid_email_address_text)
                    valid = false
                }
                if (password.isNullOrEmpty()) {
                    tilPassword.error = getString(R.string.required_error_text)
                    valid = false
                }
                if (valid) {
                    setEnabledForAll(false)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val phoneNumberNormalized = Validators.normalizePhone(phone.toString())
                        var allUnique = true
                        if (!UserService.checkEmailUnique(email.toString())) {
                            requireActivity().runOnUiThread {
                                tilEmail.error = getString(R.string.unique_error_text)
                            }
                            allUnique = false
                        }
                        if (!UserService.checkPhoneUnique(phoneNumberNormalized)) {
                            requireActivity().runOnUiThread {
                                tilPhone.error = getString(R.string.unique_error_text)
                            }
                            allUnique = false
                        }
                        if (allUnique) {
                            UserService.saveUser(
                                user = UserModel(
                                    name = name.toString(),
                                    phone = phoneNumberNormalized,
                                    email = email.toString(),
                                ),
                                password = password.toString(),
                            )
                            parentFragmentManager.beginTransaction()
                                .replace(
                                    R.id.fragment_container,
                                    LoginFragment()
                                )
                                .commit()
                        } else {
                            requireActivity().runOnUiThread {
                                setEnabledForAll(true)
                            }
                        }
                    }
                }
            }
            btnToLogin.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        LoginFragment(),
                    )
                    .commit()
            }
        }
    }

    private fun setEnabledForAll(enabled: Boolean) {
        with (binding) {
            tilName.isEnabled = enabled
            tilEmail.isEnabled = enabled
            tilPhone.isEnabled = enabled
            tilPassword.isEnabled = enabled
            btnRegister.isEnabled = enabled
            btnToLogin.isEnabled = enabled
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
