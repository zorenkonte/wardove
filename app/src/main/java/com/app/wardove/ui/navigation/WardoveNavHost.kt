package com.app.wardove.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.wardove.ui.additem.AddItemScreen
import com.app.wardove.ui.history.HistoryScreen
import com.app.wardove.ui.itemdetail.ItemDetailScreen
import com.app.wardove.ui.laundry.LaundryScreen
import com.app.wardove.ui.wardrobe.WardrobeScreen

@Composable
fun WardoveNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = WardoveDestinations.WARDROBE
    ) {
        composable(WardoveDestinations.WARDROBE) {
            WardrobeScreen(
                onAddItem = { navController.navigate(WardoveDestinations.ADD_ITEM) },
                onOpenItem = { id ->
                    navController.navigate(WardoveDestinations.itemDetail(id))
                },
                onOpenLaundry = { navController.navigate(WardoveDestinations.LAUNDRY) }
            )
        }

        composable(WardoveDestinations.ADD_ITEM) {
            AddItemScreen(onDone = { navController.popBackStack() })
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
                onBack = { navController.popBackStack() }
            )
        }

        composable(WardoveDestinations.LAUNDRY) {
            LaundryScreen(
                onBack = { navController.popBackStack() },
                onOpenHistory = { navController.navigate(WardoveDestinations.HISTORY) }
            )
        }

        composable(WardoveDestinations.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }
    }
}
