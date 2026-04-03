package edu.nd.pmcburne.hello

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import edu.nd.pmcburne.hello.ui.theme.UVABlue
import edu.nd.pmcburne.hello.ui.theme.UVAOrange
import edu.nd.pmcburne.hello.ui.theme.HelloTheme
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloTheme() {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MapViewModel = viewModel()) {
    val tags by viewModel.allTags.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val locations by viewModel.filtered.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<PlacemarkEntity?>(null) }

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(38.0336, -78.5080), 15f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "UVA Campus Map",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            text = "University of Virginia",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = UVABlue)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // Filter bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UVABlue.copy(alpha = 0.07f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter by tag:",
                    fontSize = 14.sp,
                    color = UVABlue,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 10.dp)
                )

                Box {
                    OutlinedButton(
                        onClick = { expanded = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = UVABlue),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, UVABlue)
                    ) {
                        Text(text = selectedTag, fontWeight = FontWeight.SemiBold)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.heightIn(max = 300.dp)
                    ) {
                        tags.forEach { tag ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = tag,
                                        color = if (tag == selectedTag) UVAOrange else Color.Unspecified,
                                        fontWeight = if (tag == selectedTag) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    viewModel.selectedTag.value = tag
                                    selectedPlace = null
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = UVAOrange
                ) {
                    Text(
                        text = "${locations.size} location${if (locations.size != 1) "s" else ""}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Map + info card overlay
            Box(modifier = Modifier.fillMaxSize()) {

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraState,
                    onMapClick = { selectedPlace = null }
                ) {
                    locations.forEach { place ->
                        val markerState = remember(place.id) {
                            MarkerState(position = LatLng(place.latitude, place.longitude))
                        }
                        Marker(
                            state = markerState,
                            title = place.name,
                            icon = BitmapDescriptorFactory.defaultMarker(
                                if (selectedPlace?.id == place.id)
                                    BitmapDescriptorFactory.HUE_ORANGE
                                else
                                    BitmapDescriptorFactory.HUE_AZURE
                            ),
                            onClick = {
                                selectedPlace = place
                                true
                            }
                        )
                    }
                }

                // Info card shown when a marker is selected
                selectedPlace?.let { place ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = place.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = UVABlue,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { selectedPlace = null }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = Color.Gray
                                    )
                                }
                            }
                            Divider(color = UVAOrange, thickness = 1.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = place.description,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }
        }
    }
}