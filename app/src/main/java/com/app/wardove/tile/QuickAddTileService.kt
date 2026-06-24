package com.app.wardove.tile

import android.service.quicksettings.TileService
import com.app.wardove.ui.navigation.ShortcutActions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuickAddTileService : TileService() {

    override fun onClick() {
        super.onClick()
        launchMainActivity(ShortcutActions.ADD_ITEM)
    }
}
