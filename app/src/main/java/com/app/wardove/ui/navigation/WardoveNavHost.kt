package com.app.wardove.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.wardove.ui.additem.AddItemScreen
import com.app.wardove.ui.calendar.CalendarScreen
import com.app.wardove.ui.history.HistoryScreen
import com.app.wardove.ui.itemdetail.ItemDetailScreen
import com.app.wardove.ui.laundry.LaundryScreen
import com.app.wardove.ui.wardrobe.WardrobeScreen

private const val SNACKBAR_KEY = "snackbar_message"

@Composable
fun WardoveNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = WardoveDestinations.WARDROBE
    ) {
        composable(WardoveDestinations.WARDROBE) { entry ->
            val savedHandle = entry.savedStateHandle
            val message by savedHandle
                .getStateFlow<String?>(SNACKBAR_KEY, null)
                .collectAsState()

            WardrobeScreen(
                onAddItem = { navController.navigate(WardoveDestinations.addItem()) },
                onOpenItem = { id ->
                    navController.navigate(WardoveDestinations.itemDetail(id))
                },
                onOpenLaundry = { navController.navigate(WardoveDestinations.LAUNDRY) },
                onOpenCalendar = { navController.navigate(WardoveDestinations.CALENDAR) },
                snackbarMessage = message,
                onSnackbarShown = { savedHandle[SNACKBAR_KEY] = null }
            )
        }

        composable(
            route = WardoveDestinations.ADD_ITEM_ROUTE,
            arguments = listOf(
                navArgument(WardoveDestinations.ADD_ITEM_ARG) {
                    type = NavType.LongType
                    defaultValue = WardoveDestinations.ADD_ITEM_NEW_ID
                }
            )
        ) {
            AddItemScreen(
                onSaved = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SNACKBAR_KEY, "Item saved")
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = WardoveDestinations.ITEM_DETAIL_ROUTE,
            arguments = listOf(
                navArgument(WardoveDestinations.ITEM_DETAIL_ARG) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong(WardoveDestinations.ITEM_DETAIL_ARG) ?: 0L
            ItemDetailScreen(
                itemId = itemId,
                onBack = { navController.popBackStack() },
                onEdit = { id ->
                    navController.navigate(WardoveDestinations.addItem(id))
                },
                onDeleted = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(SNACKBAR_KEY, "Item deleted")
                    navController.popBackStack()
                }
            )
        }

        composable(WardoveDestinations.LAUNDRY) {
            LaundryScreen(
                onBack = { navController.popBackStack() },
                onOpenHistory = { navController.navigate(WardoveDestinations.HISTORY) },
                onOpenCalendar = {
                    navController.navigate(WardoveDestinations.CALENDAR) {
                        popUpTo(WardoveDestinations.WARDROBE)
                    }
                }
            )
        }

        composable(WardoveDestinations.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(WardoveDestinations.CALENDAR) {
            CalendarScreen(
                onSelectWardrobe = {
                    navController.popBackStack(WardoveDestinations.WARDROBE, inclusive = false)
                },
                onSelectLaundry = {
                    navController.navigate(WardoveDestinations.LAUNDRY) {
                        popUpTo(WardoveDestinations.WARDROBE)
                    }
                }
            )
        }
    }
}
