# MediPredict
MediPredict is a Java-based AI-powered medicine demand forecasting and inventory management system for Jan Aushadhi stores and pharmacies. It predicts stockouts, flags expiry risks via FEFO, and recommends smart reorders using statistical forecasting — reducing medicine shortages and wastage across healthcare centers.

 #MediPredict – AI-Powered Medicine Demand & Inventory Management System

MediPredict is a Java desktop application for **Jan Aushadhi stores, pharmacies
and small healthcare centers across India**. It uses historical sales data,
configurable seasonal patterns and lightweight statistical forecasting to
answer one question:

> **What medicines should we order, how much, when, and which ones need
> immediate attention?**

MediPredict is an **inventory decision-support tool** — it never diagnoses
patients, prescribes medicines, or moves stock automatically. Every reorder
and every inter-pharmacy transfer is only a *suggestion* until a pharmacist
reviews and approves it.

---

## 1. Problem Statement

Healthcare centers and Jan Aushadhi stores often order medicines based on
manual estimation. This causes two problems at once:

1. Patients can't get essential medicines when stock runs out unexpectedly.
2. Slow-moving medicines pile up and expire before they're ever sold.

MediPredict forecasts demand, predicts stockouts, flags expiry/wastage risk,
and recommends what to reorder — all from data the pharmacy already has.

---

## 2. Features

| Module | What it does |
|---|---|
| User & Pharmacy Management | Login, roles (Admin/Pharmacist), per-pharmacy accounts |
| Medicine & Inventory Management | Full CRUD for medicines and stock batches |
| Sales & Data Processing | Record sales, auto-update inventory, compute consumption trends |
| AI Demand Forecasting | 7/14/30-day forecasts from average demand × seasonal factor × trend factor |
| Stockout Prediction | Days-until-stockout + LOW/MEDIUM/HIGH/CRITICAL risk alerts |
| Smart Reorder Recommendation | Reorder point, safety stock, recommended order quantity |
| Expiry Risk & FEFO | First-Expiry-First-Out issuing + wastage-risk alerts |
| Demand Anomaly Detection | Flags unusual demand swings for staff to investigate (never auto-declares an outbreak) |
| Medicine Priority Scoring | 0–100 composite score so staff see what matters most, first |
| Multi-Pharmacy Stock Transfer | Suggests moving surplus stock to a pharmacy facing a shortage (requires approval) |
| Dashboard & Reporting | Totals, low-stock, stockouts, expiring stock, anomalies, top priorities |

### Forecasting formulas used

```
Predicted Demand      = Historical Average × Seasonal Factor × Recent Trend Factor
Days Until Stockout   = Current Usable Stock ÷ Predicted Daily Demand
Reorder Point         = Expected Demand During Lead Time + Safety Stock
Recommended Order Qty = Forecasted Demand + Safety Stock − Current Usable Stock
```

These are plain Java/statistics — no external AI API is required. The
forecasting logic lives entirely in `service/ForecastEngine.java` and is kept
independent of the DAO/UI layers so it can be swapped for a real ML model
later (see [Future Improvements](#9-future-ai-improvements)).

---

## 3. Architecture

```
Presentation Layer   -> medipredict.ui      (ConsoleUI)
Service/Business Layer -> medipredict.service (ForecastEngine, StockoutPredictor,
                                                ReorderEngine, ExpiryAnalyzer,
                                                DemandAnomalyDetector,
                                                MedicinePriorityCalculator,
                                                TransferAdvisor, DashboardService)
DAO / Data Layer      -> medipredict.dao     (DataStore, SampleDataLoader,
                                               DatabaseConnection)
Model Layer           -> medipredict.model   (Medicine, InventoryBatch,
                                               SalesRecord, Supplier, Pharmacy,
                                               User, Alert, ReorderRecommendation)
```

The GUI, business logic and data access are kept in separate packages
(Maintainability requirement) so any layer can be replaced independently —
for example, swapping the console UI for Swing/JavaFX, or the in-memory
`DataStore` for the JDBC/MySQL layer described below.

### Why in-memory by default?

The full spec calls for MySQL + JDBC. This project ships **both**:

- A ready-to-run in-memory `DataStore` (default) — so you can compile and
  demo the whole system in under a minute with zero external setup.
- A complete `database/medipredict.sql` schema and a `DatabaseConnection.java`
  JDBC factory (using `PreparedStatement`, credentials from environment
  variables only — never hard-coded) for teams that want to plug in real
  MySQL persistence. The DAO layer (`DataStore`) is the single seam where
  that swap happens; the service layer never talks to storage directly.

---

## 4. Project Structure

```
MediPredict/
├── src/medipredict/
│   ├── model/       Medicine, InventoryBatch, SalesRecord, Supplier, Pharmacy,
│   │                 User, Role, RiskLevel, Alert, ReorderRecommendation
│   ├── service/      ForecastEngine, StockoutPredictor, ExpiryAnalyzer,
│   │                 ReorderEngine, DemandAnomalyDetector,
│   │                 MedicinePriorityCalculator, TransferAdvisor, DashboardService
│   ├── dao/          DataStore (in-memory repository), SampleDataLoader,
│   │                 DatabaseConnection (JDBC/MySQL scaffolding)
│   ├── util/         AppLogger, SecurityUtil, Validator, ValidationException
│   ├── ui/            ConsoleUI
│   └── Main.java
├── test/              TestRunner, ForecastEngineTest, StockoutPredictorTest,
│                      ReorderEngineTest, RunAllTests
├── database/
│   └── medipredict.sql
├── compile.sh / run.sh / run-tests.sh
├── .gitignore
└── README.md
```

---

## 5. Requirements

- **Java 11 or newer** (JDK, not just a JRE — `javac` is required to build).
- No external libraries needed to build and run the demo.
- MySQL 8+ and `mysql-connector-j` are **only** needed if you choose to wire
  up the optional JDBC persistence layer.

Check your version:

```bash
java -version
javac -version
```

---

## 6. How to Run

```bash
# from the MediPredict/ directory
./compile.sh    # compiles src/ into ./out
./run.sh        # compiles (if needed) and launches the console app
```

Or manually:

```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out -encoding UTF-8 @sources.txt
java -cp out medipredict.Main
```

### Sample login credentials

| Username | Password | Role | Pharmacy |
|---|---|---|---|
| `admin` | `admin123` | ADMIN | PH01 (Bhopal Central) |
| `pharmacist1` | `pharma123` | PHARMACIST | PH01 (Bhopal Central) |
| `pharmacist2` | `pharma123` | PHARMACIST | PH02 (Indore East) |

On startup, `SampleDataLoader` populates 12 demo medicines (Paracetamol, ORS,
Amoxicillin, Cetirizine, Ibuprofen, Azithromycin, Antacid, Iron Supplement,
Vitamin D3, Metformin, Insulin Glargine, Cough Syrup) across 2 pharmacies,
with multiple batches, varying expiry dates, and 90 days of randomized (but
reproducible — fixed random seed) sales history, including a built-in demand
spike on Azithromycin so the anomaly detector has something real to flag.

---

## 7. Running Tests

```bash
./run-tests.sh
```

This compiles `src/` + `test/` together and runs `medipredict.test.RunAllTests`,
a small dependency-free test runner (no JUnit needed) covering:

- **Forecasting** — average demand math, trend-factor division-by-zero safety,
  seasonal scaling, non-negative output.
- **Stockout prediction** — days-until-stockout math, zero-demand safety,
  LOW/MEDIUM/HIGH/CRITICAL boundary classification, alert generation.
- **Reorder calculation** — recommended order quantities, "no order needed"
  case, CRITICAL priority for an essential medicine at zero stock, and that
  recommended quantities never go negative.

---

## 8. MySQL Setup (optional — for real persistence)

1. Install MySQL 8+ and start the server.
2. Load the schema and demo data:
   ```bash
   mysql -u root -p < database/medipredict.sql
   ```
3. Set connection details via environment variables (never hard-code
   credentials in source):
   ```bash
   export MEDIPREDICT_DB_URL="jdbc:mysql://localhost:3306/medipredict?useSSL=false&serverTimezone=UTC"
   export MEDIPREDICT_DB_USER="root"
   export MEDIPREDICT_DB_PASSWORD="your_password"
   ```
4. Add `mysql-connector-j` to your classpath and implement JDBC-backed DAO
   classes (`MedicineDAO`, `InventoryDAO`, `SalesDAO`, `UserDAO`) that read
   from/write to the tables in `medipredict.sql` using `DatabaseConnection`
   and `PreparedStatement`, following the same method signatures as
   `DataStore`. This keeps the service layer completely unchanged.

---

## 9. Non-Functional Requirements — how they're addressed

- **Performance** — `HashMap`/`LinkedHashMap` lookups for O(1) medicine/batch
  access; forecasting only scans each medicine's own sales history.
- **Security** — SHA-256 password hashing (`SecurityUtil`), role-based
  authorization (`requireRole`), `PreparedStatement` mandated for the JDBC
  layer, no credentials in source code.
- **Usability** — clear menu-driven console UI, LOW/MEDIUM/HIGH/CRITICAL
  labels everywhere, friendly validation error messages.
- **Reliability** — `Validator` rejects negative quantities/prices and past
  expiry dates; `InventoryBatch` refuses to go below zero stock.
- **Scalability** — DAO seam (`DataStore`) lets the same service layer run
  against thousands of medicines or a MySQL backend without changes.
- **Maintainability** — strict `model` / `service` / `dao` / `ui` separation;
  no business logic in the UI layer.
- **Error Handling** — `ValidationException` for user-facing input errors,
  general `Exception` guard in the main menu loop so the app never crashes
  to a stack trace, division-by-zero protected in every forecasting formula.
- **Logging & Monitoring** — `AppLogger` (built on `java.util.logging`)
  records logins, medicine/stock changes, sales, forecasts, reorder
  approvals, transfer suggestions, and errors to `medipredict.log`.
- **Resource Efficiency** — in-memory demo avoids DB round-trips entirely;
  the JDBC scaffolding is written to close connections via try-with-resources
  when implemented.

---

## 10. Safety Principle

MediPredict is **not** a medical diagnosis or prescribing system. It does not:

- Diagnose patients or prescribe/substitute medicines automatically.
- Declare a disease outbreak automatically (anomaly alerts only ask staff to
  investigate unusual demand).
- Move inventory between pharmacies automatically — every transfer suggestion
  requires staff approval.

---

## 11. Future AI Improvements

The current Java statistical engine is intentionally isolated
(`ForecastEngine`) so it can be upgraded later to:

- Machine-learning demand forecasting / time-series models (ARIMA, Prophet, etc.)
- Real-time disease/outbreak datasets and weather-based demand prediction
- Regional demand analysis across many pharmacies
- Cloud deployment (the MySQL schema is already cloud-ready)
- A mobile companion app
- Government/public-health API integration

---

## 12. Suggested Git History

```
Initial project setup
Added database schema
Added medicine CRUD
Added sales module
Added forecasting engine
Added stockout prediction
Added expiry/FEFO
Added reorder system
Added dashboard
Added testing
Final documentation
```

A `.gitignore` is included (excludes `out/`, `*.class`, logs, and IDE files).

---

## 13. License / Academic Use

Built as a demonstrable college project / hackathon submission for the
Healthcare & MedTech track. Feel free to fork, extend, and adapt.
