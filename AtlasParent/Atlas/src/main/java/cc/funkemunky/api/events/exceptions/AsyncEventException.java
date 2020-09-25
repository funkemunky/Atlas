package cc.funkemunky.api.events.exceptions;

import cc.funkemunky.api.events.AtlasEvent;

public class AsyncEventException extends Exception {

    public AsyncEventException(AtlasEvent event) {
        super("Event " + event.getClass().getSimpleName()
                + " is cancellable and therefore cannot be used asynchronously.");
    }
}
