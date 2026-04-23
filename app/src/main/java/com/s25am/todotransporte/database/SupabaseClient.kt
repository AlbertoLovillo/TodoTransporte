package com.s25am.todotransporte.database

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://iyinmvtnpmvcjnbumfqx.supabase.co",
        supabaseKey = "sb_publishable_fuvQ1DPnDhiuxZhhC8JR7Q_e3VyDK9g"
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