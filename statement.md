# MediPredict — Project Statement

**AI-Powered Medicine Demand & Inventory Management System**

---

## 1. Problem Statement

Small and mid-sized pharmacies — including government Jan Aushadhi Kendras and independent retail chemists — manage their medicine inventory almost entirely by intuition and manual stock registers. This creates two opposite failures that occur at the same time, on the same shelf:

**Stockouts of essential medicines.** When a pharmacy runs out of a critical drug such as an antibiotic, an antidiabetic, or ORS, the patient either leaves without treatment or has to travel to another outlet. For chronic-condition patients and for low-income patients in particular, this is a direct interruption of care, not a minor inconvenience. Reordering is typically triggered only after a shelf is visibly empty, by which point the supplier lead time (often 5–8 days) guarantees a gap in availability.

**Expiry wastage of over-ordered medicines.** To compensate for the fear of stockouts, staff over-order slow-moving items. Because stock is usually picked from whichever box is in front, older batches sit untouched until they expire and must be written off. This is pure financial loss for the pharmacy and pure waste of manufactured medicine.

Underlying both problems is the same root cause: **there is no demand signal.** Sales data is recorded (if at all) for billing, never for prediction. Nobody computes how fast a given medicine is actually moving at a given branch, how long current stock will last, whether demand has recently shifted, or which batch should be sold first. Seasonal patterns (monsoon gastro cases, winter respiratory demand, allergy season) are known anecdotally but never quantified. In multi-branch operations, one branch may be sitting on excess stock of the exact medicine another branch has run out of, and neither knows.

MediPredict addresses this by turning routine sales records into a forecasting signal, and converting that forecast into concrete, reviewable operational recommendations: what will run out and when, what to reorder and how much, what to sell first, what to investigate, and what to transfer between branches.

---

## 2. Scope of the Project

### 2.1 In Scope

**Core data management**
- User authentication with role-based access (Admin, Pharmacist) and branch-level data scoping
- Pharmacy, supplier, and medicine master records with full CRUD and search
- Batch-level inventory tracking — each batch carries its own quantity, expiry date, purchase price, supplier, and owning pharmacy

**Sales processing**
- Sale recording with automatic inventory deduction
- FEFO (First Expiry, First Out) allocation, so every sale draws down the earliest-expiring usable batch first
- Validation against insufficient or expired stock

**Predictive and analytical modules**
- Demand forecasting using historical average × seasonal factor × recent trend factor
- Stockout prediction with days-until-stockout and Low/Medium/High/Critical risk classification
- Smart reorder recommendations based on a reorder point of lead-time demand plus safety stock
- Expiry and wastage-risk analysis comparing batch quantity against expected demand before expiry
- Demand anomaly detection flagging statistically unusual week-over-week swings
- Composite medicine priority scoring (0–100) combining stockout urgency, expiry proximity, essential-medicine status, and demand volume
- Multi-pharmacy transfer suggestions between surplus and shortage branches

**Presentation**
- A dashboard summarising inventory value, low-stock counts, stockout risk, expiring batches, anomalies, and top-priority medicines
- On-demand operational reports for each analytical module

### 2.2 Out of Scope

- Prescription validation, drug-interaction checking, dosage guidance, or any clinical decision support
- Patient records, diagnoses, or any personally identifiable patient health information
- Billing, GST/tax computation, accounting, or payment processing
- Automated purchase-order placement or direct supplier API integration — the system recommends; a human approves and orders
- Epidemiological or outbreak surveillance. The anomaly module flags demand patterns for staff investigation only and makes no public-health claim
- Regulatory compliance reporting (Schedule H/X narcotics registers, drug licensing submissions)

### 2.3 Technical Scope and Assumptions

The current implementation is a single-file, dependency-free Java console application using in-memory collections behind DAO-style manager classes. This was a deliberate choice so the system runs with `javac` and `java` alone, with no database server or GUI toolkit setup.

The architecture is layered so that scope can be extended without rewriting logic: the service layer (forecasting, stockout, reorder, expiry, anomaly, priority) depends only on the data-store read/write methods, so the in-memory store can be replaced with MySQL/JDBC DAOs, and the console UI can be replaced with a Swing or web front end, independently of each other.

Forecast quality is bounded by the sales history available. The model is a transparent statistical one, not a black-box predictor — every number it produces can be traced back to a formula and its inputs. It assumes sales are recorded reasonably consistently; sparse or missing history produces correspondingly weaker forecasts.

---

## 3. Target Users

| User | Role in system | What they need from it |
|---|---|---|
| **Pharmacist / counter staff** | `PHARMACIST`, scoped to one branch | Record sales quickly; know which batch to hand over (FEFO); see what is about to run out at their own branch |
| **Pharmacy owner / manager** | `ADMIN`, all branches | Reorder quantities with a justification; visibility into capital tied up in stock and money being lost to expiry; approve inter-branch transfers |
| **Inventory / procurement officer** | `ADMIN` | Consolidated reorder lists by priority, aligned to supplier lead times |
| **Multi-branch chain supervisor** | `ADMIN` | Comparison across branches; identification of surplus-vs-shortage imbalances that can be resolved by transfer rather than purchase |
| **Public health scheme operators (e.g. Jan Aushadhi Kendra staff)** | Either role | Continuous availability of essential generic medicines, where a stockout has a direct patient-access cost |

**Assumed user profile:** comfortable with basic computer use, not technically trained. All outputs are therefore plain, labelled, and explanatory rather than statistical. Every recommendation the system produces is advisory and requires human approval before acting.

---

## 4. High-Level Features

### F1 — User & Access Management
Role-based login with Admin and Pharmacist roles. Pharmacists are automatically scoped to their own branch's data; Admins see all branches and must specify a branch when recording a transaction. Login attempts are limited and logged.

### F2 — Medicine & Master Data Management
Create, view, search, and delete medicine records. Each medicine carries a category, an essential/non-essential flag, unit price, minimum stock level, reorder level, and a configurable seasonal demand factor. Search matches on ID, name, or category.

### F3 — Batch-Level Inventory Tracking
Stock is tracked per batch rather than as a single quantity, so expiry dates, purchase prices, and source suppliers are preserved. Usable stock excludes expired batches automatically. Expiry dates in the past are rejected at entry.

### F4 — Sales Processing with FEFO Enforcement
Recording a sale deducts stock from the earliest-expiring usable batch first, then the next, and so on. If usable stock is insufficient, the sale is rejected with the exact shortfall rather than silently going negative. This makes wastage reduction a property of normal operation rather than a separate task.

### F5 — Demand Forecasting
Predicts daily and multi-day demand per medicine per branch:

> **Predicted Demand = Historical Average × Seasonal Factor × Recent Trend Factor**

The historical average is computed over the last 30 days; the trend factor compares the last 7 days against the preceding 8–30 days and is clamped to a sane range to avoid overreacting to noise; the seasonal factor is a per-medicine configurable multiplier. Forecasts are presented over 7-, 14-, and 30-day horizons.

### F6 — Stockout Prediction & Risk Alerts
Computes **Days Until Stockout = Current Usable Stock ÷ Predicted Daily Demand**, and classifies the result as Critical (≤3 days), High (≤7), Medium (≤14), or Low. Any medicine within 14 days of stockout raises an alert. The report is sorted most-urgent-first.

### F7 — Smart Reorder Recommendations
For each medicine, computes a reorder point of expected demand during supplier lead time plus safety stock, and a recommended order quantity of forecast 30-day demand plus safety stock minus current usable stock. Each line is tagged Critical / High / Medium / Low so procurement can work top-down. Recommendations are explicitly advisory and require approval.

### F8 — Expiry & Wastage Risk Analysis
For every batch nearing expiry, compares the quantity on hand against the demand expected before that expiry date, and reports the projected excess — the units likely to be wasted. Batches are classified as *Expiring Soon* or *High Wastage Risk* and sorted by time remaining, so staff can prioritise, promote, or transfer that stock while it still has value.

### F9 — Demand Anomaly Detection
Compares the last 7 days of demand against the average of the two preceding weeks and flags any medicine whose demand has moved beyond a configurable threshold, in either direction. Results are ranked by magnitude of change. The output is framed strictly as a prompt for staff investigation — it identifies that demand moved, not why.

### F10 — Medicine Priority Scoring
Combines four weighted signals into a single 0–100 score: stockout urgency (up to 40), expiry proximity (up to 20), essential-medicine status (up to 20), and demand volume (up to 20). Scores map to Critical / High / Medium / Low bands, producing one ranked list that tells staff where to direct attention first.

### F11 — Multi-Pharmacy Transfer Suggestions
Identifies branches holding stock beyond their forecast 30-day need plus minimum level, matches them against branches below reorder level for the same medicine, and proposes a transfer quantity. This resolves imbalances internally instead of purchasing new stock. All transfers require staff approval.

### F12 — Dashboard & Reporting
A single summary view showing total medicines tracked, total usable inventory value, low-stock count, stockout-risk count, batches expiring within 30 days, anomaly count, and the top five priority medicines — with each analytical module available as a drill-down report.

### F13 — Validation, Error Handling & Logging
Input is validated at every boundary (negative quantities, past expiry dates, duplicate IDs, unknown medicines, non-numeric input). Domain failures raise a dedicated `ValidationException` and are reported to the user in plain language rather than crashing. All significant operations — logins, sales, stock changes, master-data edits — are written to an application log.

---

## 5. Success Criteria

The project is considered successful if it:

1. Produces a defensible numeric forecast for every medicine with sufficient sales history, traceable to its inputs
2. Flags every medicine within 14 days of stockout, before the shelf empties
3. Surfaces every batch at wastage risk while corrective action is still possible
4. Enforces FEFO on 100% of sales without requiring staff to think about it
5. Presents all of the above in language a non-technical pharmacist can act on immediately
6. Keeps a human in the loop for every ordering and transfer decision
