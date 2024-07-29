package moe.wisteria.android.kimoji.entity

enum class Sort(
    val string: String
) {
    NEW("dd"),
    OLD("da"),
    LIKE("ld"),
    FAVORITE("vd");

    companion object {
        fun getByOrdinal(index: Int): Sort {
            return entries[index]
        }
    }

    override fun toString(): String {
        return this.string
    }
}