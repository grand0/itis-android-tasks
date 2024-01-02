package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.config.AppConfig
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentProfileBinding
import ru.kpfu.itis.ponomarev.androidcourse.service.UserService
import ru.kpfu.itis.ponomarev.androidcourse.session.AppSession
import ru.kpfu.itis.ponomarev.androidcourse.util.Validators
import ru.kpfu.itis.ponomarev.androidcourse.util.clearErrorOnType
import ru.kpfu.itis.ponomarev.androidcourse.util.setupPhoneInput

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        val user = AppSession.authorizedUser
        if (user != null) {
            with (binding) {
                tilPhone.clearErrorOnType()
                tilCurrentPassword.clearErrorOnType()
                tilNewPassword.clearErrorOnType()

                tvName.text = user.name
                tvEmail.text = user.email

                tilPhone.isEndIconVisible = false
                tilPhone.setEndIconOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val newPhone = etPhone.text
                        if (!newPhone.isNullOrEmpty()) {
                            val normalizedPhone = Validators.normalizePhone(newPhone.toString())
                            if (UserService.checkPhoneUnique(normalizedPhone)) {
                                UserService.updatePhone(user, normalizedPhone)
                                requireActivity().runOnUiThread {
                                    tilPhone.isEndIconVisible = false
                                    Snackbar.make(
                                        root,
                                        getString(R.string.phone_changed_text),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                requireActivity().runOnUiThread {
                                    tilPhone.error = getString(R.string.unique_error_text)
                                }
                            }
                        } else {
                            requireActivity().runOnUiThread {
                                tilPhone.error = getString(R.string.required_error_text)
                            }
                        }
                    }
                }
                etPhone.setupPhoneInput()
                etPhone.setText(user.phone)
                etPhone.addTextChangedListener(object: TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) { }

                    override fun afterTextChanged(s: Editable?) { }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        s?.let { newPhone ->
                            tilPhone.error = null
                            tilPhone.isEndIconVisible = Validators.validatePhone(newPhone.toString()) && Validators.normalizePhone(newPhone.toString()) != user.phone
                        }
                    }
                })

                btnChangePassword.setOnClickListener {
                    val currentPassword = etCurrentPassword.text
                    val newPassword = etNewPassword.text
                    var valid = true
                    if (currentPassword.isNullOrEmpty()) {
                        tilCurrentPassword.error = getString(R.string.required_error_text)
                        valid = false
                    }
                    if (newPassword.isNullOrEmpty()) {
                        tilNewPassword.error = getString(R.string.required_error_text)
                        valid = false
                    }
                    if (valid) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            if (UserService.checkPassword(user, currentPassword.toString())) {
                                UserService.updatePassword(user, newPassword.toString())
                                user.passwordHash?.let { passHash ->
                                    AppConfig.rememberUser(user.email, passHash)
                                }
                                requireActivity().runOnUiThread {
                                    etCurrentPassword.setText("")
                                    etNewPassword.setText("")
                                    Snackbar.make(
                                        root,
                                        getString(R.string.password_changed_text),
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                requireActivity().runOnUiThread {
                                    tilCurrentPassword.error = getString(R.string.wrong_password_text)
                                }
                            }
                        }
                    }
                }

                btnLogout.setOnClickListener { logout() }
                btnDeleteAccount.setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        UserService.requestDelete(user)
                        requireActivity().runOnUiThread {
                            logout()
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        AppConfig.clearUserConfig()
        AppSession.clearAuthorizedUser()
        repeat (parentFragmentManager.backStackEntryCount) {
            parentFragmentManager.popBackStack()
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
