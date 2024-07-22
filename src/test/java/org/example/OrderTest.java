package org.example;

import io.restassured.response.Response;
import org.example.models.responses.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class OrderTest {
    public CourierApi courierApi = new CourierApi();
    public OrderApi orderApi = new OrderApi();

    public static final String login = "createCourier";
    public static final  String password = "1";

    public static final String firstName = "Ivan";
    public static final String lastName = "Ivanov";
    public static final String address = "Тверская, 21";
    public static final int metroStation = 21;
    public static final String phone = "+7 916 999 99 21";
    public static final int rentTime = 5;
    public static final String deliveryDate = "2024-07-30";
    public static final String comment = "Test Comment";
    public static final List<String> color = List.of("BLACK");



    @DisplayName("Creating an order with 'BLACK', 'GRAY' and no color.")
    @ParameterizedTest()
    @CsvSource({
            "BLACK",
            "GRAY"
    })
    @EmptySource
    public void testCreateOrderWithTwoColors(String colorElement) {
        List<String> color = new ArrayList<>();
        if (colorElement != null && !colorElement.isEmpty()) {
            color.add(colorElement);
        }
        Response response = orderApi.createOrder(
                firstName,
                lastName,
                address,
                metroStation,
                phone,
                rentTime,
                deliveryDate,
                comment,
                color
        );
        assertEquals(201, response.statusCode());

        OrdersCreateResponse responseOrder = response.then().extract().as(OrdersCreateResponse.class);
        assertNotNull(responseOrder.getTrack());
    }

    @Test
    public void testGetOrderList() {
        // Создание курьера
        Response responseCourier = courierApi.createCourier(login, password, firstName);
        assertEquals(201, responseCourier.statusCode());

        CourierCreateResponse courier = responseCourier.then().extract().body().as(CourierCreateResponse.class);
        assertTrue(courier.isOk());

        // Логин курьера и получение его id
        Response responseLogin = courierApi.loginCourier(login, password);
        assertEquals(200, responseLogin.statusCode());

        CourierLoginResponse courierLoginResponse = responseLogin.then().extract().body().as(CourierLoginResponse.class);
        Long courierId = courierLoginResponse.getId();
        assertNotNull(courierId);

        // Создание заказа
        Response responseOrder = orderApi.createOrder(
                firstName,
                lastName,
                address,
                metroStation,
                phone,
                rentTime,
                deliveryDate,
                comment,
                color
        );
        assertEquals(201, responseOrder.statusCode());

        OrdersCreateResponse order = responseOrder.then().extract().as(OrdersCreateResponse.class);
        Long orderId = order.getTrack();
        assertNotNull(orderId);

        // Приемка заказа курьером
        Response responseAcceptOrder = orderApi.acceptOrder(orderId, courierId);
        assertEquals(200, responseAcceptOrder.statusCode());

        OrdersAcceptResponse acceptOrder = responseAcceptOrder.then().extract().body().as(OrdersAcceptResponse.class);
        assertTrue(acceptOrder.isOk());

        // Получение списка заказов по курьеру
        Response responseOrderList = orderApi.getOrders(courierId);
        assertEquals(200, responseOrderList.statusCode());

        OrdersGetListResponse orderList = responseOrderList.then().extract().body().as(OrdersGetListResponse.class);
        assertNotNull(orderList.getOrders());
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        System.out.println("TestInfo: " + testInfo);
        System.out.println("Tags: " + testInfo.getTags());

        if (testInfo.getDisplayName().equals("testGetOrderList()")) {
            CourierLoginResponse loginResponse = courierApi
                    .loginCourier(login, password)
                    .then().extract().body().as(CourierLoginResponse.class);
            Long courierId = loginResponse.getId();

            CourierDeleteIdResponse deleteResponse = courierApi
                    .deleteCourier(Long.toString(courierId))
                    .then().extract().body().as(CourierDeleteIdResponse.class);

            assertTrue(deleteResponse.isOk());
        }
    }
}
