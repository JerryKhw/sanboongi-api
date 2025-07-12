package dev.jerrykhw.sanboongi.util.hwp

import kr.dogfoot.hwplib.`object`.bodytext.Section
import kr.dogfoot.hwplib.`object`.bodytext.control.ControlTable
import kr.dogfoot.hwplib.`object`.bodytext.control.ControlType
import kr.dogfoot.hwplib.tool.objectfinder.CellFinder

object Hwp {
    fun getTablesFromSection(section: Section): List<ControlTable> {
        val tables = mutableListOf<ControlTable>()
        section.paragraphs.forEach { paragraph ->
            paragraph.controlList.forEach {
                if (it.type == ControlType.Table) {
                    val table = (it as ControlTable)
                    if (CellFinder.findAll(table, "소속").isNotEmpty()) {
                        tables.add(table)
                    }
                    table.findTables(tables)
                }
            }
        }
        return tables
    }


    private fun ControlTable.findTables(list: MutableList<ControlTable>) {
        for (row in rowList) {
            for (cell in row.cellList) {
                cell.paragraphList.forEach { paragraph ->
                    paragraph.controlList?.forEach { control ->
                        if (control.type == ControlType.Table) {
                            val tableControl = (control as ControlTable)
                            if (CellFinder.findAll(tableControl, "소속").isNotEmpty()) {
                                list.add(tableControl)
                            }
                            tableControl.findTables(list)
                        }
                    }
                }
            }
        }
    }

    fun setCellTextByField(table: ControlTable, fieldName: String, fieldText: String) {
        val cellList = CellFinder.findAll(table, fieldName)
        for (c in cellList) {
            val firstPara = c.paragraphList.getParagraph(0)

            var paraText = firstPara.text
            if (paraText == null) {
                firstPara.createText()
                paraText = firstPara.text
            }
            paraText!!.insertString(0, fieldText)
        }
    }
}
