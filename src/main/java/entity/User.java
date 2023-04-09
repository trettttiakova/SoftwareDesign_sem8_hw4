package entity;

import currency.Currency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import server.QueryParamNames;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class User {
    private final int id;
    private final String name;
    private final String login;
    private final Currency currency;

    public User(Document doc) {
        this(
            doc.getDouble(QueryParamNames.ID.getParamName()).intValue(),
            doc.getString(QueryParamNames.NAME.getParamName()),
            doc.getString(QueryParamNames.LOGIN.getParamName()),
            Currency.valueOf(doc.getString(QueryParamNames.CURRENCY.getParamName()))
        );
    }

    public Document getDocument() {
        return new Document(QueryParamNames.ID.getParamName(), id)
            .append(QueryParamNames.NAME.getParamName(), name)
            .append(QueryParamNames.LOGIN.getParamName(), login)
            .append(QueryParamNames.CURRENCY.getParamName(), currency);
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", login='" + login + '\'' +
            ", currency='" + currency + '\'' +
            '}';
    }

    public static Collection<QueryParamNames> getRequiredParamNames() {
        return List.of(
            QueryParamNames.ID,
            QueryParamNames.NAME,
            QueryParamNames.LOGIN,
            QueryParamNames.CURRENCY
        );
    }
}
