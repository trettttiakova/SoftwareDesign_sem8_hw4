package entity;

import currency.Currency;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import server.QueryParamNames;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@AllArgsConstructor
public class Product {
    private final int id;
    private final String name;
    private final String category;
    private final Map<Currency, Integer> prices;

    public Product(Document doc) {
        this(
            doc.getDouble(QueryParamNames.ID.getParamName()).intValue(),
            doc.getString(QueryParamNames.NAME.getParamName()),
            doc.getString(QueryParamNames.CATEGORY.getParamName()),
            Map.of(
                Currency.RUB, Integer.parseInt(doc.getString(QueryParamNames.RUB.getParamName())),
                Currency.USD, Integer.parseInt(doc.getString(QueryParamNames.USD.getParamName())),
                Currency.EUR, Integer.parseInt(doc.getString(QueryParamNames.EUR.getParamName()))
            )
        );
    }

    public Document getDocument() {
        return new Document(QueryParamNames.ID.getParamName(), id)
            .append(QueryParamNames.NAME.getParamName(), name)
            .append(QueryParamNames.CATEGORY.getParamName(), category)
            .append(QueryParamNames.RUB.getParamName(), prices.get(Currency.RUB))
            .append(QueryParamNames.USD.getParamName(), prices.get(Currency.USD))
            .append(QueryParamNames.EUR.getParamName(), prices.get(Currency.EUR));
    }

    public String toString(Currency currency) {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", category='" + category + '\'' +
            ", price='" + prices.get(currency) + " " + currency + '\'' +
            '}';
    }

    public static Collection<QueryParamNames> getRequiredParamNames() {
        return List.of(
            QueryParamNames.ID,
            QueryParamNames.NAME,
            QueryParamNames.CATEGORY,
            QueryParamNames.RUB,
            QueryParamNames.USD,
            QueryParamNames.EUR
        );
    }

    public static Collection<QueryParamNames> getRequiredParamNamesToGet() {
        return List.of(
            QueryParamNames.ID // User id
        );
    }
}
