package com.maxton.bad_android.features.match.domain.entities

data class OpponentCandidate(
    val id: String,
    val fullName: String,
    val username: String,
    val avatarUrl: String? = null,
    val skillLevel: String,
    val compatibilityScore: Int,
)

data class MatchItem(
    val id: String,
    val player1Name: String,
    val player2Name: String,
    val player1Avatar: String? = null,
    val player2Avatar: String? = null,
    val setScores: List<Map<String, Int>>,
    val date: Long,
    val courtName: String,
    val isCompleted: Boolean,
) {
    fun copyWith(
        setScores: List<Map<String, Int>> = this.setScores,
        isCompleted: Boolean = this.isCompleted,
    ): MatchItem = copy(setScores = setScores, isCompleted = isCompleted)
}

fun Map<String, Int>.p1(): Int = this["p1"] ?: 0
fun Map<String, Int>.p2(): Int = this["p2"] ?: 0
