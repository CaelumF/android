package com.toggl.api.network.deserializers

import com.squareup.moshi.Moshi
import com.toggl.api.models.ApiUser
import com.toggl.api.models.ApiUserJsonAdapter
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("The User deserializer")
class UserDeserializerTest {

    @Test
    fun `properly deserializes the user`() {
        val jsonAdapter = ApiUserJsonAdapter(Moshi.Builder().build())
        val user = jsonAdapter.fromJson(validUserJson)

        user shouldBe ApiUser(
            id = 9000,
            userId = null,
            apiToken = "1971800d4d82861d8f2c1651fea4d212",
            email = "johnt@swift.com",
            fullname = "John Swift",
            defaultWorkspaceId = 777,
            beginningOfWeek = 0
        )
    }

    @Test
    fun `properly deserializes the user with no workspace id`() {
        val jsonAdapter = ApiUserJsonAdapter(Moshi.Builder().build())
        val user = jsonAdapter.fromJson(validUserJsonWithNoDefaultWorkspace)

        user shouldBe ApiUser(
            id = 9000,
            userId = null,
            apiToken = "1971800d4d82861d8f2c1651fea4d212",
            email = "johnt@swift.com",
            fullname = "John Swift",
            defaultWorkspaceId = null,
            beginningOfWeek = 0
        )
    }

    @Test
    fun `properly deserializes the user with no id property`() {
        val jsonAdapter = ApiUserJsonAdapter(Moshi.Builder().build())
        val user = jsonAdapter.fromJson(validUserJsonWithNoId)

        user shouldBe ApiUser(
            id = null,
            userId = 9000,
            apiToken = "1971800d4d82861d8f2c1651fea4d212",
            email = "johnt@swift.com",
            fullname = "John Swift",
            defaultWorkspaceId = 777,
            beginningOfWeek = 0
        )
    }

    companion object {
        @Language("JSON")
        private const val validUserJson =
            """{"id":9000,"api_token":"1971800d4d82861d8f2c1651fea4d212","default_workspace_id":777,"email":"johnt@swift.com","fullname":"John Swift","beginning_of_week":0,"language":"en_US","image_url":"https://www.toggl.com/system/avatars/9000/small/open-uri20121116-2767-b1qr8l.png","timezone":"Europe/Zagreb","updated_at":"2013-03-06T12:18:42+00:00"}"""
        @Language("JSON")
        private const val validUserJsonWithNoDefaultWorkspace =
            """{"id":9000,"api_token":"1971800d4d82861d8f2c1651fea4d212","default_workspace_id":null,"email":"johnt@swift.com","fullname":"John Swift","beginning_of_week":0,"language":"en_US","image_url":"https://www.toggl.com/system/avatars/9000/small/open-uri20121116-2767-b1qr8l.png","timezone":"Europe/Zagreb","updated_at":"2013-03-06T12:18:42+00:00"}"""
        @Language("JSON")
        private const val validUserJsonWithNoId =
            """{"user_id":9000,"api_token":"1971800d4d82861d8f2c1651fea4d212","default_workspace_id":777,"email":"johnt@swift.com","fullname":"John Swift","beginning_of_week":0,"language":"en_US","image_url":"https://www.toggl.com/system/avatars/9000/small/open-uri20121116-2767-b1qr8l.png","timezone":"Europe/Zagreb","updated_at":"2013-03-06T12:18:42+00:00"}"""
    }
}
