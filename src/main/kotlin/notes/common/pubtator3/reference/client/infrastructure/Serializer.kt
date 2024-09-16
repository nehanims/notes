package notes.common.pubtator3.reference.client.infrastructure

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.swagger.client.infrastructure.LocalDateAdapter
import java.util.Date

object Serializer {
    @JvmStatic
    val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .add(notes.common.pubtator3.reference.client.infrastructure.LocalDateTimeAdapter())
            .add(LocalDateAdapter())
            .build()
}