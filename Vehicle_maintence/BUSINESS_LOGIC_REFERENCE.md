# 🔧 Business Logic Engines - Detailed Reference

**Version:** 2.0  
**Last Updated:** April 30, 2026

---

## TABLE OF CONTENTS

1. [Maintenance Scheduling Engine](#1-maintenance-scheduling-engine)
2. [Cost Optimization Engine](#2-cost-optimization-engine)
3. [Spare Parts Planning Engine](#3-spare-parts-planning-engine)
4. [Fleet Prioritization Engine](#4-fleet-prioritization-engine)
5. [Decision Flow Diagrams](#decision-flow-diagrams)
6. [Example Scenarios](#example-scenarios)

---

## 1. MAINTENANCE SCHEDULING ENGINE

### Purpose
Determine maintenance urgency level based on:
- Failure probability (ML model output: 0.0 to 1.0)
- Remaining Useful Life (ML model output: hours)
- Reported issues count (vehicle data)

### Decision Logic

```python
def schedule_maintenance(failure_prob, rul_hours, reported_issues):
    """
    Inputs:
        failure_prob (float): 0.0 to 1.0 (higher = more likely to fail)
        rul_hours (float): Hours of useful life remaining
        reported_issues (int): Count of reported problems
    
    Returns:
        urgency_level (str): One of 4 levels
    """
    
    # Adjust failure probability based on issue count
    issue_factor = min(reported_issues / 10, 1.0)  # Cap at 1.0
    adjusted_prob = min(failure_prob + (issue_factor * 0.2), 1.0)
    
    # Decision tree with multiple conditions
    if adjusted_prob > 0.8 or rul_hours < 5:
        return "IMMEDIATE SERVICE"
    
    elif rul_hours < 10 or adjusted_prob > 0.6:
        return "SCHEDULE WITHIN 24 HOURS"
    
    elif rul_hours < 50 or adjusted_prob > 0.4:
        return "PLAN SERVICE THIS WEEK"
    
    elif adjusted_prob > 0.2:
        return "NORMAL MONITORING"
    
    else:
        return "NORMAL MONITORING"
```

### Decision Thresholds Table

| Urgency Level | Primary Condition | Secondary Condition | RUL Window | Action Timeline |
|--------------|-------------------|---------------------|-----------|-----------------|
| **IMMEDIATE SERVICE** | Failure_Prob > 0.80 | OR | RUL < 5 hours | ASAP (0-2 hours) |
| **SCHEDULE 24H** | RUL < 10 hours | OR | Failure_Prob > 0.60 | Next 24 hours |
| **PLAN WEEKLY** | RUL < 50 hours | OR | Failure_Prob > 0.40 | This week (7 days) |
| **MONITORING** | Failure_Prob > 0.20 | - | Any | Ongoing checks |
| **DEFER** | Failure_Prob ≤ 0.20 | - | Any | No action needed |

### Issue Adjustment Factor

```
Reported_Issues  →  Factor   →  Prob_Increase
       0         →   0.00   →    +0.0%
       1         →   0.10   →    +2.0%
       2         →   0.20   →    +4.0%
       3         →   0.30   →    +6.0%
       4         →   0.40   →    +8.0%
       5         →   0.50   →   +10.0%
      10+        →   1.00   →   +20.0%
```

### Example Calculations

**Example 1: Emergency Stop**
```
Input:
  failure_prob = 0.92
  rul_hours = 3.2
  reported_issues = 2

Calculation:
  issue_factor = min(2/10, 1.0) = 0.20
  adjusted_prob = min(0.92 + (0.20 * 0.2), 1.0) = 0.96
  
  Check: adjusted_prob > 0.8? YES (0.96 > 0.80)
  
Output: "IMMEDIATE SERVICE" ✅
Action: Stop vehicle, conduct emergency service now
```

**Example 2: Planned Service**
```
Input:
  failure_prob = 0.45
  rul_hours = 35.5
  reported_issues = 1

Calculation:
  issue_factor = min(1/10, 1.0) = 0.10
  adjusted_prob = min(0.45 + (0.10 * 0.2), 1.0) = 0.47
  
  Check: adjusted_prob > 0.80? NO
         rul_hours < 10? NO
         adjusted_prob > 0.60? NO
         rul_hours < 50? YES (35.5 < 50) ✅
  
Output: "PLAN SERVICE THIS WEEK" ✅
Action: Schedule service appointment this week
```

**Example 3: Monitoring Only**
```
Input:
  failure_prob = 0.25
  rul_hours = 180.0
  reported_issues = 0

Calculation:
  issue_factor = min(0/10, 1.0) = 0.00
  adjusted_prob = min(0.25 + (0.00 * 0.2), 1.0) = 0.25
  
  Check: adjusted_prob > 0.80? NO
         rul_hours < 10? NO
         adjusted_prob > 0.60? NO
         rul_hours < 50? NO
         adjusted_prob > 0.20? YES (0.25 > 0.20) ✅
  
Output: "NORMAL MONITORING" ✅
Action: Continue normal monitoring, no service needed
```

---

## 2. COST OPTIMIZATION ENGINE

### Purpose
Compare **proactive maintenance cost** vs **reactive emergency repair cost** to determine if maintenance should happen now or be deferred.

### Cost Model Formula

```
Expected_Loss = Downtime_Cost × Failure_Probability × RUL_Factor
Emergency_Repair_Cost = Expected_Loss × Emergency_Multiplier
Proactive_Cost = Fixed_Repair_Cost
ROI = (Emergency_Cost - Proactive_Cost) / Proactive_Cost × 100%

Decision:
  If Expected_Loss > Proactive_Cost:
      Recommendation = "REPAIR NOW"
      Cost_Saving = Emergency_Cost - Proactive_Cost
  Else:
      Recommendation = "DEFER MAINTENANCE"
      Cost_Saving = Proactive_Cost (avoid cost)
```

### Default Financial Parameters

```python
DOWNTIME_COST_PER_HOUR = 500        # $ / hour vehicle is unavailable
PROACTIVE_REPAIR_COST = 1500        # $ for planned maintenance
EMERGENCY_REPAIR_COST_MULTIPLIER = 4  # Emergency repairs cost 4x more
RUL_REFERENCE = 100                 # Hours (for normalization)
```

### RUL Factor Calculation

```
RUL_Factor adjusts risk based on remaining life:
  RUL_Factor = max(1 - (RUL_Hours / RUL_REFERENCE), 0.5)
  
Interpretation:
  - RUL = 100h → Factor = 0.50 (half risk)
  - RUL = 50h  → Factor = 0.75 (75% risk)
  - RUL = 10h  → Factor = 0.90 (90% risk)
  - RUL = 5h   → Factor = 0.95 (95% risk)
  - RUL = 0h   → Factor = 1.00 (100% risk, capped at 0.5 minimum)
```

### Cost Decision Table

| Scenario | Failure_Prob | RUL_Hours | Expected_Loss | Emergency_Cost | Proactive_Cost | Decision | Savings |
|----------|-------------|-----------|---------------|----------------|----------------|----------|---------|
| High Risk | 0.90 | 5 | $2,138 | $8,550 | $1,500 | **REPAIR NOW** | $7,050 |
| Medium Risk | 0.60 | 25 | $750 | $3,000 | $1,500 | **DEFER** | $1,500 |
| Low Risk | 0.20 | 150 | $133 | $533 | $1,500 | **DEFER** | $1,500 |
| Critical | 0.95 | 2 | $2,375 | $9,500 | $1,500 | **REPAIR NOW** | $8,000 |

### Detailed Example

**Scenario: Engine Misfire Detection**

```
Vehicle Data:
- Vehicle_ID: VEH_FLT_042
- Failure_Probability: 0.75
- RUL_Hours: 15
- Downtime_Cost_Per_Hour: $650 (fleet uses this vehicle 13 hours/day)
- Custom_Repair_Cost: $1,200 (this specific repair)

Calculation Step-by-Step:

1. Calculate RUL Factor:
   RUL_Factor = max(1 - (15 / 100), 0.5)
              = max(1 - 0.15, 0.5)
              = max(0.85, 0.5)
              = 0.85

2. Calculate Expected Loss:
   Expected_Loss = $650 × 0.75 × 0.85
                 = $413.75

3. Calculate Emergency Cost:
   Emergency_Cost = $413.75 × 4
                  = $1,655.00

4. Compare:
   Expected_Loss ($413.75) < Proactive_Cost ($1,200)?
   NO → Expected loss is lower, but emergency cost is higher

5. Decision:
   Since Emergency_Cost ($1,655) > Proactive_Cost ($1,200):
   Recommendation = "PROACTIVE MAINTENANCE (REPAIR NOW)"
   
   ROI = ($1,655 - $1,200) / $1,200 × 100%
       = $455 / $1,200 × 100%
       = 37.9% return on investment

Output:
{
  "expected_loss": 413.75,
  "proactive_repair_cost": 1200.00,
  "emergency_repair_cost": 1655.00,
  "recommendation": "PROACTIVE MAINTENANCE (REPAIR NOW)",
  "cost_saving": 455.00,
  "roi": 37.9
}

Action: Schedule maintenance - saves $455 vs emergency repair
```

### Cost Scenarios Comparison

```
SCENARIO A: High-Risk Vehicle (Expensive Downtime)
- Failure Probability: 0.85
- RUL: 8 hours
- Downtime Cost/Hour: $800 (mission-critical vehicle)
- Expected Loss: 0.85 × 800 × 0.92 = $626
- Emergency Cost: $626 × 4 = $2,504
- Proactive Cost: $1,500
- Decision: REPAIR NOW (Save $1,004)

SCENARIO B: Low-Risk Vehicle (Low Downtime Cost)
- Failure Probability: 0.35
- RUL: 90 hours
- Downtime Cost/Hour: $300 (occasional-use vehicle)
- Expected Loss: 0.35 × 300 × 0.55 = $57.75
- Emergency Cost: $57.75 × 4 = $231
- Proactive Cost: $1,500
- Decision: DEFER MAINTENANCE (Save $1,500)

SCENARIO C: Medium-Risk, Expensive Repair
- Failure Probability: 0.65
- RUL: 20 hours
- Downtime Cost/Hour: $400
- Proactive Cost: $3,500 (complex repair)
- Expected Loss: 0.65 × 400 × 0.8 = $208
- Emergency Cost: $208 × 4 = $832
- Decision: DEFER MAINTENANCE ($832 < $3,500)
```

---

## 3. SPARE PARTS PLANNING ENGINE

### Purpose
Forecast spare parts demand across entire fleet based on:
- Predicted trouble codes for each vehicle
- Failure probabilities
- Historical parts demand rates

### Spare Parts Demand Matrix

```python
SPARE_PARTS_DEMAND = {
    'P0000-OK': {
        'part': 'None',
        'multiplier': 0.00,
        'cost_per_unit': 0
    },
    'P0300-Engine Misfire': {
        'part': 'Spark Plugs',
        'multiplier': 0.35,
        'cost_per_unit': 45.00,
        'supplier': 'Bosch',
        'lead_time_days': 2
    },
    'P0420-Catalyst System': {
        'part': 'Catalytic Converter',
        'multiplier': 0.15,
        'cost_per_unit': 280.00,
        'supplier': 'OEM',
        'lead_time_days': 5
    },
    'P0171-Fuel System': {
        'part': 'Fuel Injectors',
        'multiplier': 0.25,
        'cost_per_unit': 120.00,
        'supplier': 'Bosch',
        'lead_time_days': 3
    },
    'P0101-Mass Airflow': {
        'part': 'MAF Sensor',
        'multiplier': 0.20,
        'cost_per_unit': 95.00,
        'supplier': 'Delphi',
        'lead_time_days': 2
    },
    'P0505-Idle Control': {
        'part': 'IAC Valve',
        'multiplier': 0.18,
        'cost_per_unit': 85.00,
        'supplier': 'Standard',
        'lead_time_days': 3
    },
    'P0700-Transmission': {
        'part': 'Transmission Fluid',
        'multiplier': 0.30,
        'cost_per_unit': 15.00,
        'supplier': 'Castrol',
        'lead_time_days': 1
    },
    'P0011-Cam Timing': {
        'part': 'Timing Chain',
        'multiplier': 0.10,
        'cost_per_unit': 180.00,
        'supplier': 'OEM',
        'lead_time_days': 7
    },
    'P0016-Cam/Crank Sync': {
        'part': 'Timing Belt',
        'multiplier': 0.12,
        'cost_per_unit': 95.00,
        'supplier': 'Dayco',
        'lead_time_days': 4
    },
    'P0401-EGR Flow': {
        'part': 'EGR Valve',
        'multiplier': 0.22,
        'cost_per_unit': 135.00,
        'supplier': 'Standard',
        'lead_time_days': 3
    }
}
```

### Demand Forecasting Formula

```
For each vehicle in fleet:
    Demand = Fleet_Size × Part_Multiplier × Vehicle_Failure_Probability

Total_Part_Demand = SUM(Demand for all vehicles with that trouble code)

Inventory_Target = Total_Part_Demand × Safety_Stock_Factor (e.g., 1.2 for 20% buffer)
```

### Fleet Forecasting Example

**Scenario: Fleet of 500 vehicles**

```
Step 1: Predict for each vehicle
Vehicle 1: Trouble Code = P0300, Failure_Prob = 0.45 → Spark_Plugs demand = 1 × 0.35 × 0.45 = 0.1575
Vehicle 2: Trouble Code = P0420, Failure_Prob = 0.72 → Cat_Conv demand = 1 × 0.15 × 0.72 = 0.108
Vehicle 3: Trouble Code = P0300, Failure_Prob = 0.38 → Spark_Plugs demand = 1 × 0.35 × 0.38 = 0.133
... (repeat for all 500)

Step 2: Aggregate by part
Spark_Plugs = 0.1575 + 0.133 + ... = 65 units total
Catalytic_Converter = 0.108 + ... = 28 units total
Fuel_Injectors = ... = 42 units total

Step 3: Add safety stock (20% buffer)
Safety_Stock_Spark_Plugs = 65 × 1.20 = 78 units to order

Step 4: Calculate procurement
Order_Date = TODAY
Lead_Time = 2 days (Bosch)
Delivery_Date = TODAY + 2 days
Cost = 78 × $45.00 = $3,510
```

### Fleet Inventory Recommendation

**Based on 100-vehicle sample:**

```json
{
  "forecast_period": "Next 30 days",
  "fleet_size": 100,
  "parts_forecast": {
    "Spark Plugs": {
      "quantity": 12,
      "safety_stock": 14,
      "cost_per_unit": 45.00,
      "total_cost": 630.00,
      "lead_time": "2 days",
      "supplier": "Bosch",
      "priority": "MEDIUM"
    },
    "Transmission Fluid": {
      "quantity": 10,
      "safety_stock": 12,
      "cost_per_unit": 15.00,
      "total_cost": 180.00,
      "lead_time": "1 day",
      "supplier": "Castrol",
      "priority": "HIGH"
    },
    "Fuel Injectors": {
      "quantity": 7,
      "safety_stock": 8,
      "cost_per_unit": 120.00,
      "total_cost": 960.00,
      "lead_time": "3 days",
      "supplier": "Bosch",
      "priority": "MEDIUM"
    },
    "EGR Valve": {
      "quantity": 6,
      "safety_stock": 7,
      "cost_per_unit": 135.00,
      "total_cost": 945.00,
      "lead_time": "3 days",
      "supplier": "Standard",
      "priority": "LOW"
    }
  },
  "total_inventory_cost": 2715.00,
  "critical_shortages": 0,
  "recommendations": [
    "Order Transmission Fluid immediately (high usage)",
    "Pre-order Catalytic Converters (7-day lead time)",
    "Maintain 10% extra safety stock during peak season"
  ]
}
```

---

## 4. FLEET PRIORITIZATION ENGINE

### Purpose
Rank vehicles 1-100 by maintenance urgency to optimize technician scheduling and resource allocation.

### Priority Score Formula

```
Priority_Score = (Failure_Prob × 100 × WEIGHT_FAILURE) +
                 (RUL_Weight × WEIGHT_RUL) +
                 (Age_Weight × WEIGHT_AGE) +
                 (Condition_Weight × WEIGHT_CONDITION) +
                 (Issue_Weight × WEIGHT_ISSUES)

Where:
  WEIGHT_FAILURE = 0.50     (50% of score)
  WEIGHT_RUL = 0.25         (25% of score)
  WEIGHT_AGE = 0.10         (10% of score)
  WEIGHT_CONDITION = 0.08   (8% of score)
  WEIGHT_ISSUES = 0.07      (7% of score)

Component Weights:
  RUL_Weight = max(0, 100 - (RUL_Hours / 100 × 100))
               (Lower RUL = higher weight)
  
  Age_Weight = min(Vehicle_Age × 10, 100)
               (Older vehicles get higher weight)
  
  Condition_Weight = (100 - Vehicle_Health_Score)
               (Worse condition = higher weight)
  
  Issue_Weight = min(Reported_Issues × 8, 100)
               (More issues = higher weight)

Final_Score = min(Priority_Score, 100)  # Cap at 100
```

### Priority Levels and Thresholds

```
Score Range  |  Level         |  Action              |  Timeline    |  Resource Allocation
  75-100     |  CRITICAL      |  Immediate service   |  Hours       |  Drop everything
  55-74      |  HIGH          |  Schedule this week  |  Days        |  Priority queue
  35-54      |  MEDIUM        |  Plan next month     |  Weeks       |  Standard queue
  0-34       |  LOW           |  Routine maintenance |  Months      |  Batch with others
```

### Example: Fleet Prioritization

**Fleet of 20 vehicles - Full ranking:**

```
CRITICAL PRIORITY (75-100)
┌─────────┬──────────────┬──────────┬────────┬──────────┬───────┬──────────────────┐
│ Rank    │ Vehicle_ID   │ Fail_Prob│ RUL_Hr │ Score    │ Age   │ Issue_Count      │
├─────────┼──────────────┼──────────┼────────┼──────────┼───────┼──────────────────┤
│ 1       │ VEH_FLT_001  │ 0.92     │ 3.2    │ 87.5     │ 9     │ 5                │
│ 2       │ VEH_FLT_047  │ 0.88     │ 6.1    │ 85.2     │ 8     │ 4                │
│ 3       │ VEH_FLT_032  │ 0.85     │ 8.7    │ 83.4     │ 7     │ 3                │
└─────────┴──────────────┴──────────┴────────┴──────────┴───────┴──────────────────┘

HIGH PRIORITY (55-74)
│ 4       │ VEH_FLT_019  │ 0.72     │ 25.3   │ 68.9     │ 6     │ 2                │
│ 5       │ VEH_FLT_088  │ 0.68     │ 35.1   │ 64.7     │ 5     │ 1                │
│ 6       │ VEH_FLT_056  │ 0.65     │ 42.0   │ 61.2     │ 4     │ 2                │
│ 7       │ VEH_FLT_103  │ 0.60     │ 48.5   │ 57.8     │ 3     │ 0                │
└─────────┴──────────────┴──────────┴────────┴──────────┴───────┴──────────────────┘

MEDIUM PRIORITY (35-54)
│ 8       │ VEH_FLT_044  │ 0.52     │ 65.2   │ 48.5     │ 5     │ 1                │
│ 9       │ VEH_FLT_091  │ 0.45     │ 78.9   │ 42.3     │ 3     │ 0                │
│ 10      │ VEH_FLT_022  │ 0.38     │ 92.1   │ 35.7     │ 2     │ 1                │
└─────────┴──────────────┴──────────┴────────┴──────────┴───────┴──────────────────┘

LOW PRIORITY (0-34)
│ 11-20   │ Various      │ <0.35    │ >120   │ <35      │ <4    │ 0                │
└─────────┴──────────────┴──────────┴────────┴──────────┴───────┴──────────────────┘
```

### Detailed Score Calculation Example

**Vehicle: VEH_FLT_001 (Top Priority)**

```
Input Data:
- Failure_Probability: 0.92
- RUL_Hours: 3.2
- Vehicle_Age: 9 years
- Vehicle_Health_Score: 38 (poor condition)
- Reported_Issues: 5

Calculation:

1. Failure Component:
   Failure_Component = 0.92 × 100 × 0.50 = 46.0

2. RUL Component:
   RUL_Weight = max(0, 100 - (3.2 / 100 × 100))
              = max(0, 100 - 3.2)
              = 96.8
   RUL_Component = 96.8 × 0.25 = 24.2

3. Age Component:
   Age_Weight = min(9 × 10, 100) = 90
   Age_Component = 90 × 0.10 = 9.0

4. Condition Component:
   Condition_Weight = 100 - 38 = 62
   Condition_Component = 62 × 0.08 = 4.96

5. Issues Component:
   Issue_Weight = min(5 × 8, 100) = 40
   Issue_Component = 40 × 0.07 = 2.8

Total Score:
Priority_Score = 46.0 + 24.2 + 9.0 + 4.96 + 2.8
               = 86.96
               ≈ 87.0 (rounded)

Result: CRITICAL PRIORITY ✅
Action: Schedule immediate service, allocate senior technician
```

### Technician Scheduling Optimization

```
Available Technicians: 5
Available Hours This Week: 40 hours

Priority Distribution:
- CRITICAL (3 vehicles): 6 hours each = 18 hours
- HIGH (4 vehicles): 2 hours each = 8 hours
- MEDIUM (6 vehicles): 1 hour each = 6 hours
- LOW (7 vehicles): Defer to next week

Allocation:
- Technician 1: VEH_001 (6h) + VEH_047 (6h) = 12h
- Technician 2: VEH_032 (6h) + VEH_019 (2h) = 8h
- Technician 3: VEH_088 (2h) + VEH_056 (2h) + VEH_103 (2h) + VEH_044 (2h) = 8h
- Technician 4: 6 MEDIUM priority vehicles = 6h
- Technician 5: Standby for emergencies
```

---

## DECISION FLOW DIAGRAMS

### Complete Prediction Pipeline

```
                    Vehicle Input Data
                            │
                            ▼
        ┌─────────────────────────────────────┐
        │    Data Preprocessing & Features    │
        │  • Missing values                   │
        │  • Categorical encoding             │
        │  • Feature engineering              │
        │  • Scaling & normalization          │
        └─────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
    ┌─────────┐      ┌──────────┐       ┌─────────┐
    │ Failure │      │ Trouble  │       │   RUL   │
    │Predictor│      │ Classifier       │Predictor│
    └────┬────┘      └──────┬───┘       └────┬────┘
         │ failure_prob     │ trouble_code    │ rul_hours
         └────────┬─────────┴────────┬────────┘
                  │                  │
                  ▼                  ▼
         ┌─────────────────────────────────┐
         │   Business Logic Engines (4)    │
         ├─────────────────────────────────┤
         │ 1. Maintenance Scheduling       │
         │    (urgency level)              │
         │                                 │
         │ 2. Cost Optimization            │
         │    (repair vs defer)            │
         │                                 │
         │ 3. Spare Parts Planning         │
         │    (parts needed)               │
         │                                 │
         │ 4. Fleet Prioritization         │
         │    (priority score)             │
         └────────┬────────────────────────┘
                  │
                  ▼
        ┌──────────────────────┐
        │ Unified Prediction   │
        │ Result Object        │
        ├──────────────────────┤
        │ • vehicle_id         │
        │ • failure_prob       │
        │ • rul_hours          │
        │ • trouble_code       │
        │ • urgency            │
        │ • cost_decision      │
        │ • spare_parts        │
        │ • fleet_priority     │
        │ • confidence_score   │
        │ • timestamp          │
        └──────────────────────┘
                  │
        ┌─────────┴──────────┐
        │                    │
        ▼                    ▼
    API Response        Database Log
    (JSON)              (History)
```

---

## EXAMPLE SCENARIOS

### Scenario 1: Fleet Taxi (High-Utilization Vehicle)

```
Vehicle Profile:
- Vehicle_ID: VEH_TAXI_042
- Vehicle_Type: Taxi (24/7 operation)
- Mileage: 180,000 km
- Age: 6 years
- Service History: Good (regular maintenance)
- Reported Issues: 2 (minor)

ML Predictions:
- Failure_Probability: 0.72
- RUL_Hours: 22
- Trouble_Code: P0401 (EGR Flow)

Business Logic Outputs:

1. MAINTENANCE SCHEDULING:
   Adjustment: reported_issues=2 → issue_factor=0.20 → +4%
   Adjusted_Prob = 0.72 + 0.04 = 0.76
   Check: adjusted_prob > 0.6? YES ✓
   Output: "SCHEDULE WITHIN 24 HOURS"

2. COST OPTIMIZATION:
   Downtime_Cost = $800/hour (taxi loses revenue 24/7)
   RUL_Factor = max(1 - (22/100), 0.5) = 0.78
   Expected_Loss = 800 × 0.72 × 0.78 = $450.24
   Emergency_Cost = 450.24 × 4 = $1,801
   Proactive_Cost = $1,200
   Decision: "REPAIR NOW" (Save $601, ROI = 50%)

3. SPARE PARTS:
   EGR_Valve demand = 1 × 0.22 × 0.72 = 0.16 units
   Estimated parts cost = $135

4. FLEET PRIORITY:
   Age_Weight = min(6 × 10, 100) = 60
   RUL_Weight = max(0, 100-22) = 78
   Condition normal, 2 issues
   Priority_Score = (0.72×100×0.50) + (78×0.25) + (60×0.10) + ...
                  = 36 + 19.5 + 6 + ...
                  = ~68 → HIGH PRIORITY

Final Decision:
✅ URGENT SERVICE NEEDED
✅ Repair now for better ROI
✅ Schedule within 24 hours
✅ High fleet priority
💰 Cost savings: $601
```

### Scenario 2: Corporate Fleet Sedan (Low-Utilization)

```
Vehicle Profile:
- Vehicle_ID: VEH_CORP_103
- Vehicle_Type: Sedan (executive use)
- Mileage: 45,000 km
- Age: 3 years
- Service History: Excellent
- Reported Issues: 0

ML Predictions:
- Failure_Probability: 0.28
- RUL_Hours: 215
- Trouble_Code: P0000 (OK)

Business Logic Outputs:

1. MAINTENANCE SCHEDULING:
   No issues → adjusted_prob = 0.28
   Check: adjusted_prob > 0.20? YES ✓
   Output: "NORMAL MONITORING"

2. COST OPTIMIZATION:
   Downtime_Cost = $200/hour (occasional use)
   RUL_Factor = max(1 - (215/100), 0.5) = 0.5
   Expected_Loss = 200 × 0.28 × 0.5 = $28
   Emergency_Cost = 28 × 4 = $112
   Proactive_Cost = $1,500
   Decision: "DEFER MAINTENANCE" (Save $1,500)

3. SPARE PARTS:
   No parts needed (P0000 = OK)

4. FLEET PRIORITY:
   Low failure_prob, high RUL, young age, no issues
   Priority_Score = ~12 → LOW PRIORITY

Final Decision:
✅ NO URGENT SERVICE
✅ Defer maintenance (save $1,500)
✅ Continue normal monitoring
✅ Low fleet priority
💰 Cost savings: $1,500
```

---

**End of Business Logic Reference**  
**For questions, contact ML Platform Team**
