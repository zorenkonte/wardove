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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.wardove.ui.additem.AddItemScreen
import com.app.wardove.ui.calendar.CalendarScreen
import com.app.wardove.ui.history.HistoryScreen
import com.app.wardove.ui.itemdetail.ItemDetailScreen
import com.app.wardove.ui.laundry.LaundryScreen
import com.app.wardove.ui.settings.AboutSettingsScreen
import com.app.wardove.ui.settings.AppLockSettingsScreen
import com.app.wardove.ui.settings.AppearanceSettingsScreen
import com.app.wardove.ui.update.UpdateScreen
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            WardoveDrawerContent(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = WardoveDestinations.WARDROBE,
            enterTransition = enterFade,
            exitTransition = exitFade,
            popEnterTransition = popEnterFade,
            popExitTransition = popExitFade
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
                ),
                enterTransition = enterSlide,
                exitTransition = exitSlide,
                popEnterTransition = popEnterSlide,
                popExitTransition = popExitSlide
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
                ),
                enterTransition = enterSlide,
                exitTransition = exitSlide,
                popEnterTransition = popEnterSlide,
                popExitTransition = popExitSlide
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
                    onOpenHistory = { navController.navigate(WardoveDestinations.HISTORY) },
                    onOpenDrawer = openDrawer
                )
            }

            composable(
                route = WardoveDestinations.HISTORY,
                enterTransition = enterSlide,
                exitTransition = exitSlide,
                popEnterTransition = popEnterSlide,
                popExitTransition = popExitSlide
            ) {
                HistoryScreen(onBack = { navController.popBackStack() })
            }

            composable(WardoveDestinations.CALENDAR) {
                CalendarScreen(
                    onOpenDrawer = openDrawer
                )
            }

            composable(WardoveDestinations.STATS) {
                StatsScreen(
                    onOpenDrawer = openDrawer
                )
            }

            composable(WardoveDestinations.SETTINGS) {
                SettingsScreen(
                    onOpenDrawer = openDrawer,
                    onOpenAppearance = { navController.navigate(WardoveDestinations.SETTINGS_APPEARANCE) },
                    onOpenAppLock = { navController.navigate(WardoveDestinations.SETTINGS_APP_LOCK) },
                    onOpenAbout = { navController.navigate(WardoveDestinations.SETTINGS_ABOUT) }
                )
            }

            composable(
                route = WardoveDestinations.SETTINGS_APP_LOCK,
                enterTransition = enterSlide,
                exitTransition = exitSlide,
                popEnterTransition = popEnterSlide,
                popExitTransition = popExitSlide
            ) {
                AppLockSettingsScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = WardoveDestinations.SETTINGS_APPEARANCE,
                enterTransition = enterSlide,
                exitTransition = exitSlide,
                popEnterTransition = popEnterSlide,
                popExitTransition = popExitSlide
            ) {
                AppearanceSettingsScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = WardoveDestinations.SETTINGS_ABOUT,
                enterTransition = enterSlide,
                exitTransition = exitSlide,
                popEnterTransition = popEnterSlide,
                popExitTransition = popExitSlide
            ) {
                AboutSettingsScreen(
                    onBack = { navController.popBackStack() },
                    onOpenUpdates = { navController.navigate(WardoveDestinations.UPDATE) }
                )
            }

            composable(
                route = WardoveDestinations.UPDATE,
                enterTransition = enterSlide,
                exitTransition = exitSlide,
                popEnterTransition = popEnterSlide,
                popExitTransition = popExitSlide
            ) {
                UpdateScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
