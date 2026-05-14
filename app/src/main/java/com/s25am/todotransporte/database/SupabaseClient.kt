package com.s25am.todotransporte.database

import com.s25am.todotransporte.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_SECRET_TOKEN
    ) {
        install(Auth) {
            serializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        install(Postgrest)
    }
}