package com.maxton.bad_android

object Routes {
    const val ONBOARDING = "onboarding"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
    const val SESSION_DETAILS = "session_details/{sessionId}"
    const val DEVICE_SESSIONS = "device_sessions"
    const val NOTIFICATIONS = "notifications"
    const val MEMBER_MANAGEMENT = "member_management"
    const val JOIN_REQUESTS = "join_requests"
    const val MATCHMAKING = "matchmaking"
    const val MATCH_CREATOR = "match_creator?opponentId={opponentId}"
    const val MATCH_SCORE_ENTRY = "match_score_entry/{matchId}"
    const val MATCH_RESULT = "match_result/{matchId}"
    const val PLAYER_PROFILE = "player_profile/{playerId}"
    const val RATE_PLAYER = "rate_player/{playerId}"
    const val LEADERBOARD = "leaderboard"

    fun sessionDetails(sessionId: String) = "session_details/$sessionId"
    fun matchScoreEntry(matchId: String) = "match_score_entry/$matchId"
    fun matchResult(matchId: String) = "match_result/$matchId"
    fun matchCreator(opponentId: String? = null): String {
        return if (opponentId.isNullOrBlank()) "match_creator?opponentId="
        else "match_creator?opponentId=$opponentId"
    }
    fun playerProfile(playerId: String) = "player_profile/$playerId"
    fun ratePlayer(playerId: String) = "rate_player/$playerId"
}
