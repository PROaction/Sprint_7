package org.example;

import io.restassured.response.Response;
import org.example.models.requests.CourierCreateRequest;
import org.example.models.requests.CourierLoginRequest;

public class CourierApi extends BaseHttpClient{

    private final String apiPath = "/api/v1/courier";

    public Response loginCourier(String login, String password) {
        CourierLoginRequest request = new CourierLoginRequest(login, password);
        return doPostRequest(apiPath + "/login", request);
    }

    public Response createCourier(String login, String password, String firstName) {
        CourierCreateRequest request = new CourierCreateRequest(login, password, firstName);
        return doPostRequest(apiPath, request);
    }

    public Response deleteCourier(String id) {
        return doDeleteRequest(apiPath + "/" + id);
    }
}
