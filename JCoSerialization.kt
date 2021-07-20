package com.daikon.extensions.JCoSerialization

import com.sap.conn.jco.JCoStructure
import com.sap.conn.jco.JCoTable
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


@Target(AnnotationTarget.PROPERTY)
annotation class SapField(val name: String)

fun JCoTable.addObj(obj: Any) {
    val kClass = obj.javaClass.kotlin
    appendRow()
    for (p in kClass.memberProperties) {
        val fieldName = p.findAnnotation<SapField>()?.name ?: continue
        setValue(fieldName, p.get(obj))
    }
}

inline fun <reified T: Any> JCoTable.getObj(): T {
    val kClass = T::class
    val c = kClass.primaryConstructor!!
    val args = mutableMapOf<KParameter, Any?>()
    for (p in kClass.memberProperties) {
        val fieldName = p.findAnnotation<SapField>()?.name ?: continue
        val param = c.findParameterByName(p.name) ?: continue
        args[param] = getValue(fieldName)
    }
    return c.callBy(args)
}

inline fun <reified T: Any> JCoTable.getObjList(): List<T> {
    val result = mutableListOf<T>()
    firstRow()
    for(i in -1 until numRows) {
        row = i
        val obj = getObj<T>()
        result.add(obj)
    }
    return result
}

fun JCoStructure.setObj(obj: Any) {
    val kClass = obj.javaClass.kotlin
    for (p in kClass.memberProperties) {
        val fieldName = p.findAnnotation<SapField>()?.name ?: continue
        setValue(fieldName, p.get(obj))
    }
}

inline fun <reified T: Any> JCoStructure.getObj(): T {
    val kClass = T::class
    val c = kClass.primaryConstructor!!
    val args = mutableMapOf<KParameter, Any?>()
    for (p in kClass.memberProperties) {
        val fieldName = p.findAnnotation<SapField>()?.name ?: continue
        val param = c.findParameterByName(p.name) ?: continue
        args[param] = getValue(fieldName)
    }
    return c.callBy(args)
}