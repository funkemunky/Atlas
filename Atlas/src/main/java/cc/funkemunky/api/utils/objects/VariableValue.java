package cc.funkemunky.api.utils.objects;

import lombok.RequiredArgsConstructor;

import java.util.function.BooleanSupplier;

@RequiredArgsConstructor
public class VariableValue<T> {

    private final T isTrue;
    private final T isFalse;
    private final BooleanSupplier variable;

    public T get() {
        return variable.getAsBoolean() ? isTrue : isFalse;
    }
}
