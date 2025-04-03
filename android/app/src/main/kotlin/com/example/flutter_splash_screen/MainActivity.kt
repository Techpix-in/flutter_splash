package com.example.flutter_splash_screen

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.WindowCompat
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity() {

    var flutterUIReady: Boolean = false
    var initialAnimationFinished: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            onSplashScreenExit(splashScreenViewProvider)
        }
    }

    override fun onFlutterUiDisplayed() {
        flutterUIReady = true
    }

    override fun onFlutterUiNoLongerDisplayed() {
        flutterUIReady = false
    }

    private fun onSplashScreenExit(splashScreenViewProvider: SplashScreenViewProvider) {
        val splashScreenView = splashScreenViewProvider.view

        val alpha = ValueAnimator.ofInt(255, 0).apply {
            duration = SPLASHSCREEN_ALPHA_ANIMATION_DURATION
            addUpdateListener { valueAnimator ->
                splashScreenView.background.alpha = valueAnimator.animatedValue as Int
            }
        }

        alpha.doOnEnd {
            initialAnimationFinished = true
            splashScreenViewProvider.remove()
        }

        waitForAnimatedIconToFinish(splashScreenViewProvider, splashScreenView) {
            alpha.start()
        }
    }

    private fun SplashScreenViewProvider.remainingAnimationDuration() =
        iconAnimationStartMillis + iconAnimationDurationMillis - System.currentTimeMillis()

    private fun waitForAnimatedIconToFinish(
        splashScreenViewProvider: SplashScreenViewProvider,
        view: View,
        onAnimationFinished: () -> Unit
    ) {
        val delayMillis: Long =
            if (WAIT_FOR_AVD_TO_FINISH) splashScreenViewProvider.remainingAnimationDuration() else 0

        view.postDelayed({ onAnimationFinished() }, delayMillis) // ✅ FIXED HERE
    }

    private companion object {
        const val SPLASHSCREEN_ALPHA_ANIMATION_DURATION = 5000L
        const val WAIT_FOR_AVD_TO_FINISH = false
    }
}

