package com.whoever.coronatracker.models.data

data class JHUReport (
    val objectIdFieldName: String,
    val uniqueIDField: UniqueIDField,
    val globalIDFieldName: String,
    val geometryType: String,
    val spatialReference: SpatialReference,
    val fields: List<Field>,
    val features: List<Feature>
)

data class Feature (
    val attributes: Attributes
)

data class Attributes (
    val OBJECTID: Long,
    val Country_Region: String,
    val Province_State: String,
    val Last_Update: Long,
    val Lat: Double,
    val Long_: Double,
    val Confirmed: Long,
    val Deaths: Long,
    val Recovered: Long,
    val Active: Long
)

data class Field (
    val name: String,
    val type: String,
    val alias: String,
    val sqlType: String,
    val domain: Any? = null,
    val defaultValue: Any? = null,
    val length: Long? = null
)

data class SpatialReference (
    val wkid: Long,
    val latestWkid: Long
)

data class UniqueIDField (
    val name: String,
    val isSystemMaintained: Boolean
)