package com.example.medication_demo.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClientProvider {
    val client = createSupabaseClient(
        supabaseUrl = "https://bxmsgwqckxcftkjysvxm.supabase.co",
        supabaseKey = "sb_publishable_gJvM60ItaXtqY1wWEhywyA_H4UmyecX"
    ) {
        install(Auth) {
            host = "login-callback"
            scheme = "medicationdemo"
        }
        install(Postgrest)
    }
}
