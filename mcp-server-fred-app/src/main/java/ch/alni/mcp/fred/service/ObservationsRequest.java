package ch.alni.mcp.fred.service;

import lombok.Builder;

import java.time.LocalDate;

/**
 * Request parameters for the FRED “Series observations” endpoint.
 * <p>
 * This record mirrors the query parameters supported by
 * <a href="https://fred.stlouisfed.org/docs/api/fred/series_observations.html">/fred/series/observations</a>.
 * Use it to request time series observations for a given FRED series with optional real‑time (vintage) filtering, date
 * range limits, frequency aggregation and unit transformations.
 * </p>
 *
 * @param seriesId          Required. The FRED series identifier (e.g. {@code GDP}, {@code CPIAUCSL}).
 * @param realtimeStart     Optional. Real‑time period start (vintage) date. Returns values that were available on or
 *                          after this date. If {@code null}, the API default is used (earliest).
 * @param realtimeEnd       Optional. Real‑time period end (vintage) date. Returns values that were available on or
 *                          before this date. If {@code null}, the API default is used (latest).
 * @param limit             Optional. Maximum number of observations to return (API default applies if 0).
 * @param offset            Optional. Results offset for pagination; 0 means start from the first observation.
 * @param sortOrder         Optional. Sort order of observations by observation date; typical values are {@code asc} or
 *                          {@code desc}. If {@code null}, the API default is used.
 * @param observationStart  Optional. Observation date range start; only observations on/after this date are returned.
 * @param observationEnd    Optional. Observation date range end; only observations on/before this date are returned.
 * @param units             Optional. Units/transform applied to values (e.g. {@code lin}, {@code chg}, {@code pch},
 *                          {@code pc1}, {@code pca}, {@code cch}, {@code cca}, {@code log}). See docs for the full
 *                          list.
 * @param frequency         Optional. Frequency to aggregate observations to (e.g. daily, weekly, monthly, quarterly,
 *                          semiannual, annual). See docs for exact accepted values and codes.
 * @param aggregationMethod Optional. Aggregation method used when changing frequency (e.g. {@code avg}, {@code sum},
 *                          {@code eop}).
 * @param outputType        Optional. Output type flag as defined by the API. See the endpoint documentation for the
 *                          accepted numeric values and their meaning.
 * @param vintageDates      Optional. Comma‑separated list of vintage dates (YYYY‑MM‑DD) to return observations “as of”
 *                          those dates. If provided, it overrides {@code realtimeStart}/{@code realtimeEnd}.
 * @see <a href="https://fred.stlouisfed.org/docs/api/fred/series_observations.html">FRED API – Series observations</a>
 */
@Builder(toBuilder = true)
public record ObservationsRequest(
        String seriesId,
        LocalDate realtimeStart,
        LocalDate realtimeEnd,
        int limit,
        int offset,
        String sortOrder,
        LocalDate observationStart,
        LocalDate observationEnd,
        String units,
        String frequency,
        String aggregationMethod,
        String outputType,
        String vintageDates
) {
}
