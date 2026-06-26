package com.app.wardove.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.app.wardove.MainActivity
import com.app.wardove.data.local.entity.ClothingStatus
import com.app.wardove.ui.navigation.ShortcutActions
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

class StatsWidget : GlanceAppWidget() {

    companion object {
        private val SMALL = DpSize(180.dp, 110.dp)
        private val MEDIUM = DpSize(250.dp, 110.dp)
    }

    override val sizeMode = SizeMode.Responsive(setOf(SMALL, MEDIUM))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java,
        )
        val repo = entryPoint.clothingRepository()

        val total = repo.observeAll().first().size
        val clean = repo.countByStatus(ClothingStatus.CLEAN)
        val worn = repo.countByStatus(ClothingStatus.WORN)
        val inLaundry = repo.countByStatus(ClothingStatus.IN_LAUNDRY)

        provideContent {
            StatsWidgetContent(
                context = context,
                total = total,
                clean = clean,
                worn = worn,
                inLaundry = inLaundry,
            )
        }
    }
}

@Composable
private fun StatsWidgetContent(
    context: Context,
    total: Int,
    clean: Int,
    worn: Int,
    inLaundry: Int,
) {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = ShortcutActions.STATS
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFF7F5F2)))
            .cornerRadius(24.dp)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clickable(actionStartActivity(intent)),
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
        ) {
            // Header: app name + total count
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Wardove",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF1A1A1A)),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = GlanceModifier.defaultWeight(),
                )
                Text(
                    text = "$total items",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF6B6B6B)),
                        fontSize = 12.sp,
                    ),
                )
            }
            Spacer(GlanceModifier.height(6.dp))
            // Status count cells
            Row(
                modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusCell(
                    count = clean,
                    label = "Clean",
                    textColor = Color(0xFF5DCAA5),
                    bgColor = Color(0x265DCAA5),
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                )
                Spacer(GlanceModifier.width(6.dp))
                StatusCell(
                    count = worn,
                    label = "Worn",
                    textColor = Color(0xFFEF9F27),
                    bgColor = Color(0x26EF9F27),
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                )
                Spacer(GlanceModifier.width(6.dp))
                StatusCell(
                    count = inLaundry,
                    label = "Laundry",
                    textColor = Color(0xFF7F77DD),
                    bgColor = Color(0x267F77DD),
                    modifier = GlanceModifier.defaultWeight().fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun StatusCell(
    count: Int,
    label: String,
    textColor: Color,
    bgColor: Color,
    modifier: GlanceModifier = GlanceModifier,
) {
    Box(
        modifier = modifier
            .background(ColorProvider(bgColor))
            .cornerRadius(14.dp)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = count.toString(),
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = 11.sp,
                ),
            )
        }
    }
}
