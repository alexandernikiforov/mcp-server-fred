# Tags and Taxonomies

| Поле MCP         | Taxonomy / tag                                                | Комментарий                                                                             |
|------------------|---------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| Revenue          | `us-gaap:RevenueFromContractWithCustomerExcludingAssessedTax` | Главный современный revenue tag. Нужны fallback aliases для старых/отраслевых filing'ов |
| Gross profit     | `us-gaap:GrossProfit`                                         | Обычно прямой факт                                                                      |
| Operating income | `us-gaap:OperatingIncomeLoss`                                 | Прямой факт                                                                             |
| Net income       | `us-gaap:NetIncomeLoss`                                       | Прямой факт                                                                             |
| Diluted EPS      | `us-gaap:EarningsPerShareDiluted`                             | Unit обычно `USD/shares`                                                                |
| CFO              | `us-gaap:NetCashProvidedByUsedInOperatingActivities`          | SEC прямо приводит этот concept как правильный для operating cash flow. ([SEC][1])      |
| Capex            | `us-gaap:PaymentsToAcquirePropertyPlantAndEquipment`          | Обычно основной PP&E capex tag; нужны fallbacks                                         |
| FCF              | derived                                                       | `CFO - Capex`; стандартного GAAP concept `FreeCashFlow` нет                             |
| Cash             | `us-gaap:CashAndCashEquivalentsAtCarryingValue`               | Это именно cash & equivalents, не обязательно short-term investments                    |
| Total debt       | derived from several debt tags                                | Единого универсально надёжного `TotalDebt` лучше не предполагать                        |
| Diluted shares   | `us-gaap:WeightedAverageNumberOfDilutedSharesOutstanding`     | Для EPS denominator; это не period-end shares                                           |

# Canonical Metrics

|  # | Canonical metric       | Основной taxonomy / XBRL concept                              | Type             | Unit      | Как использовать / caveat                                                                                                            |
|---:|------------------------|---------------------------------------------------------------|------------------|-----------|--------------------------------------------------------------------------------------------------------------------------------------|
|  1 | `revenue`              | `us-gaap:RevenueFromContractWithCustomerExcludingAssessedTax` | Duration         | USD       | Основной современный revenue concept. Fallback: `RevenueFromContractWithCustomerIncludingAssessedTax`, `SalesRevenueNet`, `Revenues` |
|  2 | `grossProfit`          | `us-gaap:GrossProfit`                                         | Duration         | USD       | Прямой GAAP fact. Может отсутствовать у банков и некоторых business models                                                           |
|  3 | `operatingIncome`      | `us-gaap:OperatingIncomeLoss`                                 | Duration         | USD       | Использовать для operating margin                                                                                                    |
|  4 | `netIncome`            | `us-gaap:NetIncomeLoss`                                       | Duration         | USD       | GAAP bottom line; может сильно искажаться one-offs                                                                                   |
|  5 | `dilutedEPS`           | `us-gaap:EarningsPerShareDiluted`                             | Duration         | USD/share | GAAP diluted EPS; не путать с adjusted/non-GAAP EPS                                                                                  |
|  6 | `operatingCashFlow`    | `us-gaap:NetCashProvidedByUsedInOperatingActivities`          | Duration         | USD       | CFO. В 10-Q часто YTD — требуется quarterization                                                                                     |
|  7 | `capex`                | `us-gaap:PaymentsToAcquirePropertyPlantAndEquipment`          | Duration         | USD       | Основной capex concept. Возможны aliases/extensions                                                                                  |
|  8 | `freeCashFlow`         | Derived: `CFO - Capex`                                        | Derived duration | USD       | Не GAAP/XBRL fact. Формулу и provenance исходных значений надо возвращать явно                                                       |
|  9 | `cashAndEquivalents`   | `us-gaap:CashAndCashEquivalentsAtCarryingValue`               | Instant          | USD       | Состояние на balance-sheet date                                                                                                      |
| 10 | `shortTermInvestments` | Несколько возможных `us-gaap` concepts                        | Instant          | USD       | Лучше нормализовать отдельно; конкретный mapping зависит от presentation компании                                                    |
| 11 | `totalDebt`            | Derived из current + non-current debt concepts.               | Derived instant  | USD       | Не полагаться на один универсальный tag; избегать double counting overlapping debt/lease concepts;                                   |
| 12 | `dilutedShares`        | `us-gaap:WeightedAverageNumberOfDilutedSharesOutstanding`     | Duration         | shares    | Средневзвешенное число diluted shares за период; это не shares outstanding на конец квартала                                         |

## Total Debt

Для totalDebt я бы предусмотрел несколько допустимых схем. Например, базовая:

```
LongTermDebtCurrent
+ ShortTermBorrowings
+ LongTermDebtNoncurrent
```

но у другой компании могут использоваться объединённые concepts вроде:

```
LongTermDebtAndFinanceLeaseObligationsCurrent
LongTermDebtAndFinanceLeaseObligationsNoncurrent
```

Их нельзя бездумно складывать одновременно с более узкими debt tags.

## Derived Concepts

Поверх этих 12 сервер уже может рассчитывать derived series без поиска дополнительных SEC concepts:

| Derived metric          | Формула                                     |
|-------------------------|---------------------------------------------|
| `revenueYoY`            | `Revenue(Q) / Revenue(Q-4) - 1`             |
| `revenueQoQ`            | `Revenue(Q) / Revenue(Q-1) - 1`             |
| `grossMargin`           | `GrossProfit / Revenue`                     |
| `operatingMargin`       | `OperatingIncome / Revenue`                 |
| `fcfMargin`             | `FCF / Revenue`                             |
| `netDebt`               | `TotalDebt - Cash - ShortTermInvestments`   |
| `dilutedShareGrowthYoY` | `DilutedShares(Q) / DilutedShares(Q-4) - 1` |

[1]: https://www.sec.gov/file/xbrl-guide-2026-01-16 "EDGAR XBRL Guide, January 2026"

