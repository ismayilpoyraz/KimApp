package com.example.kimapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kimapp.databinding.ActivityPremiumBinding
import com.example.kimapp.utils.BillingManager
import com.example.kimapp.utils.SoundPlayer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PremiumActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPremiumBinding
    private lateinit var soundPlayer: SoundPlayer
    private lateinit var billingManager: BillingManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Ses efektleri için SoundPlayer başlat
        soundPlayer = SoundPlayer(this)
        
        // Satın alma işlemleri için BillingManager başlat
        billingManager = BillingManager(this)
        
        setupListeners()
        observePurchases()
    }
    
    private fun setupListeners() {
        // Premium satın alma butonu
        binding.btnBuyPremium.setOnClickListener {
            soundPlayer.playButtonClickSound()
            billingManager.launchPurchaseFlow(this, BillingManager.PREMIUM_UPGRADE)
        }
        
        // Reklamları kaldır butonu
        binding.btnRemoveAds.setOnClickListener {
            soundPlayer.playButtonClickSound()
            billingManager.launchPurchaseFlow(this, BillingManager.REMOVE_ADS)
        }
        
        // Ekstra sorular butonu
        binding.btnExtraQuestions.setOnClickListener {
            soundPlayer.playButtonClickSound()
            billingManager.launchPurchaseFlow(this, BillingManager.EXTRA_QUESTIONS)
        }
        
        // Geri dön butonu
        binding.btnBack.setOnClickListener {
            soundPlayer.playButtonClickSound()
            finish()
        }
    }
    
    private fun observePurchases() {
        // Satın almaları gözlemle
        lifecycleScope.launch {
            billingManager.isPremium.collect { isPremium ->
                updatePremiumUI(isPremium)
            }
        }
        
        lifecycleScope.launch {
            billingManager.isAdFree.collect { isAdFree ->
                updateAdFreeUI(isAdFree)
            }
        }
        
        lifecycleScope.launch {
            billingManager.hasExtraQuestions.collect { hasExtraQuestions ->
                updateExtraQuestionsUI(hasExtraQuestions)
            }
        }
    }
    
    private fun updatePremiumUI(isPremium: Boolean) {
        if (isPremium) {
            binding.btnBuyPremium.text = "Satın Alındı"
            binding.btnBuyPremium.isEnabled = false
            binding.tvPremiumUpgradeDesc.text = "Tüm premium özelliklere erişiminiz var. Teşekkürler!"
        }
    }
    
    private fun updateAdFreeUI(isAdFree: Boolean) {
        if (isAdFree) {
            binding.btnRemoveAds.text = "Satın Alındı"
            binding.btnRemoveAds.isEnabled = false
            binding.tvRemoveAdsDesc.text = "Reklamsız deneyimin keyfini çıkarın. Teşekkürler!"
        }
    }
    
    private fun updateExtraQuestionsUI(hasExtraQuestions: Boolean) {
        if (hasExtraQuestions) {
            binding.btnExtraQuestions.text = "Satın Alındı"
            binding.btnExtraQuestions.isEnabled = false
            binding.tvExtraQuestionsDesc.text = "Ekstra sorulara erişiminiz var. Teşekkürler!"
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Kaynakları serbest bırak
        soundPlayer.release()
        billingManager.release()
    }
}
