package com.parkhub.app.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.theme.ParkHubGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@SuppressLint("DefaultLocale")
@Composable
fun StellplatzDetailScreen(
    stellplatz: StellplatzMitDetails,
    von: Long?,
    bis: Long?,
    onBackClick: () -> Unit,
    onBuchenClick: () -> Unit
) {
    val adresse = stellplatz.adresse
    val preisProStunde = stellplatz.stellplatz.preis_stunde
    val stunden = berechneStunden(von, bis)
    val gesamtpreis = preisProStunde * stunden

    val context = LocalContext.current
    val bildResId = context.resources.getIdentifier(
        stellplatz.stellplatz.bildName,
        "drawable",
        context.packageName
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (bildResId != 0) {
                Image(
                    painter = painterResource(id = bildResId),
                    contentDescription = "Bild des Stellplatzes",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Zurück")
                }

                Row {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorit")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = "Teilen")
                    }
                }
            }

            Text(
                text = "• • • •",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                modifier = Modifier.align(Alignment.Center),
                fontSize = 28.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${adresse?.strasse ?: "Unbekannte Straße"} ${adresse?.hausnummer ?: ""}",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${adresse?.plz ?: ""} ${adresse?.ort ?: ""}",
                        fontSize = 13.sp,
                        color = Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "★ ${String.format("%.1f", stellplatz.bewertungSchnitt)}",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "(${stellplatz.anzahlBewertungen})",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = ParkHubGreen,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "Verifiziert",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Text(
                    text = "${String.format("%.2f", preisProStunde)} €/h",
                    color = ParkHubGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(modifier = Modifier.padding(vertical = 18.dp))

            DetailInfoRow(
                icon = { Icon(Icons.Outlined.Straighten, contentDescription = null) },
                title = "${cmZuMeter(stellplatz.stellplatz.laenge_cm)} × ${cmZuMeter(stellplatz.stellplatz.breite_cm)} × ${cmZuMeter(stellplatz.stellplatz.hoehe_cm)} m",
                subtitle = "Max. ${stellplatz.stellplatz.maxGewichtTonnen} t · ${stellplatz.stellplatz.geeignetFuer}"
            )

            Spacer(modifier = Modifier.height(14.dp))

            DetailInfoRow(
                icon = { Icon(Icons.Outlined.Videocam, contentDescription = null) },
                title = if (stellplatz.stellplatz.videoUeberwacht) "Videoüberwacht" else "Nicht videoüberwacht",
                subtitle = stellplatz.stellplatz.videoHinweis
            )

            Spacer(modifier = Modifier.height(14.dp))

            DetailInfoRow(
                icon = { Icon(Icons.Outlined.Key, contentDescription = null) },
                title = stellplatz.stellplatz.zugangsart,
                subtitle = stellplatz.stellplatz.zugangshinweis
            )
        }

        Surface(
            tonalElevation = 3.dp,
            shadowElevation = 3.dp
        ) {
            Button(
                onClick = onBuchenClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Buchen für ${zeitText(von)} – ${zeitText(bis)} · ${String.format("%.2f", gesamtpreis)} €",
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .padding(top = 2.dp)
        ) {
            icon()
        }

        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@SuppressLint("DefaultLocale")
private fun cmZuMeter(cm: Float): String {
    return String.format("%.1f", cm / 100f)
}

@SuppressLint("DefaultLocale")
private fun berechneStunden(von: Long?, bis: Long?): Float {
    if (von == null || bis == null || bis <= von) return 1f
    val differenzMillis = bis - von
    return differenzMillis / (1000f * 60f * 60f)
}

private fun zeitText(millis: Long?): String {
    if (millis == null) return "--:--"
    return SimpleDateFormat("HH:mm", Locale.GERMAN).format(Date(millis))
}