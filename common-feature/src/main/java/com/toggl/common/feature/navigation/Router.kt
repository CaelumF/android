package com.toggl.common.feature.navigation

import androidx.navigation.NavController
import com.toggl.common.DeepLinkUrls
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Router @Inject constructor(
    private val deepLinkUrls: DeepLinkUrls
) {
    private var currentBackStack: BackStack = backStackOf(Route.Timer)

    fun processNewBackStack(
        newBackStack: BackStack,
        navController: NavController
    ) {
        val oldBackStack = currentBackStack

        val operations = sequence {

            val neededPopOperations = calculatePopOperations(oldBackStack, newBackStack)

            yieldAll(neededPopOperations)

            val numberOfValidOperations = oldBackStack.size - neededPopOperations.size

            for (i in numberOfValidOperations until newBackStack.size) {
                val newRoute = newBackStack[i]
                val deepLink = newRoute.deepLink(deepLinkUrls)
                val navOptions = newRoute.navigationOptions()
                val operation = BackStackOperation.Push(deepLink, navOptions)
                yield(operation)
            }
        }

        for (operation in operations) {
            when (operation) {
                is BackStackOperation.Push -> navController.navigate(operation.deepLink, operation.navOptions)
                BackStackOperation.Pop -> navController.popBackStack()
            }
        }

        currentBackStack = newBackStack
    }

    private fun calculatePopOperations(oldBackStack: BackStack, newBackStack: BackStack): List<BackStackOperation> =
        oldBackStack.foldIndexed(emptyList<BackStackOperation>()) { index, popOperations, oldRoute ->
            if (popOperations.any()) {
                // As soon as any operation is invalid, we need to pop the rest
                return@foldIndexed popOperations + BackStackOperation.Pop
            }

            val newRoute = newBackStack.getOrNull(index)
            if (newRoute != null && oldRoute.isSameTypeAs(newRoute)) popOperations
            else listOf(BackStackOperation.Pop)
        }
}
