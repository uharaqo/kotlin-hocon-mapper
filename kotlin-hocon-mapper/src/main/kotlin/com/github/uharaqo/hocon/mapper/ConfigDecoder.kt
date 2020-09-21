package com.github.uharaqo.hocon.mapper

import com.typesafe.config.Config
import com.typesafe.config.ConfigObject
import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueFactory
import com.typesafe.config.ConfigValueType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.TaggedDecoder

@OptIn(ExperimentalSerializationApi::class)
class ConfigDecoder(private val config: Config) : ConfigDecoderBase<String>() {

    private var idx = 0

    override fun SerialDescriptor.getTag(index: Int): String {
        val parentTag = currentTagOrNull ?: ""
        val childTag = getElementName(index)
        val path = if (parentTag.isNotEmpty()) "$parentTag.$childTag" else childTag

        return path
            // throw an exception when the path is not found
            .also {
                if (!config.hasPathOrNull(path))
                    throw SerializationException("Field '$path' is required, but it was missing")
            }
    }

    // check if the value is `null` or not
    override fun decodeTaggedNotNullMark(tag: String): Boolean = !config.getIsNull(tag)

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        (if (idx < descriptor.elementsCount) idx else CompositeDecoder.DECODE_DONE)
            .also { idx++ }

    override fun newObjectDecoder(configProvider: () -> Config): ConfigDecoder =
        if (idx == 0) this else ConfigDecoder(configProvider())

    override fun getValue(tag: String): ConfigValue = config.getValue(tag)
}

internal class ConfigListDecoder(private val list: List<ConfigValue>) : ConfigDecoderBase<Int>() {

    private var idx = 0

    override fun SerialDescriptor.getTag(index: Int) = index

    override fun decodeCollectionSize(descriptor: SerialDescriptor) = list.size

    override fun decodeElementIndex(descriptor: SerialDescriptor) =
        (if (idx < list.size) idx else CompositeDecoder.DECODE_DONE)
            .also { idx++ }

    override fun getValue(tag: Int): ConfigValue = list[tag]
}

@OptIn(ExperimentalSerializationApi::class)
abstract class ConfigDecoderBase<T> : TaggedDecoder<T>() {

    override fun beginStructure(desc: SerialDescriptor) =
        when (desc.kind) {
            StructureKind.LIST ->
                ConfigListDecoder(getValueAs(currentTag))

            StructureKind.MAP ->
                ConfigListDecoder(flattenEntries(getValueAs(currentTag)))

            StructureKind.CLASS, StructureKind.OBJECT ->
                newObjectDecoder { getValueAs<ConfigObject>(currentTag).toConfig() }

            else -> this
        }

    open fun newObjectDecoder(configProvider: () -> Config): ConfigDecoder = ConfigDecoder(configProvider())

    override fun decodeTaggedString(tag: T): String = getText(tag)
    override fun decodeTaggedChar(tag: T) =
        getText(tag).firstOrNull() ?: throw SerializationException("$tag is empty")

    override fun decodeTaggedEnum(tag: T, enumDescriptor: SerialDescriptor): Int =
        enumDescriptor.getElementIndex(getText(tag))

    override fun decodeTaggedBoolean(tag: T): Boolean =
        unwrapAs(tag, ConfigValueType.BOOLEAN)

    override fun decodeTaggedByte(tag: T) = getNumber(tag).toByte()
    override fun decodeTaggedInt(tag: T) = getNumber(tag).toInt()
    override fun decodeTaggedLong(tag: T) = getNumber(tag).toLong()
    override fun decodeTaggedShort(tag: T) = getNumber(tag).toShort()
    override fun decodeTaggedFloat(tag: T) = getNumber(tag).toFloat()
    override fun decodeTaggedDouble(tag: T) = getNumber(tag).toDouble()

    private fun getText(tag: T): String = unwrapAs(tag, ConfigValueType.STRING)
    private fun getNumber(tag: T): Number = unwrapAs(tag, ConfigValueType.NUMBER)

    override fun decodeTaggedValue(tag: T): Any = getValue(tag).unwrapped()

    private inline fun <reified E : Any> unwrapAs(tag: T, valueType: ConfigValueType): E =
        getValue(tag)
            .also {
                if (it.valueType() != valueType)
                    throw SerializationException(
                        "[property name: $tag] type: $valueType was expected, " +
                                "but received type: ${it.valueType()} " +
                                "with value: '${it.unwrapped()}'. " +
                                "(${it.origin().description()})"
                    )
            }
            .unwrapped() as E

    protected abstract fun getValue(tag: T): ConfigValue

    private inline fun <reified E : Any> getValueAs(tag: T): E = getValue(tag) as E

    private fun flattenEntries(config: Map<String, ConfigValue>): List<ConfigValue> =
        config.entries
            .fold(mutableListOf()) { acc, entry ->
                acc.add(ConfigValueFactory.fromAnyRef(entry.key))
                acc.add(entry.value)

                acc
            }
}
