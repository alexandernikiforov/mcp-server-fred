package ch.alni.mcp.edgar.port;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single reported fact observation.
 *
 * @param start period start date
 * @param end   period end date
 * @param val   reported numeric value
 * @param accn  accession number
 * @param fy    fiscal year
 * @param fp    fiscal period
 * @param form  SEC form type
 * @param filed filing date
 * @param frame optional SEC frame identifier
 */
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyFact(
        LocalDate start,
        LocalDate end,
        BigDecimal val,
        String accn,
        Integer fy,
        String fp,
        String form,
        LocalDate filed,
        String frame
) {
}
