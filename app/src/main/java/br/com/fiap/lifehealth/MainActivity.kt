package br.com.fiap.lifehealth

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener


class MainActivity : ComponentActivity() {

    private val list = mutableListOf<CarouselItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val carousel: ImageCarousel = findViewById(R.id.carousel)
        list.add(
            CarouselItem(
                R.drawable.image3,
               "Receba orientações e auxílio no monitoramento e ajuda no controle de sua saúde ou de doenças cronicas."
            )
        )
        list.add(
            CarouselItem(
                R.drawable.image2,
                "Receba alertas, localização de unidades médicas gratuitas e pagas para ajudar no cuidado de sua saude ou no tratamento de doenças cronicas."
            )
        )
        list.add(
            CarouselItem(
                R.drawable.image1,
                "Seja monitorado por profissionais, compartilhe informações com médicos e profissionais da saúde."
            )
        )
        carousel.carouselListener = object : CarouselListener {
            override fun onClick(position: Int, carouselItem: CarouselItem) {
                Log.d("", "onClick: ${carouselItem.caption}")
            }

            override fun onLongClick(position: Int, dataObject: CarouselItem) {
                // ...
            }
        }
        carousel.addData(list)
    }
}

