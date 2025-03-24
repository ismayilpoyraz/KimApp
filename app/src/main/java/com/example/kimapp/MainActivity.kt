package com.example.kimapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kimapp.databinding.ActivityMainBinding
import com.example.kimapp.utils.AdManager
import com.example.kimapp.utils.BillingManager
import com.example.kimapp.utils.SoundPlayer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var soundPlayer: SoundPlayer
    private lateinit var adManager: AdManager
    private lateinit var billingManager: BillingManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Ses oynatıcısını başlat
        soundPlayer = SoundPlayer(this)
        
        // Reklam yöneticisini başlat
        adManager = AdManager(this)
        adManager.initialize()
        
        // Satın alma yöneticisini başlat
        billingManager = BillingManager(this)
        
        setupListeners()
        observePurchases()
        loadAds()
    }
    
    private fun setupListeners() {
        binding.cardWhoSaid.setOnClickListener {
            soundPlayer.playButtonClickSound()
            startQuiz("KIM_SOYLEDI")
        }
        
        binding.cardWhoDid.setOnClickListener {
            soundPlayer.playButtonClickSound()
            startQuiz("KIM_YAPTI")
        }
        
        binding.btnAbout.setOnClickListener {
            soundPlayer.playButtonClickSound()
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
        
        binding.btnPremium.setOnClickListener {
            soundPlayer.playButtonClickSound()
            val intent = Intent(this, PremiumActivity::class.java)
            startActivity(intent)
        }
    }
    
    private fun observePurchases() {
        // Reklamsız mod durumunu gözlemle
        lifecycleScope.launch {
            billingManager.isAdFree.collect { isAdFree ->
                adManager.setAdFree(isAdFree)
                if (isAdFree) {
                    binding.adView.visibility = android.view.View.GONE
                } else {
                    binding.adView.visibility = android.view.View.VISIBLE
                    adManager.loadBannerAd(binding.adView)
                }
            }
        }
    }
    
    private fun loadAds() {
        // Banner reklamı yükle
        adManager.loadBannerAd(binding.adView)
        
        // Tam sayfa reklamı yükle
        adManager.loadInterstitialAd()
        
        // Ödüllü reklamı yükle
        adManager.loadRewardedAd()
    }
    
    private fun startQuiz(category: String) {
        val intent = Intent(this, QuestionActivity::class.java).apply {
            putExtra(QuestionActivity.EXTRA_CATEGORY, category)
        }
        startActivity(intent)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Ses kaynaklarını serbest bırak
        soundPlayer.release()
        
        // Satın alma kaynaklarını serbest bırak
        billingManager.release()
    }
}
