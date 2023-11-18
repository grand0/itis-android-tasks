package ru.kpfu.itis.ponomarev.androidcourse.ui

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import ru.kpfu.itis.ponomarev.androidcourse.MainActivity
import ru.kpfu.itis.ponomarev.androidcourse.R
import ru.kpfu.itis.ponomarev.androidcourse.databinding.FragmentCoroutinesBinding
import ru.kpfu.itis.ponomarev.androidcourse.util.AirplaneModeNotifier
import ru.kpfu.itis.ponomarev.androidcourse.util.toPx

class CoroutinesFragment : Fragment() {

    private var _binding: FragmentCoroutinesBinding? = null
    private val binding get() = _binding!!

    private var airplaneModeCallbackId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoroutinesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
    }

    private fun init() {
        binding.apply {
            sbCoroutinesCount.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                private var prevProgress = sbCoroutinesCount.progress

                private var animInProgress = false
                private var queuedProgress: Int? = null
                private var queuedPrevProgress: Int? = null

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (prevProgress == progress) return

                    val prev = prevProgress
                    prevProgress = progress

                    if (!animInProgress) {
                        launchAnimation(progress, prev)
                    } else {
                        queuedProgress = progress
                        queuedPrevProgress = prev
                    }
                }

                private fun launchAnimation(progress: Int, prev: Int) {
                    animInProgress = true

                    val anim = createTranslateAnimation(progress < prev)
                    val prevNextAnimSet = AnimationSet(true)
                        .apply {
                            addAnimation(anim)
                            addAnimation(createFadeAnimation(false))
                        }

                    if (progress < prev) {
                        tvCoroutinesCountPrev.text = progress.toString()
                        tvCoroutinesCountPrev.startAnimation(prevNextAnimSet)
                    } else {
                        tvCoroutinesCountNext.text = progress.toString()
                        tvCoroutinesCountNext.startAnimation(prevNextAnimSet)
                    }

                    anim.setAnimationListener(object : AnimationListener {
                        override fun onAnimationStart(animation: Animation?) { }

                        override fun onAnimationEnd(animation: Animation?) {
                            tvCoroutinesCount.text = progress.toString()

                            val progress = queuedProgress
                            val prev = queuedPrevProgress
                            if (progress != null && prev != null) {
                                queuedProgress = null
                                queuedPrevProgress = null
                                launchAnimation(progress, prev)
                            } else {
                                animInProgress = false
                            }
                        }

                        override fun onAnimationRepeat(animation: Animation?) { }
                    })
                    val curAnimSet = AnimationSet(true).apply {
                        addAnimation(anim)
                        addAnimation(createFadeAnimation(true))
                    }
                    tvCoroutinesCount.startAnimation(curAnimSet)
                }

                private fun createTranslateAnimation(decreasing: Boolean) = TranslateAnimation(
                    0f,
                    0f,
                    0f,
                    requireContext().toPx(if (decreasing) -24 else 24),
                ).apply {
                    duration = 100
                }

                private fun createFadeAnimation(fadeOut: Boolean) = AlphaAnimation(
                    if (fadeOut) 1f else 0f,
                    if (fadeOut) 0f else 1f,
                ).apply {
                    duration = 100
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) { }

                override fun onStopTrackingTouch(seekBar: SeekBar?) { }
            })

            tvCoroutinesCount.text = sbCoroutinesCount.progress.toString()

            btnExecute.setOnClickListener {
                val async = cbAsync.checkedState == MaterialCheckBox.STATE_CHECKED
                val stopOnBackground = cbStopOnBackground.checkedState == MaterialCheckBox.STATE_CHECKED
                (requireActivity() as MainActivity).startCoroutines(
                    sbCoroutinesCount.progress,
                    async,
                    stopOnBackground,
                )

//                btnExecute.text = getString(R.string.done_text)
                var avd = AppCompatResources.getDrawable(requireContext(), R.drawable.avd_coroutines_checked) as AnimatedVectorDrawable
                btnExecute.icon = avd
                avd.start()
                Handler(Looper.getMainLooper()).postDelayed({
//                    btnExecute.text = getString(R.string.execute_btn_text)
                    avd = AppCompatResources.getDrawable(requireContext(), R.drawable.avd_coroutines_unchecked) as AnimatedVectorDrawable
                    btnExecute.icon = avd
                    avd.start()
                }, 1000)
            }

            airplaneModeCallbackId = AirplaneModeNotifier.registerCallback(requireContext()) { isOn ->
                btnExecute.isEnabled = !isOn
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        airplaneModeCallbackId?.let {
            AirplaneModeNotifier.unregisterCallback(it)
        }
        super.onDestroyView()
    }
}
