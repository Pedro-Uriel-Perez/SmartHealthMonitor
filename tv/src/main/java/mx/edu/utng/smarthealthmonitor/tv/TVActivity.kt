package mx.edu.utng.smarthealthmonitor.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.edu.utng.smarthealthmonitor.tv.presentation.TvCatalogScreen
import mx.edu.utng.smarthealthmonitor.tv.presentation.TvDetailScreen
import mx.edu.utng.smarthealthmonitor.tv.presentation.TvPlaybackScreen

class TVActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHealthTvTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "catalog") {
                    composable("catalog") {
                        TvCatalogScreen(onCardClick = { lecturaId ->
                            navController.navigate("detail/$lecturaId")
                        })
                    }
                    composable(
                        route = "detail/{lecturaId}",
                        arguments = listOf(navArgument("lecturaId") { type = NavType.IntType })
                    ) { backStack ->
                        val id = backStack.arguments?.getInt("lecturaId") ?: return@composable
                        TvDetailScreen(lecturaId = id, navController = navController)
                    }
                    composable("playback") {
                        TvPlaybackScreen(navController = navController)
                    }
                }
            }
        }
    }
}
