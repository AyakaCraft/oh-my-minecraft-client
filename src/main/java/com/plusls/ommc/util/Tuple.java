package com.plusls.ommc.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Tuple<A, B> {
    private A a;
    private B b;

    public Tuple(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

}
