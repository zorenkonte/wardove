package com.app.wardove.ui.navigation

import android.net.Uri

object WardoveDestinations {
    const val WARDROBE = "wardrobe"
    const val LAUNDRY = "laundry"
    const val HISTORY = "history"
    const val CALENDAR = "calendar"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val SETTINGS_APPEARANCE = "settings_appearance"
    const val SETTINGS_APP_LOCK = "settings_app_lock"
    const val SETTINGS_ABOUT = "settings_about"
    const val SETTINGS_LICENSES = "settings_licenses"
    const val SETTINGS_DIAGNOSTICS = "settings_diagnostics"
    const val SETTINGS_NOTIFICATIONS = "settings_notifications"
    const val UPDATE = "update"

    const val ADD_ITEM_ROUTE = "add_item?itemId={itemId}"
    const val ADD_ITEM_ARG = "itemId"
    const val ADD_ITEM_NEW_ID: Long = -1L
    fun addItem(itemId: Long? = null): String =
        if (itemId == null || itemId < 0) "add_item" else "add_item?itemId=$itemId"

    const val ITEM_DETAIL_ROUTE = "item_detail/{itemId}"
    const val ITEM_DETAIL_ARG = "itemId"
    fun itemDetail(itemId: Long) = "item_detail/$itemId"

    const val LICENSE_DETAIL_ROUTE = "license_detail/{libraryId}"
    const val LICENSE_DETAIL_ARG = "libraryId"
    fun licenseDetail(libraryId: String) = "license_detail/${Uri.encode(libraryId)}"
}

/** Custom intent actions for static app shortcuts defined in res/xml/shortcuts.xml. */
object ShortcutActions {
    const val ADD_ITEM = "com.app.wardove.action.ADD_ITEM"
    const val LAUNDRY  = "com.app.wardove.action.LAUNDRY"
    const val UPDATE   = "com.app.wardove.action.UPDATE"
    const val STATS    = "com.app.wardove.action.STATS"

    /** Maps a shortcut intent action to the Compose nav route to navigate to. */
    fun routeForAction(action: String?): String? = when (action) {
        ADD_ITEM -> WardoveDestinations.addItem()
        LAUNDRY  -> WardoveDestinations.LAUNDRY
        UPDATE   -> WardoveDestinations.UPDATE
        STATS    -> WardoveDestinations.STATS
        else     -> null
    }

    /** Maps a shortcut intent action to the shortcutId used in shortcuts.xml (for reportShortcutUsed). */
    fun shortcutIdForAction(action: String?): String? = when (action) {
        ADD_ITEM -> "add_item"
        LAUNDRY  -> "laundry"
        UPDATE   -> "update"
        STATS    -> "stats"
        else     -> null
    }
}
