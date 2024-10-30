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

        composable(
            "CommunityScreen?cid={cid}&name={name}&image={image}&members={members}",
            arguments = listOf(
                navArgument("cid") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("image") { type = NavType.StringType },
                navArgument("members") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cid = backStackEntry.arguments?.getString("cid") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val image = backStackEntry.arguments?.getString("image") ?: ""
            val membersString = backStackEntry.arguments?.getString("members") ?: ""
            val members = membersString.split(",").toList() // Convert comma-separated string back to list

            CommunityScreen(
                resolutionViewModel = ResolutionViewModelObject.resolutionViewModel,
                navController = navController,
                cid = cid,
                name = name,
                image = image,
                members = members
            )
        }


    }

}