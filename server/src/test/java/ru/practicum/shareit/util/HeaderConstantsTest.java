package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderConstantsTest {

    @Test
    void headerConstantsClassShouldNotBeInstantiable() {
        HeaderConstants headerConstants = new HeaderConstants();
    }

    @Test
    void xSharerUserIdShouldBeCorrect() {
        assertEquals("X-Sharer-User-Id", HeaderConstants.X_SHARER_USER_ID);
    }
}
