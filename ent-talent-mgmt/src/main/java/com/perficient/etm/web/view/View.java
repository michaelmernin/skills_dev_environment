package com.perficient.etm.web.view;

public class View {
    public static class Identity {}
    public static class Public extends Identity {}
    public static class Peer extends Public {}
    public static class Counselee extends Peer {}
    public static class Private extends Counselee {}
}
