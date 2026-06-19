package com.parkhub.app.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.ui.theme.ParkHubGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun BuchungsBestaetigungScreen(
    stellplatz: StellplatzMitDetails,
    von: Long?,
    bis: Long?,
    fahrzeug: FahrzeugMitStatus?,
    fahrer: FahrerMitStatus?,
    onBackClick: () -> Unit,
    onJetztBuchenClick: () -> Unit
) {
    var agbAkzeptiert by remember { mutableStateOf(true) }

    val preisProStunde = stellplatz.stellplatz.preis_stunde
    val stunden = berechneStundenBuchung(von, bis)
    val gesamtpreis = preisProStunde * stunden
    val provision = gesamtpreis * 0.18f
    val adresse = stellplatz.adresse

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Zurück")
            }

            Text(
                text = "Buchung prüfen",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${adresse?.strasse ?: ""} ${adresse?.hausnummer ?: ""}",
                    fontWeight = FontWeight.Bold
                )
                Text(text = adresse?.ort ?: "")
                Text(
                    text = "★ ${String.format("%.1f", stellplatz.bewertungSchnitt)} (${stellplatz.anzahlBewertungen})",
                    color = ParkHubGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoZeile("Zeitraum", "${datumText(von)} · ${zeitTextBuchung(von)} – ${zeitTextBuchung(bis)}")
                InfoZeile(
                    "Fahrzeug",
                    "${fahrzeug?.typ?.bezeichnung ?: "Mercedes Sprinter"} · ${fahrzeug?.fahrzeug?.kennzeichen ?: "KA-XY 1234"}"
                )
                InfoZeile(
                    "Fahrer",
                    "${fahrer?.fahrer?.vorname ?: "Max"} ${fahrer?.fahrer?.nachname ?: "Müller"}"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Preisaufschlüsselung",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoZeile(
                    "${String.format("%.0f", stunden)} Std × ${String.format("%.2f", preisProStunde)} €",
                    "${String.format("%.2f", gesamtpreis)} €"
                )
                InfoZeile(
                    "davon Plattformprovision (18 %)",
                    "${String.format("%.2f", provision)} €"
                )

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                InfoZeile(
                    "Gesamtbetrag",
                    "${String.format("%.2f", gesamtpreis)} €",
                    bold = true
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Outlined.VerifiedUser, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inkl. Versicherungsschutz bis 5.000 € pro Buchung.",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Checkbox(
                checked = agbAkzeptiert,
                onCheckedChange = { agbAkzeptiert = it }
            )
            Text(text = "Stornobedingungen akzeptiert")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onJetztBuchenClick,
            enabled = agbAkzeptiert,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text("Jetzt buchen & zahlen")
        }
    }
}

@Composable
private fun InfoZeile(
    label: String,
    value: String,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(
            text = value,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@SuppressLint("DefaultLocale")
private fun berechneStundenBuchung(von: Long?, bis: Long?): Float {
    if (von == null || bis == null || bis <= von) return 1f
    return (bis - von) / (1000f * 60f * 60f)
}

private fun zeitTextBuchung(millis: Long?): String {
    if (millis == null) return "--:--"
    return SimpleDateFormat("HH:mm", Locale.GERMAN).format(Date(millis))
}

private fun datumText(millis: Long?): String {
    if (millis == null) return "--"
    return SimpleDateFormat("d. MMM", Locale.GERMAN).format(Date(millis))
}