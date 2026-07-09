package com.example.booknest.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private const val ONBOARDING_PAGE_COUNT = 3

@Composable
fun OnboardingScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToSignIn: () -> Unit,
    onNavigateToGuestHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { ONBOARDING_PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()

    fun goToNextPage() {
        coroutineScope.launch {
            val nextPage = (pagerState.currentPage + 1).coerceAtMost(ONBOARDING_PAGE_COUNT - 1)
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> WelcomeScreen(
                        onNextClick = { goToNextPage() }
                    )

                    1 -> ExplanationScreen(
                        onNextClick = { goToNextPage() }
                    )

                    2 -> FinalScreen(

                        onGetStartedClick = onNavigateToSignUp,

                        onSignInClick = onNavigateToSignIn,

                        onContinueAsGuestClick = onNavigateToGuestHome
                    )
                }
            }


            if (pagerState.currentPage != ONBOARDING_PAGE_COUNT - 1) {
                OnboardingPageIndicator(
                    pageCount = ONBOARDING_PAGE_COUNT,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}