package com.example.kimapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kimapp.databinding.ActivityQuestionBinding
import com.example.kimapp.models.Question
import com.example.kimapp.utils.AdManager
import com.example.kimapp.utils.SoundPlayer
import java.util.Timer
import java.util.TimerTask

class QuestionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        private const val DELAY_TO_NEXT_QUESTION = 1500L // 1.5 saniye
        private const val COUNTDOWN_TIME = 15000L // 15 saniye
        private const val COUNTDOWN_INTERVAL = 1000L // 1 saniye
    }

    private lateinit var binding: ActivityQuestionBinding
    private lateinit var soundPlayer: SoundPlayer
    private lateinit var adManager: AdManager
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var score = 0
    private var wrongAnswers = 0
    private var countDownTimer: CountDownTimer? = null
    private var timeRemaining = COUNTDOWN_TIME
    private var category = ""
    private var questionCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ses oynatıcısını başlat
        soundPlayer = SoundPlayer(this)
        
        // Reklam yöneticisini başlat
        adManager = AdManager(this)
        adManager.initialize()
        adManager.loadInterstitialAd()

        // Intent'ten kategoriyi al
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "KIM_SOYLEDI"

        // Kategori başlığını ayarla
        binding.tvCategoryTitle.text = when (category) {
            "KIM_SOYLEDI" -> "Kim Söyledi?"
            "KIM_YAPTI" -> "Kim Yaptı?"
            else -> "Kim?"
        }

        // Soruları yükle
        loadQuestions()

        // İlk soruyu göster
        showQuestion()

        // Buton tıklamalarını ayarla
        setupButtonListeners()
    }

    private fun loadQuestions() {
        // Burada gerçek bir veritabanından veya API'den sorular yüklenebilir
        // Şimdilik örnek sorular oluşturuyoruz
        questions = when (category) {
            "KIM_SOYLEDI" -> createWhoSaidQuestions()
            "KIM_YAPTI" -> createWhoDitQuestions()
            else -> createWhoSaidQuestions()
        }
        
        // Toplam soru sayısını ayarla
        questionCount = questions.size
    }

    private fun createWhoSaidQuestions(): List<Question> {
        return listOf(
            Question(
                "Düşünüyorum, öyleyse varım.",
                listOf("Sokrates", "Descartes", "Platon", "Aristoteles"),
                1,
                Color.parseColor("#FF5722")
            ),
            Question(
                "Bildiğim tek şey, hiçbir şey bilmediğimdir.",
                listOf("Sokrates", "Nietzsche", "Kant", "Hume"),
                0,
                Color.parseColor("#E91E63")
            ),
            Question(
                "İnsan, insan kurdudur.",
                listOf("Rousseau", "Hobbes", "Locke", "Marx"),
                1,
                Color.parseColor("#9C27B0")
            ),
            Question(
                "Tanrı öldü.",
                listOf("Sartre", "Camus", "Nietzsche", "Kierkegaard"),
                2,
                Color.parseColor("#673AB7")
            ),
            Question(
                "Özgürlük, başkalarının özgürlüğü başladığı yerde biter.",
                listOf("Mill", "Rousseau", "Locke", "Voltaire"),
                3,
                Color.parseColor("#3F51B5")
            )
        )
    }

    private fun createWhoDitQuestions(): List<Question> {
        return listOf(
            Question(
                "Yerçekimi yasasını kim keşfetti?",
                listOf("Einstein", "Newton", "Galileo", "Kepler"),
                1,
                Color.parseColor("#2196F3")
            ),
            Question(
                "Görelilik teorisini kim geliştirdi?",
                listOf("Einstein", "Bohr", "Heisenberg", "Schrödinger"),
                0,
                Color.parseColor("#03A9F4")
            ),
            Question(
                "Amerika'yı kim keşfetti?",
                listOf("Magellan", "Vasco da Gama", "Kolomb", "Amerigo Vespucci"),
                2,
                Color.parseColor("#00BCD4")
            ),
            Question(
                "Telefonu kim icat etti?",
                listOf("Edison", "Bell", "Tesla", "Marconi"),
                1,
                Color.parseColor("#009688")
            ),
            Question(
                "İlk bilgisayarı kim icat etti?",
                listOf("Turing", "Babbage", "Von Neumann", "Pascal"),
                1,
                Color.parseColor("#4CAF50")
            )
        )
    }

    private fun showQuestion() {
        // Eğer tüm sorular bittiyse veya 3 yanlış cevap verildiyse sonuç ekranına git
        if (currentQuestionIndex >= questions.size || wrongAnswers >= 3) {
            showResult()
            return
        }

        // Mevcut soruyu al
        val question = questions[currentQuestionIndex]

        // Arka plan rengini ayarla
        binding.root.setBackgroundColor(question.backgroundColor)

        // Soru metnini ayarla
        binding.tvQuestion.text = question.text

        // Cevap butonlarını ayarla
        binding.btnAnswer1.text = question.options[0]
        binding.btnAnswer2.text = question.options[1]
        binding.btnAnswer3.text = question.options[2]
        binding.btnAnswer4.text = question.options[3]

        // Butonları etkinleştir
        setAnswerButtonsEnabled(true)

        // İlerleme çubuğunu güncelle
        updateProgressBar()

        // Geri sayımı başlat
        startCountdown()
    }

    private fun setupButtonListeners() {
        binding.btnAnswer1.setOnClickListener {
            soundPlayer.playButtonClickSound()
            checkAnswer(0)
        }

        binding.btnAnswer2.setOnClickListener {
            soundPlayer.playButtonClickSound()
            checkAnswer(1)
        }

        binding.btnAnswer3.setOnClickListener {
            soundPlayer.playButtonClickSound()
            checkAnswer(2)
        }

        binding.btnAnswer4.setOnClickListener {
            soundPlayer.playButtonClickSound()
            checkAnswer(3)
        }
    }

    private fun checkAnswer(selectedAnswerIndex: Int) {
        // Geri sayımı durdur
        countDownTimer?.cancel()

        // Mevcut soruyu al
        val question = questions[currentQuestionIndex]

        // Butonları devre dışı bırak
        setAnswerButtonsEnabled(false)

        // Doğru cevabı vurgula
        highlightCorrectAnswer(question.correctAnswerIndex)

        // Cevabı kontrol et
        if (selectedAnswerIndex == question.correctAnswerIndex) {
            // Doğru cevap
            score++
            soundPlayer.playCorrectAnswerSound()
        } else {
            // Yanlış cevap
            wrongAnswers++
            soundPlayer.playWrongAnswerSound()
            // Seçilen yanlış cevabı vurgula
            highlightWrongAnswer(selectedAnswerIndex)
        }

        // Skor metnini güncelle
        updateScoreText()

        // Gecikmeli olarak bir sonraki soruya geç
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    currentQuestionIndex++
                    
                    // Her 2 sorudan sonra reklam göster
                    if (currentQuestionIndex % 2 == 0 && currentQuestionIndex > 0) {
                        adManager.showInterstitialAd(this@QuestionActivity) {
                            // Reklam kapatıldıktan sonra bir sonraki soruya geç
                            showQuestion()
                        }
                    } else {
                        showQuestion()
                    }
                }
            }
        }, DELAY_TO_NEXT_QUESTION)
    }

    private fun highlightCorrectAnswer(correctAnswerIndex: Int) {
        when (correctAnswerIndex) {
            0 -> binding.btnAnswer1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            1 -> binding.btnAnswer2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            2 -> binding.btnAnswer3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
            3 -> binding.btnAnswer4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
        }
    }

    private fun highlightWrongAnswer(wrongAnswerIndex: Int) {
        when (wrongAnswerIndex) {
            0 -> binding.btnAnswer1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            1 -> binding.btnAnswer2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            2 -> binding.btnAnswer3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
            3 -> binding.btnAnswer4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
        }
    }

    private fun setAnswerButtonsEnabled(enabled: Boolean) {
        binding.btnAnswer1.isEnabled = enabled
        binding.btnAnswer2.isEnabled = enabled
        binding.btnAnswer3.isEnabled = enabled
        binding.btnAnswer4.isEnabled = enabled

        // Butonların arka plan rengini sıfırla
        if (enabled) {
            binding.btnAnswer1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            binding.btnAnswer2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            binding.btnAnswer3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            binding.btnAnswer4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
        }
    }

    private fun updateScoreText() {
        binding.tvScore.text = "Skor: $score"
    }

    private fun updateProgressBar() {
        binding.progressBar.progress = ((currentQuestionIndex + 1) * 100) / questions.size
        binding.tvProgress.text = "${currentQuestionIndex + 1}/${questions.size}"
    }

    private fun startCountdown() {
        // Geri sayım metnini görünür yap
        binding.tvCountdown.visibility = View.VISIBLE

        // Geri sayım süresini sıfırla
        timeRemaining = COUNTDOWN_TIME

        // Yeni bir geri sayım başlat
        countDownTimer = object : CountDownTimer(COUNTDOWN_TIME, COUNTDOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                binding.tvCountdown.text = "${millisUntilFinished / 1000}"

                // Son 5 saniyede kırmızı renk
                if (millisUntilFinished <= 5000) {
                    binding.tvCountdown.setTextColor(Color.RED)
                } else {
                    binding.tvCountdown.setTextColor(Color.WHITE)
                }
            }

            override fun onFinish() {
                // Süre dolduğunda
                timeRemaining = 0
                binding.tvCountdown.text = "0"
                soundPlayer.playTimeExpiredSound()

                // Butonları devre dışı bırak
                setAnswerButtonsEnabled(false)

                // Doğru cevabı göster
                val question = questions[currentQuestionIndex]
                highlightCorrectAnswer(question.correctAnswerIndex)

                // Yanlış sayısını artır
                wrongAnswers++

                // Gecikmeli olarak bir sonraki soruya geç
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            currentQuestionIndex++
                            showQuestion()
                        }
                    }
                }, DELAY_TO_NEXT_QUESTION)
            }
        }.start()
    }

    private fun showResult() {
        // Geri sayımı durdur
        countDownTimer?.cancel()

        // Sonuç ekranına git
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(EXTRA_SCORE, score)
            putExtra(EXTRA_TOTAL_QUESTIONS, questionCount)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Geri sayımı durdur
        countDownTimer?.cancel()
        // Ses kaynaklarını serbest bırak
        soundPlayer.release()
    }
}
