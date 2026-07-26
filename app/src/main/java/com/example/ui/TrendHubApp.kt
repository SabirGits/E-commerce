package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.viewmodel.TrendHubViewModel

@Composable
fun TrendHubApp(viewModel: TrendHubViewModel = viewModel()) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: "home"

  val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
  val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
  val isAdmin by viewModel.isAdminMode.collectAsStateWithLifecycle()

  val cartCount = remember(cartItems) { cartItems.sumOf { it.quantity } }
  val wishlistCount = remember(wishlistItems) { wishlistItems.size }

  val showTopBar = !currentRoute.startsWith("details") && !currentRoute.startsWith("tracking") && !currentRoute.equals("checkout")
  val showBottomBar = !currentRoute.startsWith("details") && !currentRoute.startsWith("tracking") && !currentRoute.equals("checkout")

  Floating3DBackground {
    Scaffold(
      containerColor = androidx.compose.ui.graphics.Color.Transparent,
      topBar = {
        if (showTopBar) {
          TrendHubTopBar(
            cartCount = cartCount,
            wishlistCount = wishlistCount,
            isAdmin = isAdmin,
            onRoleToggle = { viewModel.toggleRoleMode() },
            onSearchClick = { navController.navigate("products") },
            onCartClick = { navController.navigate("cart") },
            onWishlistClick = { navController.navigate("wishlist") }
          )
        }
      },
      bottomBar = {
        if (showBottomBar) {
          TrendHubBottomNav(
            currentRoute = if (currentRoute.startsWith("products")) "products" else currentRoute,
            isAdmin = isAdmin,
            onNavigate = { route ->
              if (currentRoute != route) {
                navController.navigate(route) {
                  popUpTo("home") { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              }
            }
          )
        }
      }
    ) { innerPadding ->
      NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(innerPadding)
      ) {
        composable("home") {
          HomeScreen(
            viewModel = viewModel,
            onProductClick = { product -> navController.navigate("details/${product.id}") },
            onCategoryClick = { category ->
              viewModel.setCategory(category)
              navController.navigate("products")
            },
            onExploreClick = { navController.navigate("products") }
          )
        }

        composable("products") {
          ProductListingScreen(
            viewModel = viewModel,
            onProductClick = { product -> navController.navigate("details/${product.id}") }
          )
        }

        composable(
          route = "details/{productId}",
          arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
          val productId = backStackEntry.arguments?.getInt("productId") ?: 1
          ProductDetailsScreen(
            productId = productId,
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() },
            onCartClick = { navController.navigate("cart") },
            onProductClick = { relProduct -> navController.navigate("details/${relProduct.id}") }
          )
        }

        composable("cart") {
          CartScreen(
            viewModel = viewModel,
            onCheckoutClick = { navController.navigate("checkout") },
            onExploreClick = { navController.navigate("products") }
          )
        }

        composable("wishlist") {
          WishlistScreen(
            viewModel = viewModel,
            onProductClick = { product -> navController.navigate("details/${product.id}") },
            onExploreClick = { navController.navigate("products") }
          )
        }

        composable("checkout") {
          CheckoutScreen(
            viewModel = viewModel,
            onOrderSuccess = { newOrder ->
              navController.navigate("tracking/${newOrder.id}") {
                popUpTo("home")
              }
            },
            onBackClick = { navController.popBackStack() }
          )
        }

        composable("orders") {
          OrderHistoryScreen(
            viewModel = viewModel,
            onOrderClick = { order -> navController.navigate("tracking/${order.id}") },
            onExploreClick = { navController.navigate("products") }
          )
        }

        composable(
          route = "tracking/{orderId}",
          arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
          val orderId = backStackEntry.arguments?.getInt("orderId") ?: 1
          OrderTrackingScreen(
            orderId = orderId,
            viewModel = viewModel,
            onBackClick = { navController.popBackStack() }
          )
        }

        composable("profile") {
          ProfileScreen(
            viewModel = viewModel,
            onNavigateToOrders = { navController.navigate("orders") },
            onNavigateToWishlist = { navController.navigate("wishlist") },
            onNavigateToAdmin = { navController.navigate("admin_dash") }
          )
        }

        composable("admin_dash") {
          AdminDashboardScreen(
            viewModel = viewModel,
            onBackToStore = {
              navController.navigate("home") {
                popUpTo("home") { inclusive = true }
              }
            }
          )
        }
      }
    }
  }
}
