package com.app.wardove.data.model

data class GithubRelease(
    val tagName: String,
    val name: String,
    val body: String,
    val publishedAt: String,
    val htmlUrl: String,
    val prerelease: Boolean,
    val assets: List<GithubAsset>
)

data class GithubAsset(
    val name: String,
    val browserDownloadUrl: String,
    val size: Long
)
