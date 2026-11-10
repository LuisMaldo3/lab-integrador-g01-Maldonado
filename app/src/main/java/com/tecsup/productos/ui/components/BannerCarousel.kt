
package com.tecsup.productos.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.productos.R
import kotlinx.coroutines.delay

private data class Promo(
    val title: String,
    val subtitle: String,
    val imageRes: Int,
    val colors: List<Color>
)

private val promos = listOf(
    Promo(
        title = "Descubre el mundo iPhone",
        subtitle = "Diseño, potencia e innovación",
        imageRes = R.drawable.iphone_pro_realista,
        colors = listOf(
            Color(0xFF071A46),
            Color(0xFF1259CB),
            Color(0xFF437CE8)
        )
    ),
    Promo(
        title = "Explora Samsung Galaxy",
        subtitle = "Tecnología para cada momento",
        imageRes = R.drawable.samsung_galaxy_realista,
        colors = listOf(
            Color(0xFF151043),
            Color(0xFF4A23B9),
            Color(0xFF2275DB)
        )
    ),
    Promo(
        title = "Tu próximo smartphone",
        subtitle = "Encuentra tu modelo ideal",
        imageRes = R.drawable.banner_dos_telefonos,
        colors = listOf(
            Color(0xFF073B90),
            Color(0xFF1169E8),
            Color(0xFF18A4ED)
        )
    )
)

@Composable
fun BannerCarousel(
    onExplorar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        pageCount = { promos.size }
    )

    // Avance automático cada 5 segundos
    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)

            if (!pagerState.isScrollInProgress) {
                val nextPage =
                    (pagerState.currentPage + 1) % promos.size

                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {

        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp
        ) { page ->

            // Cada página obtiene una imagen diferente
            val promo = promos[page]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(promo.colors)
                    )
            ) {

                // Decoración circular suave del fondo
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 45.dp, y = 65.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(alpha = 0.08f)
                        )
                )

                // Imagen diferente para cada banner
                Image(
                    painter = painterResource(promo.imageRes),
                    contentDescription = promo.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .fillMaxWidth(0.61f)
                        .fillMaxHeight(0.95f)
                        .offset(x = 8.dp, y = 10.dp)
                )

                // Títulos y botón
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth(0.57f)
                        .padding(start = 18.dp)
                ) {

                    Text(
                        text = promo.title,
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 3
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = promo.subtitle,
                        color = Color(0xFFE5F1FF),
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = 3
                    )

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFFFF5267))
                            .clickable { onExplorar() }
                            .padding(
                                horizontal = 16.dp,
                                vertical = 11.dp
                            )
                    ) {
                        Text(
                            text = "Explorar ahora →",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(11.dp))

        // Indicadores animados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(promos.size) { index ->

                val selected =
                    pagerState.currentPage == index

                val indicatorWidth by animateDpAsState(
                    targetValue =
                        if (selected) 22.dp else 7.dp,
                    label = "IndicadorBanner"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(indicatorWidth)
                        .height(7.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected)
                                Color(0xFF1267F5)
                            else
                                Color(0xFFB4C6EA)
                        )
                )
            }
        }
    }
}
