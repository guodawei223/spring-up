package io.spring.up.core;

import io.spring.up.core.data.JsonObject;
import io.spring.up.exception.WebException;

public class Envelop {

    private WebException error;

    private JsonObject data;
}
