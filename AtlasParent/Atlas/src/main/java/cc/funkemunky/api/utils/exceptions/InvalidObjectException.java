package cc.funkemunky.api.utils.exceptions;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvalidObjectException extends Exception {

    public InvalidObjectException(Object object, Class<?>... expected) {
        super("Object " + object.getClass().getSimpleName() + " was not expected. Expected {" +
                Stream.of(expected).map(Class::getSimpleName).collect(Collectors.joining(", ")) + "}");
    }
}
