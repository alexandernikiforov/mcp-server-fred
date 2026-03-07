package ch.alni.mcp.fred.service;

/**
 * Represents a set of predefined economic data series used for financial and economic analysis. Each constant in this
 * enumeration represents a specific dataset commonly referenced in financial markets and research.
 */
public enum Series {
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
    BAMLH0A0HYM2,

    /**
     * US 10-Year Treasury Minus 2-Year Treasury Yield Spread. Represents the difference between the 10-year US Treasury
     * constant maturity rate and the 2-year US Treasury constant maturity rate. A widely used economic indicator for
     * assessing the yield curve and potential recession signals.
     */
    T10Y2Y,

    /**
     * US 10-Year Treasury Constant Maturity Rate. Represents the yield on a US Treasury security that has a constant
     * maturity of 10 years. The data is provided by the Federal Reserve and typically used as a benchmark for long-term
     * interest rates.
     */
    DGS10,

    /**
     * US 2-Year Treasury Constant Maturity Rate. Represents the yield on a US Treasury security that has a constant
     * maturity of 2 years. The data is provided by the Federal Reserve and typically used as a benchmark for short-term
     * interest rates.
     */
    DGS2,

    /**
     * US 10-Year Treasury Inflation-Indexed Yield (TIPS). Represents the yield on a US Treasury security with a
     * constant maturity of 10 years that is indexed to adjust for inflation. These securities are designed to protect
     * against inflation risk by adjusting both their principal and coupon payments according to changes in the Consumer
     * Price Index (CPI). This data is often used for evaluating real interest rates, investment decisions, and economic
     * indicators tied to inflation expectations.
     */
    DFII10
}
