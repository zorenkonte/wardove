package com.app.wardove.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.ui.wardrobe.WardoveBottomBar
import com.app.wardove.ui.wardrobe.WardroveBottomRoute
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    onSelectWardrobe: () -> Unit,
    onSelectLaundry: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val datesWithWear by viewModel.datesWithWear.collectAsState()
    val itemsForDay by viewModel.itemsForSelectedDate.collectAsState()
    val today = remember { LocalDate.now() }
    var visibleMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

    Scaffold(
        containerColor = Color(0xFFF7F5F2),
        bottomBar = {
            WardoveBottomBar(
                currentRoute = WardroveBottomRoute.CALENDAR,
                onSelectWardrobe = onSelectWardrobe,
                onSelectLaundry = onSelectLaundry,
                onSelectCalendar = {}
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CalendarTopBar(
                month = visibleMonth,
                onPrev = {
                    val target = visibleMonth.minusMonths(1)
                    visibleMonth = target
                    viewModel.selectDate(adjustSelection(selectedDate, target))
                },
                onNext = {
                    val target = visibleMonth.plusMonths(1)
                    if (!target.isAfter(YearMonth.from(today))) {
                        visibleMonth = target
                        viewModel.selectDate(adjustSelection(selectedDate, target))
                    }
                },
                canGoNext = visibleMonth.isBefore(YearMonth.from(today))
            )

            WeekdayHeader()

            MonthGrid(
                month = visibleMonth,
                selectedDate = selectedDate,
                today = today,
                datesWithWear = datesWithWear,
                onSelect = viewModel::selectDate
            )

            HorizontalDivider(
                color = Color(0xFFE0DDD8),
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            SelectedDayLabel(date = selectedDate)

            Spacer(Modifier.height(12.dp))

            if (itemsForDay.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Nothing worn on this day",
                        color = Color(0xFFAAAAAA),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(itemsForDay, key = { it.id }) { item ->
                        WornItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarTopBar(
    month: YearMonth,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    canGoNext: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Calendar",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF1A1A1A)
            )
            Text(
                "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF888888)
            )
        }
        IconButton(onClick = onPrev) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = Color(0xFF1A1A1A)
            )
        }
        IconButton(onClick = onNext, enabled = canGoNext) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next month",
                tint = if (canGoNext) Color(0xFF1A1A1A) else Color(0xFFCCCCCC)
            )
        }
    }
}

private val weekdayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

@Composable
private fun WeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        weekdayLabels.forEach { label ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label,
                    fontSize = 11.sp,
                    color = Color(0xFF888888)
                )
            }
        }
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    datesWithWear: Set<LocalDate>,
    onSelect: (LocalDate) -> Unit
) {
    val firstOfMonth = month.atDay(1)
    val leadingBlanks = (firstOfMonth.dayOfWeek.sundayBasedIndex())
    val gridStart = firstOfMonth.minusDays(leadingBlanks.toLong())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        repeat(6) { weekIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { dayIndex ->
                    val date = gridStart.plusDays((weekIndex * 7 + dayIndex).toLong())
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        DayCell(
                            date = date,
                            month = month,
                            selectedDate = selectedDate,
                            today = today,
                            hasWear = date in datesWithWear,
                            onClick = { onSelect(date) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    month: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    hasWear: Boolean,
    onClick: () -> Unit
) {
    val isSelected = date == selectedDate
    val isToday = date == today
    val inMonth = YearMonth.from(date) == month
    val textColor = when {
        isSelected -> Color.White
        !inMonth -> Color(0xFFCCCCCC)
        isToday -> Color(0xFF1A1A1A)
        else -> Color(0xFF888888)
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF1A1A1A) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                date.dayOfMonth.toString(),
                fontSize = 13.sp,
                fontWeight = if (isToday && !isSelected) FontWeight.Medium else FontWeight.Normal,
                color = textColor
            )
            if (hasWear) {
                Spacer(Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF5DCAA5), CircleShape)
                )
            }
        }
    }
}

@Composable
private fun SelectedDayLabel(date: LocalDate) {
    val day = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val month = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    Text(
        text = "$day, $month ${date.dayOfMonth}",
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF1A1A1A),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun WornItemCard(item: ClothingItem) {
    Card(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = File(item.imagePath),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(Modifier.height(6.dp))
            Text(
                item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A),
                maxLines = 1
            )
            Text(
                item.category,
                fontSize = 11.sp,
                color = Color(0xFF888888),
                maxLines = 1
            )
        }
    }
}

private fun DayOfWeek.sundayBasedIndex(): Int = when (this) {
    DayOfWeek.SUNDAY -> 0
    DayOfWeek.MONDAY -> 1
    DayOfWeek.TUESDAY -> 2
    DayOfWeek.WEDNESDAY -> 3
    DayOfWeek.THURSDAY -> 4
    DayOfWeek.FRIDAY -> 5
    DayOfWeek.SATURDAY -> 6
}

private fun adjustSelection(current: LocalDate, target: YearMonth): LocalDate {
    val day = current.dayOfMonth.coerceAtMost(target.lengthOfMonth())
    val candidate = target.atDay(day)
    val today = LocalDate.now()
    return if (candidate.isAfter(today)) today else candidate
}
