package com.homefirst.microservices.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping

class publicController {

    @GetMapping("/hello",
        produces = [MediaType.TEXT_HTML_VALUE]
    )
    fun sayHey(): String {

        return ("<html> " + "<title>" + "HomeFirst Operations" + "</title>" + "<body><h1>"
                + "Successfully deployed HomeFirst Operations Application on ECS." + "</h1></body>" + "</html> ")
    }
}