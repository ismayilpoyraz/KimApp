package com.example.kimapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kimapp.databinding.ActivityResultBinding
import com.example.kimapp.utils.AdManager
import com.example.kimapp.utils.BillingManager
import com.example.kimapp.utils.SoundPlayer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityResultBinding
    private lateinit var soundPlayer: SoundPlayer
    private lateinit var adManager: AdManager
    private lateinit var billingManager: BillingManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Ses oynatıcısını başlat
        soundPlayer = SoundPlayer(this)
        
        // Reklam yöneticisini başlat
        adManager = AdManager(this)
        adManager.initialize()
        adManager.loadRewardedAd()
        
        // Satın alma yöneticisini başlat
        billingManager = BillingManager(this)
        
        // Intent'ten skoru al
        val score = intent.getIntExtra(QuestionActivity.EXTRA_SCORE, 0)
        val totalQuestions = intent.getIntExtra(QuestionActivity.EXTRA_TOTAL_QUESTIONS, 0)
        
        // Skoru göster
        binding.tvScore.text = "Skorunuz: $score / $totalQuestions"
        
        // Performans mesajını ayarla
        val performanceMessage = when {
            score == totalQuestions -> "Mükemmel! Tüm soruları doğru cevapladınız."
            score >= totalQuestions * 0.8 -> "Harika! Çok iyi bir performans gösterdiniz."
            score >= totalQuestions * 0.6 -> "İyi! Bilginizi geliştirmeye devam edin."
            score >= totalQuestions * 0.4 -> "Fena değil. Biraz daha çalışmaya ne dersiniz?"
            else -> "Daha fazla pratik yaparak bilginizi geliştirebilirsiniz."
        }
        binding.tvPerformance.text = performanceMessage
        
        // Buton tıklamalarını ayarla
        setupListeners()
        
        // Satın alma durumunu gözlemle
        observePurchases()
    }
    
    private fun setupListeners() {
        // Ana menüye dön butonu
        binding.btnMainMenu.setOnClickListener {
            soundPlayer.playButtonClickSound()
            finish()
        }
        
        // Tekrar oyna butonu
        binding.btnPlayAgain.setOnClickListener {
            soundPlayer.playButtonClickSound()
            finish()
            // Bir önceki aktiviteyi sonlandırdık, yeni bir QuestionActivity başlatmıyoruz
            // Kullanıcı ana menüden tekrar kategori seçebilir
        }
        
        // Ekstra puan kazan butonu
        binding.btnEarnExtraPoints.setOnClickListener {
            soundPlayer.playButtonClickSound()
            showRewardedAd()
        }
    }
    
    private fun observePurchases() {
        // Reklamsız mod durumunu gözlemle
        lifecycleScope.launch {
            billingManager.isAdFree.collect { isAdFree ->
                adManager.setAdFree(isAdFree)
                if (isAdFree) {
                    binding.btnEarnExtraPoints.isEnabled = false
                    binding.btnEarnExtraPoints.text = "Premium Üyelik Aktif"
                }
            }
        }
    }
    
    private fun showRewardedAd() {
        adManager.showRewardedAd(
            this,
            onRewarded = {
                // Ödül kazanıldığında
                val currentScore = binding.tvScore.text.toString().split(":")[1].trim().split("/")[0].trim().toInt()
                val totalQuestions = binding.tvScore.text.toString().split("/")[1].trim().toInt()
                val newScore = currentScore + 1
                
                // Yeni skoru göster
                binding.tvScore.text = "Skorunuz: $newScore / $totalQuestions"
                
                // Performans mesajını güncelle
                val performanceMessage = when {
                    newScore == totalQuestions -> "Mükemmel! Tüm soruları doğru cevapladınız."
                    newScore >= totalQuestions * 0.8 -> "Harika! Çok iyi bir performans gösterdiniz."
                    newScore >= totalQuestions * 0.6 -> "İyi! Bilginizi geliştirmeye devam edin."
                    newScore >= totalQuestions * 0.4 -> "Fena değil. Biraz daha çalışmaya ne dersiniz?"
                    else -> "Daha fazla pratik yaparak bilginizi geliştirebilirsiniz."
                }
                binding.tvPerformance.text = performanceMessage
            },
            onAdDismissed = {
                // Reklam kapatıldığında
                adManager.loadRewardedAd() // Yeni reklam yükle
            }
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Ses kaynaklarını serbest bırak
        soundPlayer.release()
    }
}
