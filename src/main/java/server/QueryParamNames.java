package server;

import currency.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum QueryParamNames {
    ID("id"),
    NAME("name"),
    LOGIN("login"),
    CURRENCY("currency"),
    CATEGORY("category"),
    RUB(Currency.RUB.name()),
    USD(Currency.USD.name()),
    EUR(Currency.EUR.name())
    ;

    private final String paramName;
}
