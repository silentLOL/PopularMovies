package at.stefanirndorfer.popularmovies.ui

/**
 * todo: remove this is just for education
 */
sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object DetailScreen : Screen("detail_screen")

    /**
     * helper function for Arguments
     */
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
