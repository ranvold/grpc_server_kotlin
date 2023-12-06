package com.example.component.column

class ColorInvlColumn(name: String, private var _min: String, private var _max: String) : Column(name) {

    val min: String
        get() = _min

    val max: String
        get() = _max

    override val type = ColumnType.COLORINVL.name

    override fun validate(data: String): Boolean {
        if (!data.matches(Regex("[0-9a-fA-F]{6}"))) {
            return false
        }

        return isWithinRange(data, min, max)
    }

    private fun isWithinRange(time: String, minTime: String, maxTime: String): Boolean {
        return time >= minTime && time <= maxTime
    }

    companion object {
        fun validateMinMax(min: String, max: String): Boolean {
            return min.matches(Regex("[0-9a-fA-F]{6}")) && max.matches(Regex("[0-9a-fA-F]{6}"))
                    && (min.toLowerCase() <= max.toLowerCase())
        }
    }
}
