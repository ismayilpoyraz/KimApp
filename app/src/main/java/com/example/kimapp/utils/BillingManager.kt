package com.example.kimapp.utils

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Google Play Store satın alma işlemlerini yöneten sınıf
 */
class BillingManager(private val context: Context) : PurchasesUpdatedListener, BillingClientStateListener {

    // Satın alınabilir ürünlerin ID'leri
    companion object {
        const val PREMIUM_UPGRADE = "premium_upgrade"
        const val EXTRA_QUESTIONS = "extra_questions"
        const val REMOVE_ADS = "remove_ads"
    }

    // BillingClient nesnesi
    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    // Ürün detayları
    private val _productDetails = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetails: StateFlow<List<ProductDetails>> = _productDetails.asStateFlow()

    // Satın alınan ürünler
    private val _purchases = MutableStateFlow<List<Purchase>>(emptyList())
    val purchases: StateFlow<List<Purchase>> = _purchases.asStateFlow()

    // Satın alma durumu
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    // Reklamsız mod durumu
    private val _isAdFree = MutableStateFlow(false)
    val isAdFree: StateFlow<Boolean> = _isAdFree.asStateFlow()

    // Ekstra sorular durumu
    private val _hasExtraQuestions = MutableStateFlow(false)
    val hasExtraQuestions: StateFlow<Boolean> = _hasExtraQuestions.asStateFlow()

    init {
        // BillingClient'ı başlat
        billingClient.startConnection(this)
    }

    /**
     * BillingClient bağlantısı kurulduğunda çağrılır
     */
    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingResponseCode.OK) {
            // Bağlantı başarılı, ürünleri ve satın almaları sorgula
            CoroutineScope(Dispatchers.IO).launch {
                queryProductDetails()
                queryPurchases()
            }
        } else {
            // Bağlantı hatası
            Toast.makeText(
                context,
                "Satın alma sistemi başlatılamadı: ${billingResult.debugMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * BillingClient bağlantısı kesildiğinde çağrılır
     */
    override fun onBillingServiceDisconnected() {
        // Yeniden bağlanmayı dene
        billingClient.startConnection(this)
    }

    /**
     * Satın alma işlemi güncellendiğinde çağrılır
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            // Satın alma başarılı
            CoroutineScope(Dispatchers.IO).launch {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            // Kullanıcı satın almayı iptal etti
            Toast.makeText(context, "Satın alma iptal edildi", Toast.LENGTH_SHORT).show()
        } else {
            // Satın alma hatası
            Toast.makeText(
                context,
                "Satın alma hatası: ${billingResult.debugMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Ürün detaylarını sorgular
     */
    private suspend fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PREMIUM_UPGRADE)
                .setProductType(ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(EXTRA_QUESTIONS)
                .setProductType(ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(REMOVE_ADS)
                .setProductType(ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params)
        }

        if (productDetailsResult.billingResult.responseCode == BillingResponseCode.OK) {
            _productDetails.value = productDetailsResult.productDetailsList ?: emptyList()
        }
    }

    /**
     * Mevcut satın almaları sorgular
     */
    private suspend fun queryPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(ProductType.INAPP)
            .build()

        val purchasesResult = withContext(Dispatchers.IO) {
            billingClient.queryPurchasesAsync(params)
        }

        if (purchasesResult.billingResult.responseCode == BillingResponseCode.OK) {
            _purchases.value = purchasesResult.purchasesList
            updatePurchaseStatus(purchasesResult.purchasesList)
        }
    }

    /**
     * Satın alma durumunu günceller
     */
    private fun updatePurchaseStatus(purchasesList: List<Purchase>) {
        var premium = false
        var adFree = false
        var extraQuestions = false

        for (purchase in purchasesList) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                when {
                    purchase.products.contains(PREMIUM_UPGRADE) -> premium = true
                    purchase.products.contains(REMOVE_ADS) -> adFree = true
                    purchase.products.contains(EXTRA_QUESTIONS) -> extraQuestions = true
                }
            }
        }

        _isPremium.value = premium
        _isAdFree.value = adFree
        _hasExtraQuestions.value = extraQuestions
    }

    /**
     * Satın alma işlemini yönetir
     */
    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Satın alma onaylandı, ürünü teslim et
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                
                val ackResult = withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams)
                }
                
                if (ackResult.responseCode == BillingResponseCode.OK) {
                    // Satın alma onaylandı
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Satın alma başarılı!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            // Satın alma durumunu güncelle
            queryPurchases()
        }
    }

    /**
     * Ürün satın alma işlemini başlatır
     */
    fun launchPurchaseFlow(activity: Activity, productId: String) {
        val productDetail = _productDetails.value.find { it.productId == productId }
        
        if (productDetail != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetail)
                    .build()
            )

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        } else {
            Toast.makeText(context, "Ürün bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Kaynakları serbest bırakır
     */
    fun release() {
        billingClient.endConnection()
    }
}
