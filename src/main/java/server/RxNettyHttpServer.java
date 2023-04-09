package server;

import com.mongodb.rx.client.Success;
import currency.Currency;
import driver.ReactiveMongoDriver;
import entity.Product;
import entity.User;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

import java.util.List;
import java.util.Map;

public class RxNettyHttpServer {
    public static void main(String[] args) {
        HttpServer
            .newServer(8080)
            .start((req, resp) -> {
                Observable<String> response;
                String name = req.getDecodedPath().substring(1);
                Map<String, List<String>> queryParams = req.getQueryParameters();

                switch (name) {
                    case "createUser" -> {
                        response = createUser(queryParams);
                        resp.setStatus(HttpResponseStatus.OK);
                    }
                    case "createProduct" -> {
                        response = createProduct(queryParams);
                        resp.setStatus(HttpResponseStatus.OK);
                    }
                    case "getProductsForUser" -> {
                        response = getProductsForUser(queryParams);
                        resp.setStatus(HttpResponseStatus.OK);
                    }
                    default -> {
                        response = Observable.just("Unsupported request");
                        resp.setStatus(HttpResponseStatus.BAD_REQUEST);
                    }
                }
                return resp.writeString(response);
            })
            .awaitShutdown();
    }

    private static Observable<String> getProductsForUser(Map<String, List<String>> queryParams) {
        var requiredParams = Product.getRequiredParamNamesToGet();
        if (
            requiredParams.stream()
                .anyMatch(param -> !queryParams.containsKey(param.getParamName()))
        ) {
            return Observable.just(
                "Bad request: getProductsForUser request requires " + requiredParams + " parameters"
            );
        }

        int userId = Integer.parseInt(queryParams.get(QueryParamNames.ID.getParamName()).get(0));

        Observable<String> products = ReactiveMongoDriver.getProductsForUser(userId);
        return Observable
            .just("{ user_id = " + userId + ", products = [")
            .concatWith(products)
            .concatWith(Observable.just("]}"));
    }

    private static Observable<String> createProduct(Map<String, List<String>> queryParams) {
        var requiredParams = Product.getRequiredParamNames();
        if (
            requiredParams.stream()
                .anyMatch(param -> !queryParams.containsKey(param.getParamName()))
        ) {
            return Observable.just(
                "Bad request: createProduct request requires " + requiredParams + " parameters"
            );
        }

        int id = Integer.parseInt(queryParams.get(QueryParamNames.ID.getParamName()).get(0));
        String name = queryParams.get(QueryParamNames.NAME.getParamName()).get(0);
        String category = queryParams.get(QueryParamNames.CATEGORY.getParamName()).get(0);
        int rubPrice = Integer.parseInt(queryParams.get(QueryParamNames.RUB.getParamName()).get(0));
        int usdPrice = Integer.parseInt(queryParams.get(QueryParamNames.USD.getParamName()).get(0));
        int eurPrice = Integer.parseInt(queryParams.get(QueryParamNames.EUR.getParamName()).get(0));

        Product newProduct = new Product(
            id,
            name,
            category,
            Map.of(
                Currency.RUB, rubPrice,
                Currency.USD, usdPrice,
                Currency.EUR, eurPrice
            )
        );
        if (ReactiveMongoDriver.createProduct(newProduct) == Success.SUCCESS) {
            return Observable.just("SUCCESS");
        } else {
            return Observable.just("Error creating product");
        }
    }

    private static Observable<String> createUser(Map<String, List<String>> queryParams) {
        var requiredParams = User.getRequiredParamNames();
        if (
            requiredParams.stream()
                .anyMatch(param -> !queryParams.containsKey(param.getParamName()))
        ) {
            return Observable.just(
                "Bad request: createUser request requires " + requiredParams + " parameters"
            );
        }

        int id = Integer.parseInt(queryParams.get(QueryParamNames.ID.getParamName()).get(0));
        String name = queryParams.get(QueryParamNames.NAME.getParamName()).get(0);
        String login = queryParams.get(QueryParamNames.LOGIN.getParamName()).get(0);
        String currencyStr = queryParams.get(QueryParamNames.CURRENCY.getParamName()).get(0);
        Currency currency = Currency.valueOf(currencyStr);

        User newUser = new User(id, name, login, currency);
        if (ReactiveMongoDriver.createUser(newUser) == Success.SUCCESS) {
            return Observable.just("SUCCESS");
        } else {
            return Observable.just("Error creating user");
        }
    }
}
