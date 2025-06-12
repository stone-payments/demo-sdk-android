package br.com.stonesdk.sdkdemo.routes

import androidx.navigation.NavController
import androidx.navigation.NavOptions

class NavigationManager {
    private var navController: NavController? = null

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    fun navigate(route: Route) = try {
        navController?.navigate(route)
    } catch (e: IllegalArgumentException) {
        handleIllegalNavigation()
    }

    fun navigateWithNavOptions(route: Route, navOptions: NavOptions) = try {
        navController?.navigate(route, navOptions)
    } catch (e: IllegalArgumentException) {
        handleIllegalNavigation()
    }

    fun navigateClearingStack(route: Route) = try {
        navController?.navigate(route) {
            popUpTo(0)
        }
    } catch (_: IllegalArgumentException) {
        handleIllegalNavigation()
    }

    fun navigateBack() {
        navController?.navigateUp()
    }

    private fun handleIllegalNavigation() = Unit
}