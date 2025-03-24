package com.example.kimapp.data

import com.example.kimapp.R
import com.example.kimapp.model.Category
import com.example.kimapp.model.Question

object QuestionBank {
    
    fun getQuestions(category: String): List<Question> {
        return when (category) {
            Category.KIM_SOYLEDI -> getWhoSaidQuestions()
            Category.KIM_YAPTI -> getWhoDidQuestions()
            else -> emptyList()
        }
    }
    
    private fun getWhoSaidQuestions(): List<Question> {
        return listOf(
            Question(
                id = 1,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Düşünüyorum, öyleyse varım.\" sözünü kim söyledi?",
                options = listOf("René Descartes", "Sokrates", "Platon", "Aristoteles"),
                correctAnswerIndex = 0,
                backgroundColor = R.color.bg_color_1
            ),
            Question(
                id = 2,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Bildiğim tek şey, hiçbir şey bilmediğimdir.\" sözünü kim söyledi?",
                options = listOf("Platon", "Sokrates", "Aristoteles", "Nietzsche"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_2
            ),
            Question(
                id = 3,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Vatan sevgisini söylemekten ve onu duyduğum gibi duymaktan utanmam.\" sözünü kim söyledi?",
                options = listOf("İsmet İnönü", "Mustafa Kemal Atatürk", "Mehmet Akif Ersoy", "Namık Kemal"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_3
            ),
            Question(
                id = 4,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Hayatta en hakiki mürşit ilimdir.\" sözünü kim söyledi?",
                options = listOf("Ziya Gökalp", "Mustafa Kemal Atatürk", "İsmet İnönü", "Mehmet Akif Ersoy"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_4
            ),
            Question(
                id = 5,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Olmak ya da olmamak, işte bütün mesele bu.\" sözünü kim söyledi?",
                options = listOf("William Shakespeare", "Oscar Wilde", "Charles Dickens", "Victor Hugo"),
                correctAnswerIndex = 0,
                backgroundColor = R.color.bg_color_5
            ),
            Question(
                id = 6,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Cahillik mutluluktur.\" sözünü kim söyledi?",
                options = listOf("George Orwell", "Aldous Huxley", "Thomas Jefferson", "Friedrich Nietzsche"),
                correctAnswerIndex = 0,
                backgroundColor = R.color.bg_color_6
            ),
            Question(
                id = 7,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Ben bir insanım, insana ait hiçbir şey bana yabancı değildir.\" sözünü kim söyledi?",
                options = listOf("Cicero", "Terentius", "Seneca", "Marcus Aurelius"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_7
            ),
            Question(
                id = 8,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Özgürlük, başkalarına zarar vermediğin sürece istediğini yapabilmektir.\" sözünü kim söyledi?",
                options = listOf("John Locke", "Jean-Jacques Rousseau", "Voltaire", "John Stuart Mill"),
                correctAnswerIndex = 2,
                backgroundColor = R.color.bg_color_8
            ),
            Question(
                id = 9,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Bilgi güçtür.\" sözünü kim söyledi?",
                options = listOf("Francis Bacon", "Isaac Newton", "Galileo Galilei", "Albert Einstein"),
                correctAnswerIndex = 0,
                backgroundColor = R.color.bg_color_9
            ),
            Question(
                id = 10,
                category = Category.KIM_SOYLEDI,
                questionText = "\"Savaşta ve barışta, kaderde ve kıvançta hep beraber.\" sözünü kim söyledi?",
                options = listOf("İsmet İnönü", "Mustafa Kemal Atatürk", "Adnan Menderes", "Celal Bayar"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_10
            )
        )
    }
    
    private fun getWhoDidQuestions(): List<Question> {
        return listOf(
            Question(
                id = 1,
                category = Category.KIM_YAPTI,
                questionText = "Yerçekimi kanununu kim keşfetti?",
                options = listOf("Isaac Newton", "Albert Einstein", "Galileo Galilei", "Nikola Tesla"),
                correctAnswerIndex = 0,
                backgroundColor = R.color.bg_color_1
            ),
            Question(
                id = 2,
                category = Category.KIM_YAPTI,
                questionText = "Türkiye Cumhuriyeti'ni kim kurdu?",
                options = listOf("İsmet İnönü", "Mustafa Kemal Atatürk", "Adnan Menderes", "Celal Bayar"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_2
            ),
            Question(
                id = 3,
                category = Category.KIM_YAPTI,
                questionText = "Görelilik teorisini kim geliştirdi?",
                options = listOf("Isaac Newton", "Albert Einstein", "Nikola Tesla", "Stephen Hawking"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_3
            ),
            Question(
                id = 4,
                category = Category.KIM_YAPTI,
                questionText = "Amerika'yı kim keşfetti?",
                options = listOf("Vasco da Gama", "Ferdinand Magellan", "Kristof Kolomb", "James Cook"),
                correctAnswerIndex = 2,
                backgroundColor = R.color.bg_color_4
            ),
            Question(
                id = 5,
                category = Category.KIM_YAPTI,
                questionText = "İlk telefonu kim icat etti?",
                options = listOf("Thomas Edison", "Alexander Graham Bell", "Nikola Tesla", "Guglielmo Marconi"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_5
            ),
            Question(
                id = 6,
                category = Category.KIM_YAPTI,
                questionText = "Elektriği kim keşfetti?",
                options = listOf("Benjamin Franklin", "Thomas Edison", "Nikola Tesla", "Michael Faraday"),
                correctAnswerIndex = 0,
                backgroundColor = R.color.bg_color_6
            ),
            Question(
                id = 7,
                category = Category.KIM_YAPTI,
                questionText = "Mona Lisa tablosunu kim yaptı?",
                options = listOf("Vincent van Gogh", "Leonardo da Vinci", "Pablo Picasso", "Michelangelo"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_7
            ),
            Question(
                id = 8,
                category = Category.KIM_YAPTI,
                questionText = "İlk ampulü kim icat etti?",
                options = listOf("Nikola Tesla", "Thomas Edison", "Alexander Graham Bell", "Benjamin Franklin"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_8
            ),
            Question(
                id = 9,
                category = Category.KIM_YAPTI,
                questionText = "Periyodik tabloyu kim oluşturdu?",
                options = listOf("Dmitri Mendeleyev", "Marie Curie", "Antoine Lavoisier", "Ernest Rutherford"),
                correctAnswerIndex = 0,
                backgroundColor = R.color.bg_color_9
            ),
            Question(
                id = 10,
                category = Category.KIM_YAPTI,
                questionText = "Türk Kurtuluş Savaşı'nı kim başlattı?",
                options = listOf("İsmet İnönü", "Mustafa Kemal Atatürk", "Kazım Karabekir", "Fevzi Çakmak"),
                correctAnswerIndex = 1,
                backgroundColor = R.color.bg_color_10
            )
        )
    }
}
