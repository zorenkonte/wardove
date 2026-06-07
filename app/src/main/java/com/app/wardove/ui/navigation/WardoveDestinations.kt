package com.app.wardove.ui.navigation

object WardoveDestinations {
    const val WARDROBE = "wardrobe"
    const val LAUNDRY = "laundry"
    const val HISTORY = "history"
    const val CALENDAR = "calendar"

    const val ADD_ITEM_ROUTE = "add_item?itemId={itemId}"
    const val ADD_ITEM_ARG = "itemId"
    const val ADD_ITEM_NEW_ID: Long = -1L
    fun addItem(itemId: Long? = null): String =
        if (itemId == null || itemId < 0) "add_item" else "add_item?itemId=$itemId"

    const val ITEM_DETAIL_ROUTE = "item_detail/{itemId}"
    const val ITEM_DETAIL_ARG = "itemId"
    fun itemDetail(itemId: Long) = "item_detail/$itemId"
}
