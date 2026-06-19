package com.parkhub.app.ui.components.suche

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import java.util.Calendar

/**
 * Sortier-Option für das "Sortieren"-Dropdown im Ergebnis-Header der
 * Stellplatzsuche. [label] ist der angezeigte Text und entscheidet
 * gleichzeitig über die eigentliche Sortierlogik im Screen (per
 * exaktem String-Vergleich). [aufsteigend] wird nur für das
 * Pfeil-Symbol (↑/↓) im Dropdown-Menü genutzt.
 */
data class Sortierung(val label: String, val aufsteigend: Boolean)

/**
 * Feste Liste aller Sortier-Optionen, die im Dropdown angeboten werden.
 */
val sortierOptionen = listOf(
    Sortierung("Preis aufsteigend", true),
    Sortierung("Preis absteigend", false),
    Sortierung("Entfernung aufsteigend", true),
    Sortierung("Entfernung absteigend", false)
)

/**
 * Setzt einen beliebigen Zeitpunkt auf 00:00:00.000 desselben Tages.
 *
 * Wird gebraucht, damit zwei Zeitpunkte am selben Kalendertag, aber
 * mit unterschiedlicher Uhrzeit, als "gleicher Tag" erkannt werden -
 * z. B. um zu prüfen, ob das gewählte Suchdatum "heute" ist, unabhängig
 * davon, wann genau heute der Vergleich ausgeführt wird.
 */
fun aufMitternachtSetzen(millis: Long): Long {
    return Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

/**
 * Dialog zur Datumsauswahl mit eingebauter Vergangenheits-Prüfung.
 *
 * Wrappt [SucheDatePickerDialog] und verhindert, dass ein Datum vor
 * dem heutigen Tag bestätigt werden kann. Bei einem ungültigen Datum
 * wird stattdessen [onFehler] mit einer passenden Nutzermeldung
 * aufgerufen, statt das Datum zu übernehmen.
 *
 * @param onDatumGewaehlt liefert sowohl den formatierten Anzeigetext
 *   (z. B. "19. Juni") als auch den Mitternacht-normalisierten
 *   Timestamp für interne Berechnungen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SucheDatumDialog(
    show: Boolean,
    state: DatePickerState,
    onDismiss: () -> Unit,
    onDatumGewaehlt: (text: String, millis: Long) -> Unit,
    onFehler: (nachricht: String) -> Unit
) {
    SucheDatePickerDialog(
        show = show,
        state = state,
        onDismiss = onDismiss,
        onConfirm = { millis ->
            val heuteMitternacht = aufMitternachtSetzen(System.currentTimeMillis())
            val gewaehltesDatum = aufMitternachtSetzen(millis)

            if (gewaehltesDatum < heuteMitternacht) {
                onDismiss()
                onFehler("Das gewählte Datum muss heute oder in der Zukunft liegen.")
            } else {
                val sdf = java.text.SimpleDateFormat("d. MMM", java.util.Locale.GERMAN)
                onDatumGewaehlt(sdf.format(java.util.Date(millis)), gewaehltesDatum)
                onDismiss()
            }
        }
    )
}

/**
 * Dialog zur Auswahl der Startzeit mit zwei Validierungsregeln:
 *
 * 1. Liegt das gewählte Datum heute, darf die Startzeit nicht in der
 *    Vergangenheit liegen (Vergleich gegen die aktuelle Uhrzeit).
 * 2. Die Startzeit muss vor der bereits gesetzten Endzeit liegen,
 *    falls diese schon existiert.
 *
 * Bei Verstoß wird [onFehler] mit einer passenden Meldung aufgerufen,
 * statt die neue Zeit über [onUebernehmen] zu übernehmen.
 */
@Composable
fun SucheStartzeitDialog(
    show: Boolean,
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    uhrzeitEndGesetzt: Boolean,
    datumMillis: Long?,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    onUebernehmen: (text: String) -> Unit,
    onFehler: (nachricht: String) -> Unit
) {
    SucheTimePickerDialog(
        show = show,
        title = "Startzeit wählen",
        selectedHour = startHour,
        selectedMinute = startMinute,
        onHourSelected = onHourSelected,
        onMinuteSelected = onMinuteSelected,
        onDismiss = onDismiss,
        onConfirm = {
            val startMinuten = startHour * 60 + startMinute
            val endMinuten = endHour * 60 + endMinute

            val istHeute = datumMillis != null &&
                    aufMitternachtSetzen(datumMillis) == aufMitternachtSetzen(System.currentTimeMillis())

            val jetztMinuten = run {
                val cal = Calendar.getInstance()
                cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
            }

            if (istHeute && startMinuten < jetztMinuten) {
                onFehler("Die Startzeit darf nicht in der Vergangenheit liegen.")
            } else if (!uhrzeitEndGesetzt || startMinuten < endMinuten) {
                onUebernehmen("%02d:%02d".format(startHour, startMinute))
            } else {
                onFehler("Die Startzeit muss vor der Endzeit liegen.")
            }
        }
    )
}

/**
 * Dialog zur Auswahl der Endzeit. Analog zu [SucheStartzeitDialog],
 * mit umgekehrter Beziehung zwischen Start- und Endzeit (die Endzeit
 * muss nach der Startzeit liegen statt davor).
 */
@Composable
fun SucheEndzeitDialog(
    show: Boolean,
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    uhrzeitStartGesetzt: Boolean,
    datumMillis: Long?,
    onHourSelected: (Int) -> Unit,
    onMinuteSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    onUebernehmen: (text: String) -> Unit,
    onFehler: (nachricht: String) -> Unit
) {
    SucheTimePickerDialog(
        show = show,
        title = "Endzeit wählen",
        selectedHour = endHour,
        selectedMinute = endMinute,
        onHourSelected = onHourSelected,
        onMinuteSelected = onMinuteSelected,
        onDismiss = onDismiss,
        onConfirm = {
            val startMinuten = startHour * 60 + startMinute
            val endMinuten = endHour * 60 + endMinute

            val istHeute = datumMillis != null &&
                    aufMitternachtSetzen(datumMillis) == aufMitternachtSetzen(System.currentTimeMillis())

            val jetztMinuten = run {
                val cal = Calendar.getInstance()
                cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
            }

            if (istHeute && endMinuten < jetztMinuten) {
                onFehler("Die Endzeit darf nicht in der Vergangenheit liegen.")
            } else if (!uhrzeitStartGesetzt || endMinuten > startMinuten) {
                onUebernehmen("%02d:%02d".format(endHour, endMinute))
            } else {
                onFehler("Die Endzeit muss nach der Startzeit liegen.")
            }
        }
    )
}