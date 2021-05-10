package io.shinoharahare.jsbridge

import org.json.JSONArray
import org.json.JSONObject

abstract class JSONAble {
    companion object {
//        fun from(v: Any) : String {
//
//        }

        fun from(c: Collection<*>) : String {
            return JSONArray(c).toString()
        }

        fun from(c: Array<*>) : String {
            return JSONArray(c).toString()
        }
    }
    abstract fun toJSON() : String
}
