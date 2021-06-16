package drewcarlson.blockset.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BdbBlockList(
    @SerialName("_embedded")
    val embedded: Embedded,
) {

    @Serializable
    data class Embedded(
        val blocks: List<BdbBlock>
    )
}
