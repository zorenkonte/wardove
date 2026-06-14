package com.app.wardove.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.app.wardove.ui.settings.SettingsScreen
import com.app.wardove.ui.stats.StatsScreen
import com.app.wardove.ui.wardrobe.WardrobeScreen
import kotlinx.coroutines.launch

private const val SNACKBAR_KEY = "snackbar_message"

@Composable
fun WardoveNavHost(
    navController: NavHostController = rememberNavController()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDrawer: () -> Unit = { scope.launch { drawerState.open() } }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            WardoveDrawerContent(
                onOpenSettings = { navController.navigate(WardoveDestinations.SETTINGS) },
                onOpenHistory = { navController.navigate(WardoveDestinations.HISTORY) },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = WardoveDestinations.WARDROBE,
            enterTransition = enterSlide,
            exitTransition = exitSlide,
            popEnterTransition = popEnterSlide,
            popExitTransition = popExitSlide
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
                    onOpenStats = { navController.navigate(WardoveDestinations.STATS) },
                    onOpenDrawer = openDrawer,
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
                    },
                    onOpenStats = {
                        navController.navigate(WardoveDestinations.STATS) {
                            popUpTo(WardoveDestinations.WARDROBE)
                        }
                    },
                    onOpenDrawer = openDrawer
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
                    },
                    onSelectStats = {
                        navController.navigate(WardoveDestinations.STATS) {
                            popUpTo(WardoveDestinations.WARDROBE)
                        }
                    },
                    onOpenDrawer = openDrawer
                )
            }

            composable(WardoveDestinations.STATS) {
                StatsScreen(
                    onSelectWardrobe = {
                        navController.popBackStack(WardoveDestinations.WARDROBE, inclusive = false)
                    },
                    onSelectLaundry = {
                        navController.navigate(WardoveDestinations.LAUNDRY) {
                            popUpTo(WardoveDestinations.WARDROBE)
                        }
                    },
                    onSelectCalendar = {
                        navController.navigate(WardoveDestinations.CALENDAR) {
                            popUpTo(WardoveDestinations.WARDROBE)
                        }
                    },
                    onOpenDrawer = openDrawer
                )
            }

            composable(WardoveDestinations.SETTINGS) {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
