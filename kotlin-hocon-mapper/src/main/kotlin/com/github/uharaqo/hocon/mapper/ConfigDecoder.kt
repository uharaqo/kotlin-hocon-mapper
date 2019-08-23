package com.github.uharaqo.hocon.mapper

import com.typesafe.config.Config
import com.typesafe.config.ConfigValue
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.SerializationException
import kotlinx.serialization.TaggedDecoder
import kotlinx.serialization.getElementIndexOrThrow
import kotlinx.serialization.internal.EnumDescriptor

class ConfigDecoder(private val config: Config) : TaggedDecoder<String>() {

    override fun decodeTaggedChar(tag: String) =
        config.getString(tag).firstOrNull()
            ?: throw SerializationException("invalid") // TODO: message

    override fun decodeTaggedString(tag: String): String = config.getString(tag); // TODO: NPE

    override fun decodeTaggedBoolean(tag: String) = config.getBoolean(tag)

    override fun decodeTaggedByte(tag: String) = decodeTaggedInt(tag).toByte()

    override fun decodeTaggedInt(tag: String) = config.getInt(tag)

    override fun decodeTaggedLong(tag: String) = config.getLong(tag)

    override fun decodeTaggedShort(tag: String) = decodeTaggedDouble(tag).toShort()

    override fun decodeTaggedFloat(tag: String) = decodeTaggedDouble(tag).toFloat()

    override fun decodeTaggedDouble(tag: String) = config.getDouble(tag)

    override fun decodeTaggedEnum(tag: String, enumDescription: EnumDescriptor) =
        enumDescription.getElementIndexOrThrow(config.getString(tag))

    override fun decodeTaggedUnit(tag: String) = Unit

    override fun decodeTaggedValue(tag: String): ConfigValue = config.getValue(tag)

    override fun decodeTaggedNotNullMark(tag: String) = !config.getIsNull(tag)

    override fun SerialDescriptor.getTag(index: Int): String {
        return getElementName(index)
            .also { if (!config.hasPath(it)) throw MissingFieldException(it) }
    }

}
