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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.app.wardove.ui.components.LargeTitleHeader
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.app.wardove.ui.components.ClothingImage
import com.app.wardove.ui.components.Dot
import com.app.wardove.R
import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.ui.theme.StatusClean
import com.app.wardove.ui.theme.textHint
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen(
    onOpenDrawer: () -> Unit = {},
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val datesWithWear by viewModel.datesWithWear.collectAsState()
    val itemsForDay by viewModel.itemsForSelectedDate.collectAsState()
    val today = remember { LocalDate.now() }
    val currentMonth = remember(today) { YearMonth.from(today) }

    // The current month is the LAST page, so the user can never page into the
    // future. ~100 years of history back is effectively unbounded.
    val pageCount = remember { MONTHS_BACK + 1 }
    fun pageToMonth(page: Int): YearMonth =
        currentMonth.minusMonths((pageCount - 1 - page).toLong())
    fun monthToPage(month: YearMonth): Int =
        pageCount - 1 - ChronoUnit.MONTHS.between(month, currentMonth).toInt()

    val pagerState = rememberPagerState(
        initialPage = monthToPage(YearMonth.from(selectedDate)),
        pageCount = { pageCount }
    )
    val scope = rememberCoroutineScope()
    val visibleMonth = pageToMonth(pagerState.currentPage)

    // When a swipe settles on a new month, move the selection into it (mirrors
    // the old chevron behaviour). Guard so tapping a day within the month — which
    // doesn't change the page — doesn't get its selection overwritten.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            val settledMonth = pageToMonth(page)
            val current = viewModel.selectedDate.value
            if (YearMonth.from(current) != settledMonth) {
                viewModel.selectDate(adjustSelection(current, settledMonth))
            }
        }
    }

    val goToPrevMonth = {
        scope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0)) }
        Unit
    }
    val goToNextMonth = {
        scope.launch { pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(pageCount - 1)) }
        Unit
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LargeTitleHeader(
                title = stringResource(R.string.nav_calendar),
                onOpenDrawer = onOpenDrawer,
                subtitle = "${visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${visibleMonth.year}",
                actions = {
                    val canGoNext = visibleMonth.isBefore(YearMonth.from(today))
                    val isOnToday = visibleMonth == YearMonth.from(today) && selectedDate == today
                    IconButton(onClick = goToPrevMonth) {
                        Icon(
                            Lucide.ChevronLeft,
                            contentDescription = stringResource(R.string.calendar_prev_month),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isOnToday) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.primary
                            )
                            .clickable(enabled = !isOnToday) {
                                viewModel.selectDate(today)
                                scope.launch { pagerState.animateScrollToPage(pageCount - 1) }
                            }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.calendar_today_label),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (isOnToday) MaterialTheme.colorScheme.textHint
                                    else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(
                        onClick = goToNextMonth,
                        enabled = canGoNext
                    ) {
                        Icon(
                            Lucide.ChevronRight,
                            contentDescription = stringResource(R.string.calendar_next_month),
                            tint = if (canGoNext) MaterialTheme.colorScheme.onBackground
                                   else MaterialTheme.colorScheme.textHint
                        )
                    }
                }
            )

            WeekdayHeader()

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.height(MONTH_GRID_HEIGHT)
            ) { page ->
                MonthGrid(
                    month = pageToMonth(page),
                    selectedDate = selectedDate,
                    today = today,
                    datesWithWear = datesWithWear,
                    onSelect = viewModel::selectDate
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
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
                        stringResource(R.string.calendar_nothing_worn),
                        color = MaterialTheme.colorScheme.textHint,
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
private fun WeekdayHeader() {
    val weekdayLabels = listOf(
        stringResource(R.string.weekday_sun),
        stringResource(R.string.weekday_mon),
        stringResource(R.string.weekday_tue),
        stringResource(R.string.weekday_wed),
        stringResource(R.string.weekday_thu),
        stringResource(R.string.weekday_fri),
        stringResource(R.string.weekday_sat)
    )
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
        isSelected -> MaterialTheme.colorScheme.onPrimary
        !inMonth -> MaterialTheme.colorScheme.textHint
        isToday -> MaterialTheme.colorScheme.onBackground
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
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
                Dot(color = StatusClean, size = 6.dp)
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
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun WornItemCard(item: ClothingItem) {
    Card(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            ClothingImage(
                imagePath = item.imagePath,
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                item.category,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

// ~100 years of history is effectively unbounded for a wardrobe log.
private const val MONTHS_BACK = 1200

// 6 week-rows × 48dp row height — keeps every paged month a fixed size.
private val MONTH_GRID_HEIGHT = 288.dp

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
