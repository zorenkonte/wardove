package com.app.wardove.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.wardove.R
import com.composables.icons.lucide.ChartBar
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.Shirt
import com.composables.icons.lucide.WashingMachine

private data class DrawerItem(val route: String, @StringRes val labelResId: Int, val icon: ImageVector)

private val mainItems = listOf(
    DrawerItem(WardoveDestinations.WARDROBE, R.string.nav_wardrobe, Lucide.Shirt),
    DrawerItem(WardoveDestinations.LAUNDRY,  R.string.nav_laundry,  Lucide.WashingMachine),
    DrawerItem(WardoveDestinations.CALENDAR, R.string.nav_calendar, Lucide.Calendar),
    DrawerItem(WardoveDestinations.STATS,    R.string.nav_stats,    Lucide.ChartBar),
)

private val utilityItems = listOf(
    DrawerItem(WardoveDestinations.HISTORY,  R.string.nav_history,  Lucide.History),
    DrawerItem(WardoveDestinations.SETTINGS, R.string.nav_settings, Lucide.Settings),
)

@Composable
fun WardoveDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background
    ) {
        val itemColors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedIconColor = MaterialTheme.colorScheme.onSurface,
            selectedTextColor = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(16.dp))

        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp)
        )

        Spacer(Modifier.height(8.dp))

        mainItems.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelResId)) },
                selected = currentRoute == item.route,
                onClick = {
                    onClose()
                    onNavigate(item.route)
                },
                colors = itemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline,
            thickness = 0.5.dp
        )

        utilityItems.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.labelResId)) },
                selected = currentRoute == item.route,
                onClick = {
                    onClose()
                    onNavigate(item.route)
                },
                colors = itemColors,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
