package com.avaricious.utility;

public enum ZIndex {
    WARP_BACKGROUND(0),
    TEXTURE_ECHO(0),
    DIGITAL_NUMBER(1),
    LIGHT_BULB_BORDER(1),
    SCORE_DISPLAY(1),
    TEXTURE_GLOW(1),
    PATTERN_DISPLAY(2),
    HEALTH_UI(4),
    UPGRADE_BAR(5),
    XP_BAR(5),
    DECK_UI_BOX(6),
    DECK_UI_CARD(6),
    RELIC_BAG(7),
    GLOW_BORDER_CARD_DESTINATION(9),
    SYMBOL_HIT_PARTICLES(8),
    CARD_APPLY_PARTICLES(9),
    HAND_UI_CARD(10),
    RING_BAR(10),
    SLOT_MACHINE(10),
    BUTTON_BOARD(11),
    ROUND_INFO_PANEL_UNFOLDED(12),
    HAND_UI_CARD_DRAGGING(12),
    RING_BAR_DRAGGING(12),
    CREDIT_SCORE(14),
    SHOP(14),
    STATUS_UPGRADE_WINDOW(15),
    SHOP_CARD(15),
    SHOP_CARD_TOUCHING(16),
    POPUP_DEFAULT(16),
    PACK_OPENING_BACKGROUND(16),
    PACK_OPENING(17),
    PACK_OPENING_SELECTED(18),
    DIMMED_BACKGROUND(20),
    UNFOLDED_DECK_BACKGROUND(20),
    UNFOLDED_DECK_CARD(25);

    private final int index;

    ZIndex(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }
}
