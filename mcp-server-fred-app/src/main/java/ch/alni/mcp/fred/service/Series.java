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
     * ICE BofA BBB US Corporate Index Option-Adjusted Spread. The ICE BofA OASs are the calculated spreads between a
     * computed OAS index of all bonds in a given rating category and a spot Treasury curve. An OAS index is constructed
     * using each constituent bond's OAS, weighted by market capitalization. When the last calendar day of the month
     * takes place on the weekend, weekend observations will occur as a result of month ending accrued interest
     * adjustments.
     */
    BAMLC0A4CBBB,

    /**
     * Represents the BAMLH0A1HYBB economic data series, which is part of the ICE BofA High Yield Index. This series
     * specifically tracks the performance of U.S. dollar-denominated below-investment-grade corporate bonds with
     * maturities between 1 and 3 years.
     * <p>
     * It serves as a key benchmark for analyzing trends in the high-yield bond market and is often used in financial
     * and economic analysis to assess risk and return in this sector.
     */
    BAMLH0A1HYBB,

    /**
     * Represents the ICE BofA High Yield Corporate Bond Option-Adjusted Spread (OAS) with a maturity classification of
     * 2 years. This economic indicator, provided by the FRED API, measures the spread of high-yield corporate bonds
     * relative to a comparable risk-free rate. The value can be used to analyze credit risk, market sentiment, and
     * overall economic conditions.
     */
    BAMLH0A2HYB,

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
     * US 30-Year Treasury Constant Maturity Rate. Represents the yield on a US Treasury security that has a constant
     * maturity of 30 years. The data is provided by the Federal Reserve and typically used as a benchmark for long-term
     * interest rates.
     */
    DGS30,

    /**
     * US 10-Year Treasury Inflation-Indexed Yield (TIPS). Represents the yield on a US Treasury security with a
     * constant maturity of 10 years that is indexed to adjust for inflation. These securities are designed to protect
     * against inflation risk by adjusting both their principal and coupon payments according to changes in the Consumer
     * Price Index (CPI). This data is often used for evaluating real interest rates, investment decisions, and economic
     * indicators tied to inflation expectations.
     */
    DFII10,

    /**
     * Represents the CBOE Volatility Index (VIX) close values as a part of economic data series. The VIX is a real-time
     * market index that reflects the market's expectations of 30-day forward-looking volatility derived from the prices
     * of S&P 500 index options. It is widely used as a measure of market risk.
     */
    VIXCLS,

    /**
     * Represents the Secured Overnight Financing Rate (SOFR), an economic indicator that measures the cost of borrowing
     * cash overnight collateralized by Treasury securities. This is one of several economic data series tracked within
     * the series enumeration.
     */
    SOFR,

    /**
     * Represents the Effective Federal Funds Rate (EFFR), which is the interest rate at which depository institutions
     * (such as banks and credit unions) lend reserve balances to other depository institutions overnight on an
     * uncollateralized basis. The EFFR is an important economic indicator reflecting monetary policy and market
     * interest rates in the United States.
     */
    EFFR,

    /**
     * Represents the T10YIE series, which is the 10-Year Breakeven Inflation Rate.
     * <p>
     * This economic indicator reflects the expected inflation over the next 10 years derived from the difference
     * between the 10-Year Treasury Nominal Yield (DGS10) and the 10-Year Treasury Inflation-Indexed Security (DFII10).
     * It is widely used by economists and policymakers to gauge expectations of future inflation.
     */
    T10YIE
}
