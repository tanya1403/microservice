package com.homefirst.kyc.utils

import org.json.JSONObject
import org.springframework.stereotype.Component

@Component
class OneResponse {

    object OneResponse {
        fun operationFailedResponse(message: String): String {
            return JSONObject()
                .put("success", false)
                .put("message", message)
                .put("data", JSONObject.NULL)
                .toString()
        }

        fun getSuccessResponse(data: JSONObject): String {
            return JSONObject()
                .put("success", true)
                .put("message", "Success")
                .put("data", data)
                .toString()
        }
    }
}