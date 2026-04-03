# Automatic SEC report downloads” (10-K / 10-Q / 8-K etc.) using **official SEC endpoints**

(Also: you asked for “the right direction” — this is it. Everything else is convenience tooling.)

---

## 1) Start with the official SEC “EDGAR APIs” + fair-access rules

SEC’s landing page for the APIs is here (lists the available JSON endpoints and examples). ([SEC][1])

**Non-negotiable operational constraints:**

* **Identify yourself with a real User-Agent** (include contact email).
* **Rate limit**: SEC has explicitly enforced automated access controls; a commonly cited hard ceiling is **10
  requests/second** across your clients. ([SEC][2])

If you build an MCP server, you enforce these constraints centrally (global token bucket + backoff).

---

## 2) The core endpoints you’ll actually use

### A) “Submissions” API (company filings index)

This gives you the company’s recent filings list, including accession numbers and document links.

Pattern (note the zero-padded CIK):

* `https://data.sec.gov/submissions/CIK0000320193.json` (Apple example)

Use this to:

* enumerate filings
* find the latest 10-K / 10-Q
* get the accession number, filing date, and primary document

(SEC documents this family on the EDGAR APIs page.) ([SEC][1])

### B) EDGAR Archives (download the actual filing files)

Once you have `cik` + `accessionNumber`, you fetch filing artifacts from:

* `https://www.sec.gov/Archives/edgar/data/{cik}/{accession_no_nodashes}/{primaryDocument}`

This is how you download the HTML filing, plus exhibits, plus the complete filing directory.

### C) XBRL “Company Facts” API (structured fundamentals)

If you want machine-readable financial statements (revenues, EPS, etc.), use:

* `https://data.sec.gov/api/xbrl/companyfacts/CIK0000320193.json`

This is a big JSON tree keyed by taxonomy tags.

### D) XBRL “Company Concept” API (one tag, time series)

For one concept (e.g., Assets) across periods:

* `https://data.sec.gov/api/xbrl/companyconcept/CIK0000320193/us-gaap/Assets.json`

### E) XBRL “Frames” API (cross-company slice)

For a tag across many filers in a period (useful for screening):

* `https://data.sec.gov/api/xbrl/frames/us-gaap/Assets/USD/CY2024Q4I.json`

Again, the SEC’s EDGAR APIs page is the authoritative directory. ([SEC][1])

---

## 3) Minimal “download latest 10-K” algorithm (what your MCP server will do)

**Inputs:** ticker or CIK, desired form type(s), “latest”, optionally date bounds.

1. **Resolve ticker → CIK**

* Maintain a local mapping (SEC provides datasets; many devs cache this).

2. Fetch **Submissions JSON** for that CIK.
3. Filter `filings.recent` by `form == "10-K"` (or `"10-Q"`, etc.).
4. Choose the most recent by filing date.
5. Construct the **Archives URL** for `primaryDocument` and download it.
6. Store to disk/object store with a deterministic key:

* `{cik}/{accession}/{form}/{filingDate}/primary.html`

7. Return metadata + a stable pointer to your stored artifact.

---

## 4) Concrete request examples (curl)

```bash
# 1) Get company filings index (Submissions)
curl -H "User-Agent: YourName your.email@domain.com" \
  "https://data.sec.gov/submissions/CIK0000320193.json"

# 2) Get structured XBRL facts
curl -H "User-Agent: YourName your.email@domain.com" \
  "https://data.sec.gov/api/xbrl/companyfacts/CIK0000320193.json"
```

Then, after you parse accession + primary doc, you download the filing:

```bash
curl -H "User-Agent: YourName your.email@domain.com" \
  "https://www.sec.gov/Archives/edgar/data/320193/000032019324000123/a10-k20240928.htm" \
  -o apple-10k.html
```

(That last URL is an example format; you must build it from the Submissions response.)

---

## 5) Index files are your “bulk mode” option (optional, but useful)

If you want *daily/quarterly discovery* (e.g., “download every 10-Q filed yesterday”), use EDGAR index files:

* Daily index directory: `.../Archives/edgar/daily-index/` ([SEC][3])
* Full index directory: `.../Archives/edgar/full-index/` ([SEC][4])
  SEC also highlights index availability in “Developer Resources.” ([SEC][5])

These let you build a pipeline:

* Pull yesterday’s master index
* Filter for forms of interest
* Download filings in batch (again respecting rate controls)

---

## 6) MCP server shape (practical, not theoretical)

Define a small tool surface area; everything else is composition:

* `sec.resolve_cik(ticker_or_cik) -> {cik, name}`
* `sec.list_filings(cik, forms, since, until) -> [{accession, form, filedAt, primaryDocument, items…}]`
* `sec.download_filing(cik, accession, document="primary") -> {storedUri, contentType, sha256}`
* `sec.get_xbrl_facts(cik) -> json`
* `sec.get_xbrl_concept(cik, taxonomy, tag) -> json`

**Hard requirements inside the server:**

* Global rate limiter (token bucket), max 10 r/s, plus exponential backoff on 429/403. ([SEC][2])
* Required `User-Agent` injection on every outgoing request. ([SEC][1])
* Disk/cache layer (avoid re-downloading the same accession repeatedly).

---

## 7) “Don’t reinvent parsing” note (optional tooling)

If you want convenience wrappers, there are third-party Python packages and hosted APIs — but I’d treat those as
*optional* after you can do it with raw endpoints. The official path above is enough to build a robust
collector. ([SEC][1])

---

If you want, next step: I’ll outline a **tiny reference implementation** (Python or Node) that:

* resolves ticker→CIK,
* fetches Submissions,
* downloads the latest 10-K into a local folder,
* and logs metadata in JSON.

(Separate note: your “mechanical rules” doc is unrelated to SEC ingestion, but I’m including a pointer since it’s part
of your project’s binding framework.)


[1]: https://www.sec.gov/search-filings/edgar-application-programming-interfaces?utm_source=chatgpt.com "EDGAR Application Programming Interfaces (APIs)"

[2]: https://www.sec.gov/filergroup/announcements-old/new-rate-control-limits?utm_source=chatgpt.com "SEC to apply new rate control limits to EDGAR websites"

[3]: https://www.sec.gov/Archives/edgar/daily-index/?utm_source=chatgpt.com "Directory listing of daily-index/"

[4]: https://www.sec.gov/Archives/edgar/full-index/?utm_source=chatgpt.com "Directory listing of full-index/"

[5]: https://www.sec.gov/about/developer-resources?utm_source=chatgpt.com "Developer Resources"
