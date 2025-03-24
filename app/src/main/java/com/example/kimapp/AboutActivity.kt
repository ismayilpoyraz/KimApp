package com.example.kimapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kimapp.databinding.ActivityAboutBinding
import com.example.kimapp.utils.SoundPlayer

class AboutActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAboutBinding
    private lateinit var soundPlayer: SoundPlayer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Ses efektleri için SoundPlayer başlat
        soundPlayer = SoundPlayer(this)
        
        // Geri dön butonuna tıklama işlemi
        binding.btnBack.setOnClickListener {
            soundPlayer.playButtonClickSound()
            finish()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Kaynakları serbest bırak
        soundPlayer.release()
    }
}
