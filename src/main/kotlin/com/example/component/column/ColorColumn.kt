package com.example.component.column

class ColorColumn(name: String) : Column(name) {

    override val type = ColumnType.COLOR.name

    override fun validate(data: String): Boolean {
        return data != null && data.matches(Regex("[0-9a-fA-F]{6}"))
    }
}