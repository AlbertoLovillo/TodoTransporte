package com.s25am.todotransporte.database

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json

object SupabaseClient {

    // Usamos las variables definidas en tu build.gradle (desde local.properties)
    val client = createSupabaseClient(
        supabaseUrl = "https://iyinmvtnpmvcjnbumfqx.supabase.co",
        supabaseKey = "sb_publishable_fuvQ1DPnDhiuxZhhC8JR7Q_e3VyDK9g"
    ) {
        // Módulo para Autenticación (Registro, Login, Gestión de sesión)
        install(Auth) {
            // Opcional: Configura el serializador si planeas guardar datos complejos
            serializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        // Módulo para Base de Datos (Insertar, Consultar, Borrar en tablas)
        install(Postgrest)
    }
}