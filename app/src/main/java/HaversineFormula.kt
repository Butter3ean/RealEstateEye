import androidx.compose.runtime.MutableState
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun haversineDistance(currentCoords: MutableState<LatLng>, otherCoords: LatLng): Double {

    val lat1 = currentCoords.value.latitude
    val lon1 = currentCoords.value.longitude

    val lat2 = otherCoords.latitude
    val lon2 = otherCoords.longitude

    val earthRadius = 6371 // Radius of the Earth in kilometers

    val diffLat = Math.toRadians(lat2 - lat1)
    val diffLon = Math.toRadians(lon2 - lon1)

    val a = sin(diffLat / 2) * sin(diffLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(diffLon / 2) * sin(diffLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c

}