package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentLoginBinding
import ru.kpfu.itis.ponomarev.androidcourse.service.UserService
import ru.kpfu.itis.ponomarev.androidcourse.config.AppConfig
import ru.kpfu.itis.ponomarev.androidcourse.util.clearErrorOnType

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        with (binding) {
            tilEmail.clearErrorOnType()
            tilPassword.clearErrorOnType()

            btnLogin.setOnClickListener {
                val email = etEmail.text
                val password = etPassword.text
                var valid = true
                if (email.isNullOrEmpty()) {
                    tilEmail.error = getString(R.string.required_error_text)
                    valid = false
                }
                if (password.isNullOrEmpty()) {
                    tilPassword.error = getString(R.string.required_error_text)
                    valid = false
                }
                if (valid) {
                    setEnabledForAll(false)
                    lifecycleScope.launch(Dispatchers.IO) {
                        val user = UserService.getUserByCredentials(email.toString(), password.toString())
                        if (user == null) {
                            requireActivity().runOnUiThread {
                                Snackbar.make(
                                    root,
                                    R.string.wrong_credentials_error_text,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                setEnabledForAll(true)
                            }
                        } else {
                            AppConfig.rememberUser(user.email, user.passwordHash!!)
                            parentFragmentManager.beginTransaction()
                                .replace(
                                    R.id.fragment_container,
                                    HomeFragment()
                                )
                                .commit()
                        }
                    }
                }
            }
            btnToRegister.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        RegisterFragment(),
                    )
                    .commit()
            }
        }
    }

    private fun setEnabledForAll(enabled: Boolean) {
        with (binding) {
            tilEmail.isEnabled = enabled
            tilPassword.isEnabled = enabled
            btnLogin.isEnabled = enabled
            btnToRegister.isEnabled = enabled
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
