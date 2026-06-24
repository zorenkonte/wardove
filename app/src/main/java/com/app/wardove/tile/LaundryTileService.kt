package com.app.wardove.tile

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.app.wardove.R
import com.app.wardove.data.local.entity.ClothingStatus
import com.app.wardove.data.repository.ClothingRepository
import com.app.wardove.ui.navigation.ShortcutActions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class LaundryTileService : TileService() {

    @Inject lateinit var clothingRepository: ClothingRepository

    private var tileScope: CoroutineScope? = null

    override fun onStartListening() {
        super.onStartListening()
        tileScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        tileScope?.launch {
            val count = clothingRepository.countByStatus(ClothingStatus.WORN)
            withContext(Dispatchers.Main) {
                qsTile?.apply {
                    state = if (count > 0) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        subtitle = if (count > 0) {
                            getString(R.string.tile_laundry_subtitle, count)
                        } else {
                            ""
                        }
                    }
                    updateTile()
                }
            }
        }
    }

    override fun onStopListening() {
        super.onStopListening()
        tileScope?.cancel()
        tileScope = null
    }

    override fun onClick() {
        super.onClick()
        launchMainActivity(ShortcutActions.LAUNDRY)
    }
}
