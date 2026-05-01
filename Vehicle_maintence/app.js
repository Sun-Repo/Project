const state = {
  fleet: [],
  strictMode: false,
  filter: "all",
};

const troubleCodes = [
  { code: "P0000-OK", part: "None" },
  { code: "P0300-Engine Misfire", part: "Spark plugs" },
  { code: "P0420-Catalyst System", part: "Catalytic converter" },
  { code: "P0171-Fuel System", part: "Fuel injectors" },
  { code: "P0101-Mass Airflow", part: "MAF sensor" },
  { code: "P0700-Transmission", part: "Transmission fluid" },
  { code: "P0401-EGR Flow", part: "EGR valve" },
];

const examples = [
  ["VEH-1001", "Delivery Van", 84200, 6, 210, 4, "Worn", "Good", "Weak", 1],
  ["VEH-1002", "Service Truck", 132500, 9, 310, 6, "Worn", "Worn", "Weak", 2],
  ["VEH-1003", "Passenger Shuttle", 46500, 3, 80, 1, "Good", "Good", "Good", 0],
  ["VEH-1004", "Box Truck", 98800, 7, 175, 3, "Good", "Worn", "Good", 1],
  ["VEH-1005", "Delivery Van", 25400, 2, 42, 0, "New", "New", "New", 0],
  ["VEH-1006", "Service Truck", 116800, 8, 260, 5, "Good", "Worn", "Weak", 1],
  ["VEH-1007", "Box Truck", 70500, 4, 122, 2, "Good", "Good", "Weak", 0],
  ["VEH-1008", "Passenger Shuttle", 151200, 10, 365, 7, "Worn", "Worn", "Weak", 3],
];

const form = document.querySelector("#vehicleForm");
const queueList = document.querySelector("#queueList");
const fleetRows = document.querySelector("#fleetRows");
const priorityFilter = document.querySelector("#priorityFilter");

function clamp(value, min, max) {
  return Math.min(Math.max(value, min), max);
}

function conditionScore(value) {
  if (value === "New") return 0;
  if (value === "Good") return 1;
  return 2;
}

function batteryScore(value) {
  if (value === "New") return 0;
  if (value === "Good") return 1;
  return 2;
}

function money(value) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 0,
  }).format(value);
}

function percent(value) {
  return `${Math.round(value * 100)}%`;
}

function calculatePrediction(vehicle) {
  const tire = conditionScore(vehicle.tireCondition);
  const brake = conditionScore(vehicle.brakeCondition);
  const battery = batteryScore(vehicle.batteryStatus);

  const healthScore = clamp(
    100 - tire * 13 - brake * 15 - battery * 11 - vehicle.reportedIssues * 3 - vehicle.accidents * 5,
    0,
    100
  );

  const servicePressure = clamp(vehicle.daysSinceService / 365, 0, 1.4);
  const mileagePressure = clamp(vehicle.mileage / 160000, 0, 1.4);
  const agePressure = clamp(vehicle.age / 12, 0, 1.3);
  const issuePressure = clamp(vehicle.reportedIssues / 8, 0, 1.4);
  const accidentPressure = clamp(vehicle.accidents / 4, 0, 1);
  const conditionPressure = clamp((100 - healthScore) / 100, 0, 1);
  const strictBoost = state.strictMode ? 0.08 : 0;

  const failureProbability = clamp(
    mileagePressure * 0.24 +
      agePressure * 0.17 +
      servicePressure * 0.18 +
      issuePressure * 0.18 +
      accidentPressure * 0.09 +
      conditionPressure * 0.22 +
      strictBoost,
    0.02,
    0.98
  );

  const rulHours = Math.round(
    clamp(210 - failureProbability * 170 - servicePressure * 42 - issuePressure * 28, 4, 220)
  );

  let priority = "Low";
  if (failureProbability >= 0.76 || rulHours < 18) priority = "Critical";
  else if (failureProbability >= 0.56 || rulHours < 45) priority = "High";
  else if (failureProbability >= 0.34 || rulHours < 95) priority = "Medium";

  const urgency =
    priority === "Critical"
      ? "Immediate service"
      : priority === "High"
        ? "Schedule within 7 days"
        : priority === "Medium"
          ? "Plan next service window"
          : "Normal monitoring";

  const troubleIndex =
    priority === "Low"
      ? 0
      : clamp(Math.ceil((failureProbability * 10 + vehicle.reportedIssues + tire + brake) % troubleCodes.length), 1, troubleCodes.length - 1);
  const trouble = troubleCodes[troubleIndex];

  const downtimeRisk = Math.round(500 * failureProbability * clamp((120 - rulHours) / 80, 0.3, 1.8));
  const proactiveCost = priority === "Critical" ? 2200 : priority === "High" ? 1600 : 950;
  const exposure = Math.round(downtimeRisk + proactiveCost * failureProbability);
  const decision = exposure > proactiveCost * 0.75 ? "Repair now" : "Monitor";

  return {
    ...vehicle,
    failureProbability,
    rulHours,
    priority,
    urgency,
    healthScore,
    troubleCode: trouble.code,
    part: trouble.part,
    exposure,
    decision,
    createdAt: new Date().toLocaleString(),
  };
}

function vehicleFromForm() {
  const data = new FormData(form);
  return {
    vehicleId: String(data.get("vehicleId")).trim(),
    vehicleType: String(data.get("vehicleType")),
    mileage: Number(data.get("mileage")),
    age: Number(data.get("age")),
    daysSinceService: Number(data.get("daysSinceService")),
    reportedIssues: Number(data.get("reportedIssues")),
    tireCondition: String(data.get("tireCondition")),
    brakeCondition: String(data.get("brakeCondition")),
    batteryStatus: String(data.get("batteryStatus")),
    accidents: Number(data.get("accidents")),
  };
}

function sortFleet(fleet) {
  const order = { Critical: 0, High: 1, Medium: 2, Low: 3 };
  return [...fleet].sort((a, b) => order[a.priority] - order[b.priority] || b.failureProbability - a.failureProbability);
}

function renderMetrics() {
  const total = state.fleet.length || 1;
  const critical = state.fleet.filter((item) => item.priority === "Critical").length;
  const high = state.fleet.filter((item) => item.priority === "High").length;
  const avgRisk = state.fleet.reduce((sum, item) => sum + item.failureProbability, 0) / total;
  const exposure = state.fleet.reduce((sum, item) => sum + item.exposure, 0);
  const review = state.fleet.filter((item) => item.priority !== "Low").length;

  document.querySelector("#criticalCount").textContent = critical;
  document.querySelector("#highCount").textContent = high;
  document.querySelector("#avgRisk").textContent = percent(avgRisk);
  document.querySelector("#visualRisk").textContent = percent(avgRisk);
  document.querySelector("#costExposure").textContent = money(exposure);
  document.querySelector("#vehicleCount").textContent = `${state.fleet.length} vehicle${state.fleet.length === 1 ? "" : "s"}`;
  document.querySelector("#fleetHeadline").textContent = `${review} vehicle${review === 1 ? "" : "s"} need review`;
  document.querySelector("#fleetSubhead").textContent =
    state.fleet.length === 0
      ? "Generate a sample fleet or add a vehicle to begin."
      : `${critical} critical and ${high} high priority vehicles are at the top of the work queue.`;
}

function renderQueue() {
  const filtered = sortFleet(state.fleet).filter((item) => state.filter === "all" || item.priority === state.filter);

  if (filtered.length === 0) {
    queueList.innerHTML = `<div class="empty-state">No vehicles match this queue. Add a prediction or change the priority filter.</div>`;
    return;
  }

  queueList.innerHTML = filtered
    .slice(0, 6)
    .map(
      (item) => `
        <article class="vehicle-card">
          <div class="vehicle-card-header">
            <div>
              <h4>${item.vehicleId}</h4>
              <p>${item.vehicleType} - ${item.urgency}</p>
            </div>
            <span class="badge ${item.priority}">${item.priority}</span>
          </div>
          <div class="vehicle-stats">
            <div><span>Failure risk</span><strong>${percent(item.failureProbability)}</strong></div>
            <div><span>RUL</span><strong>${item.rulHours}h</strong></div>
            <div><span>Exposure</span><strong>${money(item.exposure)}</strong></div>
          </div>
          <p>${item.troubleCode}; recommended part: ${item.part}. Decision: ${item.decision}.</p>
        </article>
      `
    )
    .join("");
}

function renderTable() {
  if (state.fleet.length === 0) {
    fleetRows.innerHTML = `<tr><td colspan="7">No predictions yet.</td></tr>`;
    return;
  }

  fleetRows.innerHTML = sortFleet(state.fleet)
    .map(
      (item) => `
        <tr>
          <td>${item.vehicleId}</td>
          <td>${item.vehicleType}</td>
          <td><span class="badge ${item.priority}">${item.priority}</span></td>
          <td>${percent(item.failureProbability)}</td>
          <td>${item.rulHours}h</td>
          <td>${item.troubleCode}</td>
          <td>${item.decision}</td>
        </tr>
      `
    )
    .join("");
}

function render() {
  renderMetrics();
  renderQueue();
  renderTable();
}

function seedFleet() {
  state.fleet = examples.map((item) =>
    calculatePrediction({
      vehicleId: item[0],
      vehicleType: item[1],
      mileage: item[2],
      age: item[3],
      daysSinceService: item[4],
      reportedIssues: item[5],
      tireCondition: item[6],
      brakeCondition: item[7],
      batteryStatus: item[8],
      accidents: item[9],
    })
  );
  render();
}

function exportCsv() {
  if (state.fleet.length === 0) return;

  const headers = ["vehicleId", "vehicleType", "priority", "failureProbability", "rulHours", "troubleCode", "part", "decision", "exposure"];
  const rows = state.fleet.map((item) => headers.map((header) => JSON.stringify(item[header] ?? "")).join(","));
  const blob = new Blob([[headers.join(","), ...rows].join("\n")], { type: "text/csv" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = "fleet-maintenance-predictions.csv";
  link.click();
  URL.revokeObjectURL(url);
}

form.addEventListener("submit", (event) => {
  event.preventDefault();
  const vehicle = vehicleFromForm();
  const prediction = calculatePrediction(vehicle);
  state.fleet = [prediction, ...state.fleet.filter((item) => item.vehicleId !== prediction.vehicleId)];
  render();
});

document.querySelector("#loadExample").addEventListener("click", () => {
  const item = examples[Math.floor(Math.random() * examples.length)];
  const names = ["vehicleId", "vehicleType", "mileage", "age", "daysSinceService", "reportedIssues", "tireCondition", "brakeCondition", "batteryStatus", "accidents"];
  names.forEach((name, index) => {
    form.elements[name].value = item[index];
  });
});

document.querySelector("#seedFleet").addEventListener("click", seedFleet);
document.querySelector("#exportCsv").addEventListener("click", exportCsv);
document.querySelector("#clearFleet").addEventListener("click", () => {
  state.fleet = [];
  render();
});

priorityFilter.addEventListener("change", (event) => {
  state.filter = event.target.value;
  renderQueue();
});

document.querySelectorAll(".nav-item").forEach((button) => {
  button.addEventListener("click", () => {
    document.querySelectorAll(".nav-item").forEach((item) => item.classList.remove("active"));
    button.classList.add("active");
    document.querySelector(`#${button.dataset.view}`).scrollIntoView({ behavior: "smooth", block: "start" });
  });
});

document.querySelector("#balancedMode").addEventListener("click", () => {
  state.strictMode = false;
  document.querySelector("#balancedMode").classList.add("active");
  document.querySelector("#strictMode").classList.remove("active");
  state.fleet = state.fleet.map(calculatePrediction);
  render();
});

document.querySelector("#strictMode").addEventListener("click", () => {
  state.strictMode = true;
  document.querySelector("#strictMode").classList.add("active");
  document.querySelector("#balancedMode").classList.remove("active");
  state.fleet = state.fleet.map(calculatePrediction);
  render();
});

seedFleet();
