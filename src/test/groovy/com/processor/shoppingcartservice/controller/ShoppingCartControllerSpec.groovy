package com.processor.shoppingcartservice.controller

import spock.lang.Specification

class ShoppingCartControllerSpec extends Specification {

    def healthCheckController = new HealthcheckController()

    def "Should return status code 200 OK when calling the healthCheckController health-check endpoint"() {
        given:
        def responseOk = 200

        when:
        def response = healthCheckController.statusHealth()

        then:
        response.getStatusCode().value() == responseOk
    }
}
