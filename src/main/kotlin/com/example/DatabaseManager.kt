package com.example

import com.example.component.Database
import com.example.component.Row
import com.example.component.Table
import com.example.component.column.*


object DatabaseManager {
    var database: Database? = null

    fun getInstance(): DatabaseManager = this

    fun populateTable() {
        val table = Table("testTable"+ database!!.tables.size)
        table.apply {
            addColumn(IntegerColumn("column1"))
            addColumn(RealColumn("column2"))
            addColumn(StringColumn("column3"))
            addColumn(CharColumn("column4"))
            addColumn(ColorColumn("column5"))
            addColumn(ColorInvlColumn("column6","AAAAAA","FFFFFF"))
        }

        val row1 = Row().apply {
            values.addAll(listOf("10", "10.0", "10", "1", "12:00", "15:00"))
        }
        table.addRow(row1)

        val row2 = Row().apply {
            values.addAll(listOf("15", "15.0", "15", "3", "12:00", "15:00"))
        }
        table.addRow(row2)

        database?.addTable(table)
    }

    fun createDB(name: String) {
        database = Database(name)
    }

    fun addTable(name: String?): Boolean {
        return if (!name.isNullOrEmpty()) {
            val table = Table(name)
            database?.addTable(table)
            true
        } else false
    }

    fun deleteTable(tableIndex: Int): Boolean {
        return if (tableIndex != -1) {
            database?.deleteTable(tableIndex)
            true
        } else false
    }

    fun addColumn(tableIndex: Int, columnName: String?, columnType: ColumnType, min: String = "", max: String = ""): Boolean {
        if (columnName.isNullOrEmpty() || tableIndex == -1) return false

        val column: Column = when (columnType) {
            ColumnType.INT -> IntegerColumn(columnName)
            ColumnType.REAL -> RealColumn(columnName)
            ColumnType.STRING -> StringColumn(columnName)
            ColumnType.CHAR -> CharColumn(columnName)
            ColumnType.COLOR -> ColorColumn(columnName)
            ColumnType.COLORINVL -> ColorInvlColumn(columnName,min,max)
        }

        database?.tables?.get(tableIndex)?.addColumn(column)
        database?.tables?.get(tableIndex)?.rows?.forEach { row ->
            row.values.add("")
        }

        return true
    }

    fun deleteColumn(tableIndex: Int, columnIndex: Int): Boolean {
        return if (columnIndex != -1) {
            database?.tables?.get(tableIndex)?.deleteColumn(columnIndex)
            true
        } else false
    }

    fun addRow(tableIndex: Int, row: com.example.component.Row): Boolean {
        if (tableIndex != -1) {
            for (i in row.values.size until database!!.tables[tableIndex].columns.size) {
                row.values.add("")
            }
            database!!.tables[tableIndex].addRow(row)
            return true
        } else {
            return false
        }
    }

    fun deleteRow(tableIndex: Int, rowIndex: Int): Boolean {
        return if (rowIndex != -1) {
            database?.tables?.get(tableIndex)?.deleteRow(rowIndex)
            true
        } else false
    }

    fun updateCellValue(value: String, tableIndex: Int, columnIndex: Int, rowIndex: Int): Boolean {
        val table = database?.tables?.get(tableIndex)
        val column = table?.columns?.get(columnIndex)
        return if (column?.validate(value) == true) {
            val row = table.rows[rowIndex]
            row.setAt(columnIndex, value.trim())
            true
        } else false
    }

    fun tablesIntersection(tableIndex1: Int, tableIndex2: Int): Boolean {
        val tempTable1 = Table(database!!.tables[tableIndex1])
        val tempTable2 = Table(database!!.tables[tableIndex2])
        var i = 0
        while (i < tempTable1.columns.size) {
            var flag = false
            var j = i
            while (j < tempTable2.columns.size) {
                if (tempTable1.columns[i].name == tempTable2.columns[j].name &&
                    tempTable1.columns[i].type == tempTable2.columns[j].type) {
                    flag = true
                    val temp = tempTable2.columns[i]
                    tempTable2.columns[i] = tempTable2.columns[j]
                    tempTable2.columns[j] = temp
                    tempTable2.rows.forEach { row ->
                        val data = row.getAt(i)
                        row.setAt(i, row.getAt(j))
                        row.setAt(j, data)
                    }
                    i++
                    break
                } else {
                    j++
                }
            }
            if (!flag) {
                tempTable1.deleteColumn(i)
            }
        }
        i = 0
        while (i < tempTable1.rows.size) {
            var flag = tempTable2.rows.isNotEmpty()
            var j = 0
            while (j < tempTable2.rows.size) {
                flag = true
                for (k in tempTable1.rows[i].values.indices) {
                    if (tempTable1.rows[i].getAt(k) != tempTable2.rows[j].getAt(k)) {
                        flag = false
                        break
                    }
                }
                if (flag) {
                    i++
                    tempTable2.rows.removeAt(j)
                    break
                } else {
                    j++
                }
            }
            if (!flag) tempTable1.rows.removeAt(i)
        }
        addTable("${tempTable1.name} ^ ${tempTable2.name}")
        tempTable1.columns.forEach { column ->
            if (column.type.equals(ColumnType.COLORINVL.name)) {
                addColumn(database!!.tables.size - 1, column.name, ColumnType.valueOf(column.type),
                    (column as ColorInvlColumn).min, column.max)
            }
            else{
                addColumn(database!!.tables.size - 1, column.name, ColumnType.valueOf(column.type))
            }
        }
        tempTable1.rows.forEach { row ->
            addRow(database!!.tables.size - 1, row)
            println(row.values)
        }
        return true
    }

}
