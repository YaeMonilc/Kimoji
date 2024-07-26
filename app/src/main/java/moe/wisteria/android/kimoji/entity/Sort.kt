package moe.wisteria.android.kimoji.entity

enum class Sort(
    val string: String
) {
    NEW("dd"),
    OLD("da"),
    LIKE("ld"),
    FAVORITE("vd");

    override fun toString(): String {
        return this.string
    }
}