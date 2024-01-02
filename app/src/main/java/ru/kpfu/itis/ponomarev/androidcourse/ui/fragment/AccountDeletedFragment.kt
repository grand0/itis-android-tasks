package ru.kpfu.itis.ponomarev.androidcourse.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.config.AppConfig
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentAccountDeletedBinding
import ru.kpfu.itis.ponomarev.androidcourse.service.UserService
import ru.kpfu.itis.ponomarev.androidcourse.session.AppSession
import kotlin.math.ceil

class AccountDeletedFragment : Fragment() {

    private var _binding: FragmentAccountDeletedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDeletedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        val user = AppSession.authorizedUser
        user?.deletionRequestTimestamp?.let { deletionRequestTimestamp ->
            val timePassed = System.currentTimeMillis() - deletionRequestTimestamp
            val timeLeft = AppConfig.USER_DELETION_GRACE_PERIOD_MS - timePassed
            val daysLeft = ceil(timeLeft / (24 * 60 * 60 * 1000.0)).toInt()
            with (binding) {
                tvInfo.text = getString(R.string.account_being_deleted_info, daysLeft)
                btnRecover.setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        UserService.cancelDeleteRequest(user)
                        requireActivity().runOnUiThread {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, HomeFragment())
                                .commit()
                        }
                    }
                }
                btnDelete.setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                        UserService.fullyDelete(user)
                    }
                    logout()
                }
                btnLogout.setOnClickListener {
                    logout()
                }
            }
        }
    }

    private fun logout() {
        AppConfig.clearUserConfig()
        AppSession.clearAuthorizedUser()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment())
            .commit()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}