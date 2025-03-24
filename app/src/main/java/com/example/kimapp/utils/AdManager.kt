package com.example.kimapp.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * AdMob reklamlarını yönetmek için yardımcı sınıf
 */
class AdManager(private val context: Context) {

    // Tam sayfa reklam
    private var interstitialAd: InterstitialAd? = null
    
    // Ödüllü reklam
    private var rewardedAd: RewardedAd? = null
    
    // Reklam durumu
    private var isAdFree = false
    
    // Test reklam ID'leri
    companion object {
        // Banner reklam test ID
        const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
        
        // Tam sayfa reklam test ID
        const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
        
        // Ödüllü reklam test ID
        const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    }
    
    /**
     * AdMob'u başlatır
     */
    fun initialize() {
        MobileAds.initialize(context) { initializationStatus ->
            // AdMob başlatıldı
        }
    }
    
    /**
     * Reklamsız modu ayarlar
     */
    fun setAdFree(adFree: Boolean) {
        isAdFree = adFree
    }
    
    /**
     * Banner reklamı yükler
     */
    fun loadBannerAd(adView: AdView) {
        if (isAdFree) {
            adView.visibility = AdView.GONE
            return
        }
        
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
    
    /**
     * Tam sayfa reklamı yükler
     */
    fun loadInterstitialAd() {
        if (isAdFree) return
        
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
                
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
            }
        )
    }
    
    /**
     * Tam sayfa reklamı gösterir
     */
    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
        if (isAdFree) {
            onAdDismissed()
            return
        }
        
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Reklam kapatıldığında
                    interstitialAd = null
                    loadInterstitialAd() // Yeni reklam yükle
                    onAdDismissed()
                }
                
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Reklam gösterilemediğinde
                    interstitialAd = null
                    onAdDismissed()
                }
            }
            
            interstitialAd?.show(activity)
        } else {
            // Reklam hazır değilse
            onAdDismissed()
            loadInterstitialAd() // Yeni reklam yükle
        }
    }
    
    /**
     * Ödüllü reklamı yükler
     */
    fun loadRewardedAd() {
        if (isAdFree) return
        
        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }
                
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
            }
        )
    }
    
    /**
     * Ödüllü reklamı gösterir
     */
    fun showRewardedAd(
        activity: Activity,
        onRewarded: () -> Unit,
        onAdDismissed: () -> Unit
    ) {
        if (isAdFree) {
            onRewarded()
            onAdDismissed()
            return
        }
        
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Reklam kapatıldığında
                    rewardedAd = null
                    loadRewardedAd() // Yeni reklam yükle
                    onAdDismissed()
                }
                
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Reklam gösterilemediğinde
                    rewardedAd = null
                    onAdDismissed()
                }
            }
            
            rewardedAd?.show(activity) { rewardItem ->
                // Kullanıcı ödülü aldığında
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Toast.makeText(
                    context,
                    "Ödül kazandınız: $rewardAmount $rewardType",
                    Toast.LENGTH_SHORT
                ).show()
                onRewarded()
            }
        } else {
            // Reklam hazır değilse
            Toast.makeText(
                context,
                "Reklam şu anda hazır değil, lütfen daha sonra tekrar deneyin.",
                Toast.LENGTH_SHORT
            ).show()
            loadRewardedAd() // Yeni reklam yükle
            onAdDismissed()
        }
    }
}
