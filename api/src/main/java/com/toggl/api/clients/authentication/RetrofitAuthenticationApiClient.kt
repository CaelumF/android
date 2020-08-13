package com.toggl.api.clients.authentication

import com.toggl.api.extensions.basicAuthenticationWithPassword
import com.toggl.api.extensions.toModel
import com.toggl.api.models.ApiUser
import com.toggl.api.network.AuthenticationApi
import com.toggl.api.network.ResetPasswordBody
import com.toggl.api.network.SignUpBody
import com.toggl.models.domain.User
import com.toggl.models.validation.Email
import com.toggl.models.validation.Password
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class RetrofitAuthenticationApiClient @Inject constructor(
    private val authenticationApi: AuthenticationApi
) : AuthenticationApiClient {
    override suspend fun login(email: Email.Valid, password: Password.Valid): User {
        val authHeader = email.basicAuthenticationWithPassword(password)
        return authenticationApi.login(authHeader).let(ApiUser::toModel)
    }

    override suspend fun resetPassword(email: Email.Valid): String {
        val body = ResetPasswordBody(email.toString())
        val result = authenticationApi.resetPassword(body)
        return result.trim('"')
    }

    override suspend fun signUp(email: Email.Valid, password: Password.Strong): User {
        val usersTimezone = supportedTimezones.find { it == TimeZone.getDefault().id }

        val body = SignUpBody(email.toString(), password.toString(), usersTimezone)

        return authenticationApi.signUp(body).let(ApiUser::toModel)
    }

    companion object {
        val supportedTimezones = listOf(
            "Africa/Algiers",
            "Africa/Cairo",
            "Africa/Casablanca",
            "Africa/Harare",
            "Africa/Johannesburg",
            "Africa/Monrovia",
            "Africa/Nairobi",
            "America/Adak",
            "America/Argentina/Buenos_Aires",
            "America/Argentina/San_Juan",
            "America/Belem",
            "America/Bogota",
            "America/Campo_Grande",
            "America/Caracas",
            "America/Chicago",
            "America/Chihuahua",
            "America/Costa_Rica",
            "America/Cuiaba",
            "America/Denver",
            "America/Godthab",
            "America/Guatemala",
            "America/Halifax",
            "America/Indiana/Indianapolis",
            "America/Juneau",
            "America/La_Paz",
            "America/Lima",
            "America/Los_Angeles",
            "America/Manaus",
            "America/Mazatlan",
            "America/Mexico_City",
            "America/Monterrey",
            "America/Montevideo",
            "America/New_York",
            "America/Phoenix",
            "America/Regina",
            "America/Rio_Branco",
            "America/Santiago",
            "America/Santo_Domingo",
            "America/Sao_Paulo",
            "America/St_Johns",
            "America/Tijuana",
            "Asia/Almaty",
            "Asia/Baghdad",
            "Asia/Baku",
            "Asia/Bangkok",
            "Asia/Chongqing",
            "Asia/Colombo",
            "Asia/Dhaka",
            "Asia/Hong_Kong",
            "Asia/Irkutsk",
            "Asia/Jakarta",
            "Asia/Jerusalem",
            "Asia/Kabul",
            "Asia/Kamchatka",
            "Asia/Karachi",
            "Asia/Katmandu",
            "Asia/Kolkata",
            "Asia/Krasnoyarsk",
            "Asia/Kuala_Lumpur",
            "Asia/Kuwait",
            "Asia/Magadan",
            "Asia/Muscat",
            "Asia/Novosibirsk",
            "Asia/Rangoon",
            "Asia/Riyadh",
            "Asia/Seoul",
            "Asia/Shanghai",
            "Asia/Singapore",
            "Asia/Taipei",
            "Asia/Tashkent",
            "Asia/Tbilisi",
            "Asia/Tehran",
            "Asia/Tokyo",
            "Asia/Ulaanbaatar",
            "Asia/Urumqi",
            "Asia/Vladivostok",
            "Asia/Yakutsk",
            "Asia/Yekaterinburg",
            "Asia/Yerevan",
            "Atlantic/Azores",
            "Atlantic/Bermuda",
            "Atlantic/Cape_Verde",
            "Atlantic/South_Georgia",
            "Australia/Adelaide",
            "Australia/Brisbane",
            "Australia/Darwin",
            "Australia/Hobart",
            "Australia/Melbourne",
            "Australia/Perth",
            "Australia/Sydney",
            "Etc/UTC",
            "Europe/Amsterdam",
            "Europe/Athens",
            "Europe/Belgrade",
            "Europe/Berlin",
            "Europe/Bratislava",
            "Europe/Brussels",
            "Europe/Bucharest",
            "Europe/Budapest",
            "Europe/Copenhagen",
            "Europe/Dublin",
            "Europe/Helsinki",
            "Europe/Istanbul",
            "Europe/Kaliningrad",
            "Europe/Kiev",
            "Europe/Lisbon",
            "Europe/Ljubljana",
            "Europe/London",
            "Europe/Madrid",
            "Europe/Minsk",
            "Europe/Moscow",
            "Europe/Oslo",
            "Europe/Paris",
            "Europe/Prague",
            "Europe/Riga",
            "Europe/Rome",
            "Europe/Sarajevo",
            "Europe/Skopje",
            "Europe/Sofia",
            "Europe/Stockholm",
            "Europe/Tallinn",
            "Europe/Vienna",
            "Europe/Vilnius",
            "Europe/Warsaw",
            "Europe/Zagreb",
            "Iceland",
            "Indian/Mauritius",
            "Pacific/Auckland",
            "Pacific/Fiji",
            "Pacific/Guam",
            "Pacific/Honolulu",
            "Pacific/Majuro",
            "Pacific/Midway",
            "Pacific/Noumea",
            "Pacific/Pago_Pago",
            "Pacific/Port_Moresby",
            "Pacific/Tongatapu"
        )
    }
}
