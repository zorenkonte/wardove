package com.app.wardove.data.backup

import com.app.wardove.data.local.entity.ClothingItem
import com.app.wardove.data.local.entity.LaundryCycle
import com.app.wardove.data.local.entity.LaundryCycleItem
import com.app.wardove.data.local.entity.WearLog
import org.json.JSONArray
import org.json.JSONObject

/**
 * Snapshot of the entire local database, serialized to `data.json` inside the backup zip.
 * `imagePath` on each item is rewritten to a relative `images/<file>` entry name at export
 * time and back to an absolute `filesDir` path at import time — see [BackupRepository].
 */
data class BackupPayload(
    val schemaVersion: Int,
    val exportedAt: Long,
    val items: List<ClothingItem>,
    val wearLogs: List<WearLog>,
    val laundryCycles: List<LaundryCycle>,
    val laundryCycleItems: List<LaundryCycleItem>
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("schemaVersion", schemaVersion)
        put("exportedAt", exportedAt)
        put("items", JSONArray(items.map { it.toJson() }))
        put("wearLogs", JSONArray(wearLogs.map { it.toJson() }))
        put("laundryCycles", JSONArray(laundryCycles.map { it.toJson() }))
        put("laundryCycleItems", JSONArray(laundryCycleItems.map { it.toJson() }))
    }

    companion object {
        const val CURRENT_SCHEMA_VERSION = 1

        fun fromJson(json: JSONObject): BackupPayload = BackupPayload(
            schemaVersion = json.optInt("schemaVersion", 1),
            exportedAt = json.optLong("exportedAt", 0L),
            items = json.getJSONArray("items").toObjectList { it.toClothingItem() },
            wearLogs = json.optJSONArray("wearLogs")?.toObjectList { it.toWearLog() } ?: emptyList(),
            laundryCycles = json.optJSONArray("laundryCycles")?.toObjectList { it.toLaundryCycle() } ?: emptyList(),
            laundryCycleItems = json.optJSONArray("laundryCycleItems")?.toObjectList { it.toLaundryCycleItem() } ?: emptyList()
        )
    }
}

/** Outcome of a restore, reported back to the UI. */
sealed interface ImportResult {
    data class Success(
        val itemCount: Int,
        val wearLogCount: Int,
        val laundryCycleCount: Int
    ) : ImportResult

    data class Error(val message: String) : ImportResult
}

private inline fun <T> JSONArray.toObjectList(transform: (JSONObject) -> T): List<T> =
    (0 until length()).map { transform(getJSONObject(it)) }

private fun ClothingItem.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("name", name)
    put("category", category)
    put("color", color)
    put("imagePath", imagePath)
    put("status", status)
    put("lastWornDate", lastWornDate ?: JSONObject.NULL)
    put("totalWearCount", totalWearCount)
    put("createdAt", createdAt)
    put("notes", notes ?: JSONObject.NULL)
    put("price", price ?: JSONObject.NULL)
    put("tags", tags)
}

private fun JSONObject.toClothingItem(): ClothingItem = ClothingItem(
    id = getLong("id"),
    name = getString("name"),
    category = getString("category"),
    color = getString("color"),
    imagePath = getString("imagePath"),
    status = optString("status", "CLEAN"),
    lastWornDate = if (isNull("lastWornDate")) null else optLong("lastWornDate"),
    totalWearCount = optInt("totalWearCount", 0),
    createdAt = optLong("createdAt", 0L),
    notes = if (isNull("notes")) null else optString("notes"),
    price = if (isNull("price")) null else optDouble("price"),
    tags = optString("tags", "")
)

private fun WearLog.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("clothingItemId", clothingItemId)
    put("wornDate", wornDate)
}

private fun JSONObject.toWearLog(): WearLog = WearLog(
    id = getLong("id"),
    clothingItemId = getLong("clothingItemId"),
    wornDate = getLong("wornDate")
)

private fun LaundryCycle.toJson(): JSONObject = JSONObject().apply {
    put("id", id)
    put("startedAt", startedAt)
    put("completedAt", completedAt ?: JSONObject.NULL)
    put("itemCount", itemCount)
}

private fun JSONObject.toLaundryCycle(): LaundryCycle = LaundryCycle(
    id = getLong("id"),
    startedAt = getLong("startedAt"),
    completedAt = if (isNull("completedAt")) null else optLong("completedAt"),
    itemCount = optInt("itemCount", 0)
)

private fun LaundryCycleItem.toJson(): JSONObject = JSONObject().apply {
    put("cycleId", cycleId)
    put("clothingItemId", clothingItemId)
}

private fun JSONObject.toLaundryCycleItem(): LaundryCycleItem = LaundryCycleItem(
    cycleId = getLong("cycleId"),
    clothingItemId = getLong("clothingItemId")
)
