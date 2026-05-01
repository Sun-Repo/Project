# FleetCare Vehicle Maintenance Prototype

A runnable browser prototype for predicting fleet maintenance priority, cost exposure, remaining useful life, and recommended service action.

The prototype is intentionally lightweight: open it locally, tweak the scoring logic, export CSVs, and use the existing Python reference system as the path toward a trained production model.

## Run It

Open `index.html` in your browser.

No package install is required for the prototype. It runs with plain HTML, CSS, and JavaScript.

## What You Can Do

- Review a generated sample fleet.
- Add or update a vehicle prediction.
- Toggle between balanced and strict scoring modes.
- Filter the priority queue by risk level.
- Export predictions as `fleet-maintenance-predictions.csv`.
- Tune the business logic in `app.js`.

## Prototype Files

- `index.html` - App structure and form controls.
- `styles.css` - Responsive dashboard styling.
- `app.js` - Sample fleet data, prediction scoring, rendering, filters, and CSV export.
- `vehicle_maintenance_system.py` - Original Python ML/business-logic reference.
- `SYSTEM_DOCUMENTATION.md` - Technical architecture notes.
- `BUSINESS_LOGIC_REFERENCE.md` - Detailed decision logic reference.
- `QUALITY_ASSURANCE_REPORT.md` - Existing QA summary.

## Tweak Points

The main scoring model lives in `calculatePrediction()` inside `app.js`.

Useful constants to adjust:

- Failure probability weights: mileage, age, service delay, issue count, accident history, and condition pressure.
- Priority thresholds: Critical, High, Medium, Low.
- Cost exposure assumptions: downtime cost and proactive repair cost.
- Trouble code mapping: `troubleCodes`.
- Sample fleet records: `examples`.

## Launch Path

1. Use this browser prototype to validate workflow, copy, thresholds, and export needs.
2. Connect the Python model pipeline to real fleet data.
3. Replace the JavaScript scoring function with API calls to the trained model.
4. Add persistence, authentication, and deployment packaging when the workflow is approved.

## Notes

The browser prototype uses deterministic rule-based scoring so it can run anywhere without dependencies. It is designed for product validation and demos, not as a final trained model.
