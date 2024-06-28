package io.iohk.atala.automation.restassured

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import io.restassured.path.json.mapper.factory.GsonObjectMapperFactory
import java.lang.reflect.Type
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

class CustomGsonObjectMapperFactory: GsonObjectMapperFactory {
    companion object {
        fun builder(): GsonBuilder {
            return GsonBuilder()
                .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter())
        }
    }
    override fun create(cls: Type?, charset: String?): Gson {
        return builder().create()
    }

    class OffsetDateTimeTypeAdapter : JsonDeserializer<OffsetDateTime>, JsonSerializer<OffsetDateTime> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): OffsetDateTime {
            try {
                val dateTimeString = json.asString
                return OffsetDateTime.parse(dateTimeString)
            } catch (e: DateTimeParseException) {
                throw JsonParseException("Error parsing OffsetDateTime", e)
            }
        }

        override fun serialize(
            src: OffsetDateTime,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(src.toString())
        }
    }
}
