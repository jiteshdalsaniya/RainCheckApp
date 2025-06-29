package com.jd.raincheckapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jd.raincheckapp.presentation.viewmodel.WeatherUiState
import com.jd.raincheckapp.presentation.viewmodel.WeatherViewModel
import com.jd.raincheckapp.ui.theme.LightBlue
import com.jd.raincheckapp.ui.theme.Neutral100
import com.jd.raincheckapp.ui.theme.Neutral200
import com.jd.raincheckapp.ui.theme.Neutral600
import com.jd.raincheckapp.ui.theme.Primary100
import com.jd.raincheckapp.ui.theme.Primary200
import com.jd.raincheckapp.ui.theme.Primary400
import com.jd.raincheckapp.ui.theme.Primary500
import com.jd.raincheckapp.ui.theme.Primary600
import com.jd.raincheckapp.ui.theme.RainCheckTheme
import com.jd.raincheckapp.ui.theme.Secondary500
import com.jd.raincheckapp.ui.theme.Secondary600
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.jd.raincheckapp.data.model.ForecastItem

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RainCheckTheme {
                RainCheckApp()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RainCheckApp(
    viewModel: WeatherViewModel = hiltViewModel(),
    navHostController: NavHostController = rememberNavController()
) {

    val uiState by viewModel.uiState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val locationName by viewModel.locationName.collectAsState()

    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var showMainScreen by remember { mutableStateOf(false) }
    var showForecastScreen by remember { mutableStateOf(false) }

    // Request permissions when the app starts
    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            viewModel.fetchCurrentLocation()
        } else {
            // Handle scenario where permissions are not granted, e.g., show a message
//            viewModel.uiState.value = WeatherUiState.Error("Location permissions are required for this app.")
        }
    }

    NavHost(navController = navHostController, startDestination = "picker") {

        composable("picker") {
            PickerScreen(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.setSelectedDate(it) },
                onStartRainCheck = {
                    navHostController.navigate("main")
                },
                onFutureForecast = {
                    navHostController.navigate("forecast")
                },
                isLoading = uiState is WeatherUiState.LoadingLocation || uiState is WeatherUiState.LoadingWeather
            )
        }

        composable("main") {
            MainScreen(
                uiState = uiState,
                selectedDate = selectedDate,
                locationName = locationName,
                onBack = { navHostController.popBackStack() },
                onRefresh = {
                    val location = viewModel.currentLocation.value
                    if (location != null) {
                        viewModel.fetchWeatherForecast(
                            location.latitude,
                            location.longitude,
                            selectedDate
                        )
                    }
                },
                formatDateDisplay = viewModel::formatDateDisplay
            )
        }

        composable("forecast") {
            SevenDayForecastScreen(
                viewModel = viewModel,
                onBack = { navHostController.popBackStack() }
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral200)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 375.dp) // max-width: 375px
                .align(Alignment.Center)
                .shadow(
                    30.dp,
                    ambientColor = Color.Black.copy(alpha = 0.3f),
                    spotColor = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(0.dp)
                )
                .clip(RoundedCornerShape(0.dp)) // No explicit border-radius in body, but for inner mobile-screen. Adjust if needed.
                .background(Neutral100)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = Triple(showMainScreen, showForecastScreen, selectedDate),
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }, label = "Screen Transition"
            ) { (main, forecast, _) ->
                when {
                    forecast -> {
                        SevenDayForecastScreen(
                            viewModel = viewModel,
                            onBack = { showForecastScreen = false }
                        )
                    }

                    main -> {
                        MainScreen(
                            uiState = uiState,
                            selectedDate = selectedDate,
                            locationName = locationName,
                            onBack = { showMainScreen = false },
                            onRefresh = {
                                val location = viewModel.currentLocation.value
                                if (location != null) {
                                    viewModel.fetchWeatherForecast(
                                        location.latitude,
                                        location.longitude,
                                        selectedDate
                                    )
                                }
                            },
                            formatDateDisplay = viewModel::formatDateDisplay
                        )
                    }

                    else -> {
                        PickerScreen(
                            selectedDate = selectedDate,
                            onDateSelected = { viewModel.setSelectedDate(it) },
                            onStartRainCheck = {
                                showMainScreen = true
                            },
                            onFutureForecast = {
                                showForecastScreen = true
                            },
                            isLoading = uiState is WeatherUiState.LoadingLocation || uiState is WeatherUiState.LoadingWeather
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PickerScreen(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onStartRainCheck: () -> Unit,
    onFutureForecast: () -> Unit,
    isLoading: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FloatingBubbles")
    val scrollState = rememberScrollState()
    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(6000, easing = EaseInOut), RepeatMode.Reverse),
        label = "Float1"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            tween(6000, delayMillis = 2000, easing = EaseInOut),
            RepeatMode.Reverse
        ), label = "Float2"
    )
    val floatOffset3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            tween(6000, delayMillis = 4000, easing = EaseInOut),
            RepeatMode.Reverse
        ), label = "Float3"
    )
    val floatOffset4 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            tween(6000, delayMillis = 1000, easing = EaseInOut),
            RepeatMode.Reverse
        ), label = "Float4"
    )

    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "Pulse1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "PulseAlpha1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            tween(2000, delayMillis = 500, easing = LinearEasing),
            RepeatMode.Restart
        ), label = "Pulse2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            tween(2000, delayMillis = 500, easing = LinearEasing),
            RepeatMode.Restart
        ), label = "PulseAlpha2"
    )

    val bounceTranslation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOut), RepeatMode.Reverse),
        label = "Bounce"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Primary400, Secondary600),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(
                        Float.POSITIVE_INFINITY,
                        Float.POSITIVE_INFINITY
                    )
                )
            )
            .statusBarsPadding()
    ) {
        // Floating bubbles
        Box(
            modifier = Modifier
                .offset(x = 32.dp, y = 80.dp + floatOffset1.dp)
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = (-48).dp, y = 160.dp + floatOffset2.dp)
                .align(Alignment.TopEnd)
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = 64.dp, y = 240.dp + floatOffset3.dp)
                .size(24.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = (-32).dp, y = (-160).dp + floatOffset4.dp)
                .align(Alignment.BottomEnd)
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp, vertical = 64.dp)
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(16.dp)) // Top padding

            // RainCheck Logo and Icon
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .graphicsLayer { translationY = bounceTranslation }
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cloud, // cloud-rain equivalent
                    contentDescription = "Cloud Rain",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = pulseScale1
                            scaleY = pulseScale1
                            alpha = pulseAlpha1
                        }
                        .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = pulseScale2
                            scaleY = pulseScale2
                            alpha = pulseAlpha2
                        }
                        .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                )
            }

            Spacer(Modifier.height(48.dp))

            // Title and Description
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Rain Check",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Your friendly weather companion.\nNever get caught in the rain again! 🌧️",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(48.dp))

            // Date Selection
            DateSelectionCard(selectedDate = selectedDate, onDateSelected = onDateSelected)

            Spacer(Modifier.height(32.dp))

            // Feature Cards
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FeatureCard(
                    icon = Icons.Default.CalendarMonth,
                    title = "Future Forecast",
                    subtitle = "Check weather up to 7 days ahead",
                    onClick = onFutureForecast
                )
                FeatureCard(
                    icon = Icons.Default.Umbrella,
                    title = "Rain Alerts",
                    subtitle = "Know exactly when to bring an umbrella",
                    onClick = {}
                )
            }

            Spacer(Modifier.height(48.dp))

            // Start Button
            Button(
                onClick = onStartRainCheck,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(
                        16.dp,
                        RoundedCornerShape(24.dp),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Neutral100,
                    contentColor = Primary600
                ),
                contentPadding = PaddingValues(horizontal = 24.dp),
                enabled = !isLoading // Disable button while loading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Primary600, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Text("Loading...", style = MaterialTheme.typography.titleLarge)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Do I need a Rain Check?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Arrow Right",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Tap to start checking the weather",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(Modifier.height(16.dp)) // Bottom padding
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionCard(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val todayMillis = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endMillis =
        today.plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli(),
        initialDisplayedMonthMillis = todayMillis,
        yearRange = today.year..(today.year + 1),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in todayMillis..endMillis
            }
        }
    )

    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "📅 Pick a Date",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "When do you want to check?",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = formattedDate,
                onValueChange = { /* Read-only */ },
                readOnly = true,
                label = { Text("", color = Color.White.copy(alpha = 0.7f)) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.3f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.3f),
                    disabledContainerColor = Color.White.copy(alpha = 0.3f),
                    focusedBorderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledTextColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.dp, Color.Transparent, RoundedCornerShape(16.dp)),
                trailingIcon = {
                    IconButton(onClick = { showDatePickerDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Pick Date",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    }

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(newDate)
                    }
                    showDatePickerDialog = false
                }) {
                    Text("OK", color = Primary600)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel", color = Primary600)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    todayContentColor = Primary600,
                    todayDateBorderColor = Primary400,
                    selectedDayContainerColor = Primary400,
                    selectedDayContentColor = Color.White
                )
            )
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .defaultMinSize(minHeight = 100.dp) // Ensures a minimum height
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),

        ) {
        Row(
            modifier = Modifier
                .padding(
                    vertical = 20.dp,
                    horizontal = 16.dp
                ), // Adjusted padding for better spacing
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp)) // Adds a small space between title and subtitle
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    uiState: WeatherUiState,
    selectedDate: LocalDate,
    locationName: String,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    formatDateDisplay: (LocalDate) -> String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FloatingBubblesMain")
    val scrollState = rememberScrollState()
    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(6000, easing = EaseInOut), RepeatMode.Reverse),
        label = "Float1Main"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            tween(6000, delayMillis = 2000, easing = EaseInOut),
            RepeatMode.Reverse
        ), label = "Float2Main"
    )
    val floatOffset3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            tween(6000, delayMillis = 4000, easing = EaseInOut),
            RepeatMode.Reverse
        ), label = "Float3Main"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Primary400, Secondary600),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(
                        Float.POSITIVE_INFINITY,
                        Float.POSITIVE_INFINITY
                    )
                )
            )
            .statusBarsPadding()
    ) {
        // Floating bubbles
        Box(
            modifier = Modifier
                .offset(x = 16.dp, y = 80.dp + floatOffset1.dp)
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = (-32).dp, y = 128.dp + floatOffset2.dp)
                .align(Alignment.TopEnd)
                .size(24.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
        )
        Box(
            modifier = Modifier
                .offset(x = 48.dp, y = 192.dp + floatOffset3.dp)
                .size(16.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp)) // Top padding

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(8.dp) // Smaller padding inside the button
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Rain Check",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your weather companion",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                IconButton(
                    onClick = onRefresh, // ← Refresh logic from parent
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Location Bubble
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Map Pin",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = locationName,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Auto-detected location",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Rain Prediction
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        16.dp,
                        RoundedCornerShape(24.dp),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    ),
                colors = CardDefaults.cardColors(containerColor = Neutral100),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Will it rain on ${formatDateDisplay(selectedDate).lowercase()}?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Neutral600
                    )
                    Spacer(Modifier.height(16.dp))

                    when (uiState) {
                        is WeatherUiState.LoadingWeather, WeatherUiState.LoadingLocation -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Primary600
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("Fetching forecast...", color = Neutral600)
                        }

                        is WeatherUiState.Success -> {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (uiState.willRain) "YES!" else "NO!",
                                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp),
                                    color = if (uiState.willRain) Primary600 else Secondary500, // Adjust colors as needed
                                    fontWeight = FontWeight.ExtraBold
                                )
                                if (uiState.willRain) {
                                    Icon(
                                        imageVector = Icons.Rounded.WaterDrop,
                                        contentDescription = "Water Drop",
                                        tint = LightBlue,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 24.dp, y = (-10).dp)
                                            .padding(100.dp, 0.dp, 0.dp, 0.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(24.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(containerColor = Primary100)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Rain Probability",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Primary600,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        LinearProgressIndicator(
                                            progress = uiState.rainProbability / 100f,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = Primary500,
                                            trackColor = Primary200
                                        )
                                        Text(
                                            text = "${uiState.rainProbability}%",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Primary600,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = if (uiState.willRain) "🌂 Don't forget your umbrella!" else "Stay dry! Enjoy the day!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Neutral600
                            )
                        }

                        is WeatherUiState.Error -> {
                            Text(
                                text = "Error: ${uiState.message}",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(8.dp))
                            // Potentially add a retry button
                        }

                        is WeatherUiState.NoForecast -> {
                            Text(text = uiState.message, color = Neutral600)
                        }

                        WeatherUiState.Initial -> { /* Handled by loading states */
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Weather Details (Temperature, Humidity)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val successState = uiState as? WeatherUiState.Success
                WeatherDetailCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🌡️",
                    value = successState?.temperature?.roundToInt()?.toString() ?: "--",
                    unit = "°C",
                    label = "Temperature"
                )
                WeatherDetailCard(
                    modifier = Modifier.weight(1f),
                    emoji = "💧",
                    value = successState?.humidity?.toString() ?: "--",
                    unit = "%",
                    label = "Humidity"
                )
            }

            Spacer(Modifier.height(24.dp))

            // Weather Description
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "⛅", fontSize = 32.sp) // Weather icon based on description
                    Column {
                        Text(
                            text = (uiState as? WeatherUiState.Success)?.weatherDescription?.capitalizeWords()
                                ?: "Loading...",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
//                        Text(text = "", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Updated: ${(uiState as? WeatherUiState.Success)?.lastUpdated ?: "N/A"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Powered by OpenWeatherMap",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
            Spacer(Modifier.height(16.dp)) // Bottom padding
        }
    }
}

@Composable
fun WeatherDetailCard(
    modifier: Modifier = Modifier,
    emoji: String,
    value: String,
    unit: String,
    label: String
) {
    Card(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun SevenDayForecastScreen(viewModel: WeatherViewModel, onBack: () -> Unit) {
    val forecastList by viewModel.forecastList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchSevenDayForecast()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(listOf(Primary400, Secondary600))
            )
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        Column {
            // Header with Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "7-Day Forecast",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(48.dp)) // for symmetry
            }

            Spacer(Modifier.height(16.dp))

            if (forecastList.isEmpty()) {
                CircularProgressIndicator(color = Color.White)
            } else {
                forecastList.forEach { forecastItem ->
                    ForecastItemCard(forecastItem)
                }
            }
        }
    }
}

@Composable
fun ForecastItemCard(forecastItem: ForecastItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically // centers all items vertically
        ) {
            Text(
                text = Instant.ofEpochSecond(forecastItem.dt).atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ofPattern("EEE, MMM d")),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${forecastItem.main.temp}°C, ${forecastItem.weather.firstOrNull()?.main ?: "N/A"}",
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Box( // Centers the weather icon visually within the row
                modifier = Modifier
                    .size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                WeatherIconWithFallback(forecastItem.weather.firstOrNull()?.icon)
            }
        }
    }
}

@Composable
fun WeatherIconWithFallback(
    iconCode: String?,
    modifier: Modifier = Modifier.size(36.dp)
) {
    if (iconCode.isNullOrBlank()) {
        Icon(
            imageVector = Icons.Default.Cloud,
            contentDescription = "Default Weather Icon",
            tint = Color.Gray,
            modifier = modifier
        )
    } else {
        val context = LocalContext.current
        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

        Box(contentAlignment = Alignment.Center, modifier = modifier) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(iconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Weather Icon",
                modifier = modifier,
                onError = {
                    // Optional: You can log or show a toast here
                },
                placeholder = rememberVectorPainter(Icons.Default.Cloud),
                error = rememberVectorPainter(Icons.Default.Error)
            )
        }
    }
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RainCheckTheme {
        // You'll need to mock your ViewModel or create a simplified state for preview
        // This is a basic preview and won't show full functionality
        // As a placeholder, show PickerScreen
        PickerScreen(
            selectedDate = LocalDate.now().plusDays(1),
            onDateSelected = {},
            onStartRainCheck = {},
            onFutureForecast = {},
            isLoading = false
        )
    }
}