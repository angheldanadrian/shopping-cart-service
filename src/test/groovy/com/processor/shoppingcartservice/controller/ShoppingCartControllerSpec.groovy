package com.processor.shoppingcartservice.controller

import spock.lang.Specification

class ShoppingCartControllerSpec extends Specification {

    def shoppingCartController = new ShoppingCartController()

    def "Should return status code 200 OK when calling the shoppingCartController health-check endpoint"() {
        given:
        def responseOk = 200

        when:
        def response = shoppingCartController.statusHealth()

        then:
        response.getStatusCode().value() == responseOk
    }
}
