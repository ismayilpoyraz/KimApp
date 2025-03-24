package com.example.kimapp.model

data class Question(
    val id: Int,
    val category: String, // "KIM_SOYLEDI" veya "KIM_YAPTI"
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val backgroundColor: Int // Arka plan renk kaynağı
)
