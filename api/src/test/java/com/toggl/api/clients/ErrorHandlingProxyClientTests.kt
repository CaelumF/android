package com.toggl.api.clients

import com.toggl.api.clients.authentication.RetrofitAuthenticationApiClient
import com.toggl.api.clients.feedback.RetrofitFeedbackApiClient
import com.toggl.api.common.CoroutineTest
import com.toggl.api.exceptions.OfflineException
import com.toggl.api.network.ReportsApi
import com.toggl.api.network.SyncApi
import com.toggl.models.validation.Email
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.UnknownHostException

@DisplayName("The Error handling proxy client")
class ErrorHandlingProxyClientTests : CoroutineTest() {
    private val feedbackApiClient = mockk<RetrofitFeedbackApiClient>()
    private val authenticationApiClient = mockk<RetrofitAuthenticationApiClient>()
    private val reportsApi = mockk<ReportsApi>()
    private val syncApi = mockk<SyncApi>()
    private val errorHandlingProxyClient =
        ErrorHandlingProxyClient(authenticationApiClient, feedbackApiClient, reportsApi, syncApi)
    private val email = mockk<Email.Valid>()

    @Test
    fun `throws an offline exception when the wrapped client throws an UnknownHostException`() {
        coEvery { authenticationApiClient.resetPassword(email) } throws UnknownHostException()

        assertThrows<OfflineException> {
            runBlockingTest {
                errorHandlingProxyClient.resetPassword(email)
            }
        }
    }
}
