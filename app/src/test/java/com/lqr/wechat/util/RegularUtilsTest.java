package com.lqr.wechat.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegularUtilsTest {

    @Test
    public void isMobile_acceptsAllCurrentPrefixes() {
        assertTrue(RegularUtils.isMobile("13800138000"));
        assertTrue(RegularUtils.isMobile("14712345678"));
        assertTrue(RegularUtils.isMobile("15912345678"));
        assertTrue(RegularUtils.isMobile("16612345678"));
        assertTrue(RegularUtils.isMobile("17112345678"));
        assertTrue(RegularUtils.isMobile("18812345678"));
        assertTrue(RegularUtils.isMobile("19912345678"));
    }

    @Test
    public void isMobile_rejectsInvalidNumbers() {
        //The old pattern accepted "17|" because "|" inside [] is a literal char
        assertFalse(RegularUtils.isMobile("17|12345678"));
        assertFalse(RegularUtils.isMobile("12012345678"));
        assertFalse(RegularUtils.isMobile("1380013800"));
        assertFalse(RegularUtils.isMobile("138001380000"));
        assertFalse(RegularUtils.isMobile("1380013800a"));
        assertFalse(RegularUtils.isMobile(" 13800138000"));
        assertFalse(RegularUtils.isMobile(""));
    }

    @Test
    public void isMobile_rejectsNull() {
        assertFalse(RegularUtils.isMobile(null));
    }

    @Test
    public void isEmail_matchesValidAndRejectsInvalid() {
        assertTrue(RegularUtils.isEmail("someone@example.com"));
        assertTrue(RegularUtils.isEmail("first.last-1@mail.example.cn"));
        assertFalse(RegularUtils.isEmail("someone@"));
        assertFalse(RegularUtils.isEmail("someone.example.com"));
        assertFalse(RegularUtils.isEmail(""));
        assertFalse(RegularUtils.isEmail(null));
    }
}
