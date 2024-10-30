package com.example.task

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.task.feature_account.presentation.LoginScreen
import com.example.task.feature_account.presentation.ProfileScreen
import com.example.task.feature_account.presentation.SignUpScreen
import com.example.task.feature_home.domain.CommunityRetrieve
import com.example.task.feature_home.presentation.CreateCommunityScreen
import com.example.task.feature_home.presentation.HomeScreen
import com.example.task.feature_resolution.presentation.CommunityScreen
import com.example.task.feature_resolution.presentation.components.CreateResolution
import com.example.task.feature_resolution.presentation.components.UpdateResolution
import com.example.task.objects.AccountViewModelObject
import com.example.task.objects.HomeViewModelObject
import com.example.task.objects.ResolutionViewModelObject


@Composable
fun NavigationController(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "LoginScreen") {
        composable("LoginScreen") {
            LoginScreen(
                navController = navController,
                modifier = modifier,
                accountViewModel = AccountViewModelObject.accountViewModel
            )
        }
        composable("SignUpScreen") {
            SignUpScreen(
                navController = navController,
                modifier = modifier,
                accountViewModel = AccountViewModelObject.accountViewModel
            )
        }
        composable(
            route = "HomeScreen?uid={uid}",
            arguments = listOf(navArgument("uid") { defaultValue = "" })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid")

            HomeScreen(
                navController = navController,
                homeViewModel = HomeViewModelObject.homeViewModel,
                //uid = uid ?: ""
            )
        }

        composable("ProfileScreen") {
            ProfileScreen(
                navController = navController,
                accountViewModel = AccountViewModelObject.accountViewModel
            )
        }

        composable("CreateCommunityScreen") {
            CreateCommunityScreen(
                homeViewModel = HomeViewModelObject.homeViewModel,
                navController = navController
            )
        }

        composable("CommunityScreen") {
            CommunityScreen(
                resolutionViewModel = ResolutionViewModelObject.resolutionViewModel,
                navController = navController
            )
        }
        composable(
            route = "UpdateResolution?cid={cid}&uid={uid}",
            arguments = listOf(
                navArgument("cid") { defaultValue = "" },
                navArgument("uid") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("cid") ?: ""
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            UpdateResolution(navController = navController,
                resolutionViewModel = ResolutionViewModelObject.resolutionViewModel,
                cid = cid,
                uid = uid)
        }
        composable(
            route = "CreateResolution?cid={cid}&uid={uid}",
            arguments = listOf(
                navArgument("cid") { defaultValue = "" },
                navArgument("uid") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("cid") ?: ""
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            CreateResolution(navController = navController,
                resolutionViewModel = ResolutionViewModelObject.resolutionViewModel,
                cid = cid,
                uid = uid)
        }
    }

}