package com.example.kimapp.models

/**
 * Soru modelini temsil eden veri sınıfı
 *
 * @property text Soru metni
 * @property options Cevap seçenekleri listesi
 * @property correctAnswerIndex Doğru cevabın listedeki indeksi
 * @property backgroundColor Sorunun arka plan rengi
 */
data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val backgroundColor: Int
)
