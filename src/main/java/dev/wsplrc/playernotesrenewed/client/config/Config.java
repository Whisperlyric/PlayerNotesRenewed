package dev.wsplrc.playernotesrenewed.client.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig {

    public static final String GENERAL = "General";

    @Comment(category = GENERAL, centered = true) public static Comment tablist;
    @Entry(category = GENERAL) public static boolean editTablist = true;
    @Entry(category = GENERAL) public static boolean showPrefixForOwn = false;
    @Entry(category = GENERAL) public static boolean changeTabOrder = true;
    @Entry(category = GENERAL) public static boolean changeTabOrderOwn = false;
    @Comment(category = GENERAL, centered = true) public static Comment nameTag;
    @Entry(category = GENERAL) public static boolean editNameTags = true;
    
    // TODO: 聊天栏显示前后缀功能待实现
    // @Comment(category = GENERAL, centered = true) public static Comment chat;
    // @Entry(category = GENERAL) public static boolean showPrefixInChat = true;
    // @Entry(category = GENERAL) public static boolean showSuffixInChat = true;
    // @Comment(category = GENERAL, centered = true) public static Comment style;
    // @Entry(category = GENERAL) public static boolean styleAffectPlayerName = false;
    
    @Comment(category = GENERAL, centered = true) public static Comment autoUpdate;
    @Entry(category = GENERAL, min = 1, max = 300) public static int autoUpdateInterval = 15;
}
