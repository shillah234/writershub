package com.writershub.app.data.mpesa

import android.util.Base64
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "MpesaManager"

object MpesaManager {

    // Sandbox URLs
    private const val BASE_URL = "https://sandbox.safaricom.co.ke"
    private const val AUTH_URL = "$BASE_URL/oauth/v1/generate?grant_type=client_credentials"
    private const val STK_PUSH_URL = "$BASE_URL/mpesa/stkpush/v1/processrequest"

    // HTTP Client
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor())
        .build()

    // Get OAuth Token - NOW ASYNCHRONOUS
    private fun getAccessToken(callback: (String?) -> Unit) {
        // Run network operation in background thread
        Thread {
            try {
                val consumerKey = MpesaCredentials.CONSUMER_KEY
                val consumerSecret = MpesaCredentials.CONSUMER_SECRET

                Log.d(TAG, "🔑 Consumer Key length: ${consumerKey.length}")
                Log.d(TAG, "🔑 Consumer Secret length: ${consumerSecret.length}")

                val credentials = "$consumerKey:$consumerSecret"
                val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

                Log.d(TAG, "📡 Requesting token from: $AUTH_URL")

                val request = Request.Builder()
                    .url(AUTH_URL)
                    .header("Authorization", "Basic $encodedCredentials")
                    .build()

                val response = client.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()

                Log.d(TAG, "📨 Response Code: $responseCode")

                if (response.isSuccessful && responseBody != null) {
                    Log.d(TAG, "✅ Response Body: $responseBody")
                    val json = JSONObject(responseBody)
                    val token = json.getString("access_token")
                    Log.d(TAG, "🎉 Token obtained successfully")
                    callback(token)
                } else {
                    Log.e(TAG, "❌ Failed. Status: $responseCode")
                    Log.e(TAG, "❌ Response: $responseBody")
                    callback(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Exception: ${e.message}")
                e.printStackTrace()
                callback(null)
            }
        }.start()
    }

    // Generate timestamp in format YYYYMMDDHHMMSS
    private fun getTimestamp(): String {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        return sdf.format(Date())
    }

    // Generate password for STK push - WITH ENHANCED LOGGING
    private fun getPassword(timestamp: String): String {
        val shortcode = MpesaCredentials.BUSINESS_SHORTCODE
        val passkey = MpesaCredentials.PASSKEY

        Log.d("MPESA_DEBUG", "========== PASSWORD GENERATION ==========")
        Log.d("MPESA_DEBUG", "Shortcode: '$shortcode' (Length: ${shortcode.length})")
        Log.d("MPESA_DEBUG", "Passkey: '$passkey' (Length: ${passkey.length})")
        Log.d("MPESA_DEBUG", "Timestamp: $timestamp")

        val rawPassword = "$shortcode$passkey$timestamp"
        Log.d("MPESA_DEBUG", "Raw Concatenated String: $rawPassword")
        Log.d("MPESA_DEBUG", "Raw String Length: ${rawPassword.length}")

        val encodedPassword = Base64.encodeToString(rawPassword.toByteArray(), Base64.NO_WRAP)
        Log.d("MPESA_DEBUG", "Final Base64 Encoded Password: $encodedPassword")
        Log.d("MPESA_DEBUG", "==========================================")

        return encodedPassword
    }
    // Format phone number (convert 07XXXXXXXX to 2547XXXXXXXX)
    private fun formatPhoneNumber(phone: String): String {
        return if (phone.startsWith("0")) {
            "254${phone.substring(1)}"
        } else if (phone.startsWith("7")) {
            "254$phone"
        } else {
            phone
        }
    }

    // Initiate STK Push for activation payment - NOW FULLY ASYNCHRONOUS
    fun initiateActivationPayment(
        phoneNumber: String,
        amount: Int = 100,
        callback: (MpesaResult) -> Unit
    ) {
        getAccessToken { token ->
            if (token == null) {
                callback(MpesaResult.Error("Failed to authenticate with M-Pesa"))
                return@getAccessToken
            }

            // Continue with STK Push in background thread
            Thread {
                try {
                    val timestamp = getTimestamp()
                    val password = getPassword(timestamp)
                    val formattedPhone = formatPhoneNumber(phoneNumber)
                    val shortcode = MpesaCredentials.BUSINESS_SHORTCODE

                    Log.d(TAG, "📱 Sending STK Push to: $formattedPhone")
                    Log.d(TAG, "📱 Using shortcode: $shortcode")
                    Log.d(TAG, "📱 Using timestamp: $timestamp")

                    val requestBody = JSONObject().apply {
                        put("BusinessShortCode", shortcode)
                        put("Password", password)
                        put("Timestamp", timestamp)
                        put("TransactionType", "CustomerPayBillOnline")
                        put("Amount", amount)
                        put("PartyA", formattedPhone)
                        put("PartyB", shortcode)
                        put("PhoneNumber", formattedPhone)
                        put("CallBackURL", "https://tgliwjhyhewzqvoayste.supabase.co/functions/v1/mpesa_callback")
                        put("AccountReference", "WritersHub")
                        put("TransactionDesc", "Account Activation")
                    }
                    Log.d(TAG, "📦 FULL REQUEST JSON: ${requestBody.toString()}")
                    // Log the full request body (without sensitive data)
                    Log.d(TAG, "📦 Request Body: $requestBody")

                    val mediaType = "application/json".toMediaType()
                    val body = requestBody.toString().toRequestBody(mediaType)

                    val request = Request.Builder()
                        .url(STK_PUSH_URL)
                        .header("Authorization", "Bearer $token")
                        .post(body)
                        .build()

                    val response = client.newCall(request).execute()
                    val responseCode = response.code
                    val responseBody = response.body?.string()

                    Log.d(TAG, "📨 STK Push Response Code: $responseCode")
                    Log.d(TAG, "📨 STK Push Response Body: $responseBody")

                    if (response.isSuccessful && responseBody != null) {
                        val json = JSONObject(responseBody)
                        val responseCode = json.optString("ResponseCode")
                        val responseDesc = json.optString("ResponseDescription")

                        if (responseCode == "0") {
                            val checkoutRequestId = json.getString("CheckoutRequestID")
                            Log.d(TAG, "✅ STK Push successful, Checkout ID: $checkoutRequestId")
                            callback(MpesaResult.Success(
                                message = "STK Push sent. Check your phone to enter PIN.",
                                checkoutRequestId = checkoutRequestId
                            ))
                        } else {
                            Log.e(TAG, "❌ STK Push failed: $responseCode - $responseDesc")
                            callback(MpesaResult.Error(responseDesc))
                        }
                    } else {
                        Log.e(TAG, "❌ Server error: $responseBody")
                        callback(MpesaResult.Error("Server error: $responseBody"))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "💥 STK Push Exception: ${e.message}")
                    e.printStackTrace()
                    callback(MpesaResult.Error("Network error: ${e.message}"))
                }
            }.start()
        }
    }

    // Check payment status (to be called from your callback URL)
    fun checkPaymentStatus(checkoutRequestId: String, callback: (MpesaResult) -> Unit) {
        callback(MpesaResult.Error("Status check not yet implemented"))
    }
}

// Result class for M-Pesa operations
sealed class MpesaResult {
    data class Success(val message: String, val checkoutRequestId: String? = null) : MpesaResult()
    data class Error(val message: String) : MpesaResult()
}

// Logging Interceptor
class HttpLoggingInterceptor : Interceptor {
    private var level = Level.NONE

    enum class Level {
        NONE, BASIC, HEADERS, BODY
    }

    fun setLevel(level: Level): HttpLoggingInterceptor {
        this.level = level
        return this
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        Log.d(TAG, "--> ${request.method} ${request.url}")
        val response = chain.proceed(request)
        Log.d(TAG, "<-- ${response.code} ${response.message}")

        return response
    }
}