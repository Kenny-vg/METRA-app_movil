package com.softnamic.proyectointegradorii.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.softnamic.proyectointegradorii.databinding.ActivitySplashBinding
import com.softnamic.proyectointegradorii.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Instala y configura el splash screen del sistema
        installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Configura y reproduce tu animación Lottie
        val lottieView = binding.lottieAnimationView
        lottieView.setAnimation("coffee_time.json")
        lottieView.loop(false)

        // --- DURACIÓN AJUSTADA --- (Más lento = dura más)
        lottieView.speed = 0.6f

        lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // 3. Cuando la animación termina, navega a la siguiente pantalla
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        // Inicia la animación
        lottieView.playAnimation()
    }
}