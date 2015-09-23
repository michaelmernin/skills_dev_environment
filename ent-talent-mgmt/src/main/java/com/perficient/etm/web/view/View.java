package com.perficient.etm.web.view;

public class View {
    public static class Public {}
    public static class Peer extends Public {}
    public static class Counselee extends Peer {}
    public static class Private extends Counselee {}
}
