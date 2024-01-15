package com.zeeyeh.devtoolkit.message;

@FunctionalInterface
public interface ColorFactory<O, T, U> {
    U apply(O origin, T text);
}
