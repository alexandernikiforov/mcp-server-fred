package ch.alni.mcp.fred.service;

/**
 * ICE BofA US Option-Adjusted Spreads.
 */
public enum Spread {
    /**
     * ICE BofA US Corporate Index Option-Adjusted Spread.
     * <p>
     * The ICE BofA Option-Adjusted Spreads (OASs) are the calculated spreads between a computed OAS index of all bonds
     * in a given rating category and a spot Treasury curve. An OAS index is constructed using each constituent bond's
     * OAS, weighted by market capitalization. The Corporate Master OAS uses an index of bonds that are considered
     * investment grade (those rated BBB or better). When the last calendar day of the month takes place on the weekend,
     * weekend observations will occur as a result of month ending accrued interest adjustments.
     */
    BAMLC0A0CM,

    /**
     * ICE BofA US High Yield Index Option-Adjusted Spread.
     * <p>
     * The ICE BofA Option-Adjusted Spreads (OASs) are the calculated spreads between a computed OAS index of all bonds
     * in a given rating category and a spot Treasury curve. An OAS index is constructed using each constituent bond's
     * OAS, weighted by market capitalization. The ICE BofA High Yield Master II OAS uses an index of bonds that are
     * below investment grade (those rated BB or below).
     */
    BAMLH0A0HYM2
}
