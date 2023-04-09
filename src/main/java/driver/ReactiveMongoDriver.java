package driver;


import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import entity.Product;
import entity.User;
import org.bson.Document;
import rx.Observable;
import server.QueryParamNames;

import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class ReactiveMongoDriver {
    private final static String DATABASE_NAME = "rxtest";
    private final static String USERS_COLLECTION = "users";
    private final static String PRODUCTS_COLLECTION = "products";

    private static MongoClient client = createMongoClient();

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    public static Success createUser(User user) {
        return insert(USERS_COLLECTION, user.getDocument());
    }

    public static Success createProduct(Product product) {
        return insert(PRODUCTS_COLLECTION, product.getDocument());
    }

    public static Observable<String> getProductsForUser(int userId) {
        return client
            .getDatabase(DATABASE_NAME)
            .getCollection(PRODUCTS_COLLECTION)
            .find()
            .toObservable()
            .map(
                document -> new Product(document)
                    .toString(getUser(userId)
                        .getCurrency())
            )
            .reduce((p1, p2) -> p1 + ", " + p2);
    }

    private static User getUser(int userId) {
        return client
            .getDatabase(DATABASE_NAME)
            .getCollection(USERS_COLLECTION)
            .find(eq(QueryParamNames.ID.getParamName(), userId))
            .first()
            .map(User::new)
            .timeout(5000, TimeUnit.MILLISECONDS)
            .toBlocking()
            .single();
    }

    private static Success insert(String collection, Document document) {
        return client
            .getDatabase(DATABASE_NAME)
            .getCollection(collection)
            .insertOne(document)
            .timeout(5000, TimeUnit.MILLISECONDS)
            .toBlocking()
            .single();
    }
}
