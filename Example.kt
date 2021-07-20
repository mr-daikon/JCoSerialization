package com.daikon.extensions.JCoSerialization

import com.sap.conn.jco.JCoDestination
import com.sap.conn.jco.JCoException

data class FuncLoc(
    @SapField("FUNCLOC") val id: String,
    @SapField("DESCRIPT") val descr: String,
    @SapField("SUPFLOC") val parId: String,
    @SapField("SORTFIELD") val sortNo: String
)

data class Range(
    @SapField("SIGN") val sign: String,
    @SapField("OPTION") val option: String,
    @SapField("LOW") val lo: String,
    @SapField("HIGH") val hi: String
)

fun getFuncLocList(dest: JCoDestination, planPlant: String, planGroup: String): List<FuncLoc> {
    return try {
        val fnName = "BAPI_FUNCLOC_GETLIST"
        val fn = dest.repository.getFunction(fnName)
            ?: throw RuntimeException("Function $fnName not found")

        fn.tableParameterList.getTable("PLANPLANT_RA").addObj(Range("", "EQ", planPlant, ""))
        fn.tableParameterList.getTable("PLANGROUP_RA").addObj(Range("", "EQ", planGroup, ""))

        fn.execute(dest)

        fn.tableParameterList.getTable("FUNCLOC_LIST").getObjList()

    } catch (ex: JCoException) {
        ex.printStackTrace()
        listOf()
    }
}