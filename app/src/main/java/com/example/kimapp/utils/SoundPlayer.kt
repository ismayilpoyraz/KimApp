package com.example.kimapp.utils

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import com.example.kimapp.R

/**
 * Uygulama içindeki ses ve titreşim geri bildirimlerini yönetmek için yardımcı sınıf
 */
class SoundPlayer(private val context: Context) {
    
    // Titreşim için Vibrator
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    // Ses dosyaları için MediaPlayer nesneleri
    private var buttonClickPlayer: MediaPlayer? = null
    private var correctAnswerPlayer: MediaPlayer? = null
    private var wrongAnswerPlayer: MediaPlayer? = null
    private var timeExpiredPlayer: MediaPlayer? = null
    
    init {
        // MediaPlayer'ları hazırla
        prepareMediaPlayers()
    }
    
    /**
     * MediaPlayer'ları hazırlar
     */
    private fun prepareMediaPlayers() {
        try {
            // Buton tıklama sesi
            buttonClickPlayer = MediaPlayer.create(context, R.raw.button_click)
            
            // Doğru cevap sesi
            correctAnswerPlayer = MediaPlayer.create(context, R.raw.correct_answer)
            
            // Yanlış cevap sesi
            wrongAnswerPlayer = MediaPlayer.create(context, R.raw.wrong_answer)
            
            // Süre dolma sesi
            timeExpiredPlayer = MediaPlayer.create(context, R.raw.time_expired)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Buton tıklama sesi ve titreşimi
     */
    fun playButtonClickSound() {
        // Ses çal
        playSound(buttonClickPlayer)
        
        // Titreşim uygula
        vibrate(20)
    }
    
    /**
     * Doğru cevap sesi ve titreşimi
     */
    fun playCorrectAnswerSound() {
        // Ses çal
        playSound(correctAnswerPlayer)
        
        // Titreşim uygula
        vibrate(100, 2)
    }
    
    /**
     * Yanlış cevap sesi ve titreşimi
     */
    fun playWrongAnswerSound() {
        // Ses çal
        playSound(wrongAnswerPlayer)
        
        // Titreşim uygula
        vibrate(300)
    }
    
    /**
     * Süre dolduğunda ses ve titreşim
     */
    fun playTimeExpiredSound() {
        // Ses çal
        playSound(timeExpiredPlayer)
        
        // Titreşim uygula
        vibrate(50, 3)
    }
    
    /**
     * MediaPlayer ile ses çalar
     */
    private fun playSound(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.let {
            try {
                if (it.isPlaying) {
                    it.stop()
                    it.prepare()
                }
                it.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Belirtilen süre kadar titreşim uygular
     */
    private fun vibrate(milliseconds: Long, repeatCount: Int = 0) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (repeatCount > 0) {
                // Tekrarlı titreşim için
                val timings = LongArray(repeatCount * 2) { i -> if (i % 2 == 0) milliseconds else 100L }
                val amplitudes = IntArray(repeatCount * 2) { i -> if (i % 2 == 0) VibrationEffect.DEFAULT_AMPLITUDE else 0 }
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator.vibrate(effect)
            } else {
                // Tek seferlik titreşim
                val effect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            }
        } else {
            // Eski Android sürümleri için
            @Suppress("DEPRECATION")
            vibrator.vibrate(milliseconds)
        }
    }
    
    /**
     * Görünüm için dokunsal geri bildirim uygular
     */
    fun performHapticFeedback(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
    
    /**
     * Kaynakları serbest bırak
     */
    fun release() {
        // Titreşimi durdur
        vibrator.cancel()
        
        // MediaPlayer'ları serbest bırak
        buttonClickPlayer?.release()
        buttonClickPlayer = null
        
        correctAnswerPlayer?.release()
        correctAnswerPlayer = null
        
        wrongAnswerPlayer?.release()
        wrongAnswerPlayer = null
        
        timeExpiredPlayer?.release()
        timeExpiredPlayer = null
    }
}
