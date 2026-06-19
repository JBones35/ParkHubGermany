package com.parkhub.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.parkhub.app.ui.components.suche.aufMitternachtSetzen
import com.parkhub.app.ui.components.suche.sortierOptionen
import java.util.Calendar

/**
 * Hält den gesamten lokalen UI-Zustand der Stellplatzsuche
 * (Ort, Datum, Uhrzeiten, Ansicht, Dialog-Sichtbarkeiten) sowie
 * die daraus abgeleiteten Werte [sucheVollstaendig], [suchVon]
 * und [suchBis].
 *
 * Wird die Suche um weitere Eingabefelder erweitert, gehört der
 * dazugehörige State hierher - nicht in SucheScreen.
 */
@Stable
class SucheUiState(
    initialDatum: String,
    initialDatumMillis: Long?,
    initialStartHour: Int
) {
    // ===== ORT =====
    // ortLat/ortLng bleiben null, bis der Nutzer aktiv einen Ort
    // ausgewählt hat (manuell aus der Vorschlagsliste oder per
    // automatischer Standorterkennung in OrtSucheField).
    var ort by mutableStateOf("")
    var ortLat by mutableStateOf<Double?>(null)
    var ortLng by mutableStateOf<Double?>(null)

    // ===== DATUM =====
    var datum by mutableStateOf(initialDatum)
    var datumMillis by mutableStateOf(initialDatumMillis)

    // ===== UHRZEIT =====
    // Startzeit ist initial auf die nächste volle Stunde
    // aufgerundet, Endzeit eine Stunde später (siehe
    // rememberSucheUiState).
    var startHour by mutableStateOf(initialStartHour)
    var startMinute by mutableStateOf(0)
    var endHour by mutableStateOf((initialStartHour + 1) % 24)
    var endMinute by mutableStateOf(0)

    var uhrzeitStart by mutableStateOf("%02d:%02d".format(initialStartHour, 0))
    var uhrzeitEnd by mutableStateOf("%02d:%02d".format((initialStartHour + 1) % 24, 0))

    // ===== SONSTIGER UI-ZUSTAND =====
    var selectedView by mutableStateOf(0) // 0 = Karte, 1 = Liste
    var dropdownExpanded by mutableStateOf(false)
    var selectedFahrzeugTyp by mutableStateOf("Mercedes Sprinter")

    var showDatePicker by mutableStateOf(false)
    var showTimePickerStart by mutableStateOf(false)
    var showTimePickerEnd by mutableStateOf(false)
    var showSortieren by mutableStateOf(false)
    var showFilterDialog by mutableStateOf(false)

    var selectedSortierung by mutableStateOf(sortierOptionen[0])

    var showFehler by mutableStateOf(false)
    var showFehlerMessage by mutableStateOf("")

    /**
     * Die Suche gilt erst als vollständig, wenn Ort, Datum und beide
     * Uhrzeiten gesetzt sind. Solange das nicht zutrifft, zeigt der
     * Screen nur einen Hinweistext statt Tabs, Karte oder Liste.
     */
    val sucheVollstaendig: Boolean
        get() = ortLat != null && ortLng != null &&
                datumMillis != null && uhrzeitStart.isNotEmpty() && uhrzeitEnd.isNotEmpty()

    /** Kombiniert das gewählte Datum mit der Startzeit zu einem Timestamp. */
    val suchVon: Long?
        get() = datumMillis?.let { basis ->
            Calendar.getInstance().apply {
                timeInMillis = basis
                set(Calendar.HOUR_OF_DAY, startHour)
                set(Calendar.MINUTE, startMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

    /** Kombiniert das gewählte Datum mit der Endzeit, analog zu [suchVon]. */
    val suchBis: Long?
        get() = datumMillis?.let { basis ->
            Calendar.getInstance().apply {
                timeInMillis = basis
                set(Calendar.HOUR_OF_DAY, endHour)
                set(Calendar.MINUTE, endMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }

    /** Zeigt die Fehler-Snackbar mit der übergebenen Nachricht an. */
    fun zeigeFehler(nachricht: String) {
        showFehler = true
        showFehlerMessage = nachricht
    }
}

/**
 * Erstellt und merkt sich ein [SucheUiState] mit sinnvollen
 * Startwerten: Datum = heute, Startzeit = nächste volle Stunde,
 * Endzeit = eine Stunde danach.
 */
@Composable
fun rememberSucheUiState(): SucheUiState {
    return remember {
        val initialDatum = java.text.SimpleDateFormat("d. MMM", java.util.Locale.GERMAN)
            .format(java.util.Date())
        val initialDatumMillis = aufMitternachtSetzen(System.currentTimeMillis())

        // Startzeit auf die nächste volle Stunde aufrunden, z. B.
        // 14:23 Uhr -> Start 15:00 Uhr. Steht die Uhr bereits exakt
        // auf einer vollen Stunde, bleibt sie unverändert.
        val jetzt = Calendar.getInstance()
        if (jetzt.get(Calendar.MINUTE) > 0) {
            jetzt.add(Calendar.HOUR_OF_DAY, 1)
        }
        val initialStartHour = jetzt.get(Calendar.HOUR_OF_DAY)

        SucheUiState(
            initialDatum = initialDatum,
            initialDatumMillis = initialDatumMillis,
            initialStartHour = initialStartHour
        )
    }
}