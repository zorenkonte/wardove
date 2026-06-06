package com.app.wardove.ui.navigation

object WardoveDestinations {
    const val WARDROBE = "wardrobe"
    const val ADD_ITEM = "add_item"
    const val LAUNDRY = "laundry"
    const val HISTORY = "history"

    const val ITEM_DETAIL_ROUTE = "item_detail/{itemId}"
    const val ITEM_DETAIL_ARG = "itemId"
    fun itemDetail(itemId: Long) = "item_detail/$itemId"
}
