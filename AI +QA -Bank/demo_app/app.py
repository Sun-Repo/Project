from __future__ import annotations

from flask import Flask, jsonify, redirect, render_template_string, request, url_for

from demo_app.model_service import service

app = Flask(__name__)


PAGE_TEMPLATE = """
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>AI Adversarial Security Testing Demo</title>
  <style>
    :root {
      --bg: #f3efe6;
      --panel: #fffaf2;
      --ink: #1d2a38;
      --accent: #ba3f1d;
      --accent-soft: #f5c7b8;
      --ok: #1d6b46;
      --warn: #8a5a00;
      --danger: #8f1d1d;
      --line: #d8cfc2;
      --shadow: 0 14px 40px rgba(29, 42, 56, 0.08);
    }

    * { box-sizing: border-box; }
    body {
      margin: 0;
      font-family: "Segoe UI", Tahoma, sans-serif;
      background:
        radial-gradient(circle at top left, #fff5dd, transparent 30%),
        linear-gradient(135deg, #f2ebde, #efe7db 45%, #e6ddcf);
      color: var(--ink);
    }
    .shell {
      max-width: 1180px;
      margin: 0 auto;
      padding: 32px 20px 48px;
    }
    .hero {
      display: grid;
      grid-template-columns: 1.3fr 0.7fr;
      gap: 20px;
      margin-bottom: 24px;
    }
    .panel {
      background: var(--panel);
      border: 1px solid var(--line);
      border-radius: 18px;
      padding: 22px;
      box-shadow: var(--shadow);
    }
    h1, h2, h3, p { margin-top: 0; }
    .kpis {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
    }
    .kpi {
      padding: 16px;
      background: #fff;
      border-radius: 14px;
      border: 1px solid var(--line);
    }
    .layout {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
    }
    label {
      display: block;
      font-weight: 600;
      margin-bottom: 6px;
    }
    input, select, textarea, button {
      width: 100%;
      border-radius: 12px;
      border: 1px solid #c8bfb0;
      padding: 12px;
      font: inherit;
    }
    textarea { min-height: 110px; resize: vertical; }
    button {
      background: var(--accent);
      color: white;
      border: none;
      font-weight: 700;
      cursor: pointer;
      margin-top: 12px;
    }
    .subtle {
      font-size: 0.92rem;
      color: #4c5a68;
    }
    .badge {
      display: inline-block;
      padding: 6px 10px;
      border-radius: 999px;
      font-size: 0.8rem;
      font-weight: 700;
      background: var(--accent-soft);
      color: var(--accent);
    }
    .alert {
      border-left: 5px solid var(--danger);
      background: #fff2f2;
      padding: 12px;
      margin-bottom: 10px;
      border-radius: 10px;
    }
    .mono {
      font-family: Consolas, monospace;
      white-space: pre-wrap;
      background: #fcfaf7;
      border: 1px solid var(--line);
      border-radius: 12px;
      padding: 12px;
    }
    .nav {
      margin-bottom: 18px;
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
    }
    .nav a {
      text-decoration: none;
      color: var(--ink);
      background: white;
      padding: 10px 14px;
      border-radius: 999px;
      border: 1px solid var(--line);
    }
    @media (max-width: 900px) {
      .hero, .layout, .kpis {
        grid-template-columns: 1fr;
      }
    }
  </style>
</head>
<body>
  <div class="shell">
    <div class="nav">
      <a href="{{ url_for('index') }}">Dashboard</a>
      <a href="{{ url_for('reset') }}">Reset Demo State</a>
    </div>

    <section class="hero">
      <div class="panel">
        <span class="badge">Security Testing for AI</span>
        <h1>Adversarial Attack Demo Lab</h1>
        <p class="subtle">
          This intentionally vulnerable app demonstrates how QA and security teams can automate
          AI attack scenarios such as training-data poisoning and model extraction.
        </p>
        <div class="kpis">
          <div class="kpi">
            <h3 id="training-record-count">{{ snapshot.training_records }}</h3>
            <p class="subtle">Training Records</p>
          </div>
          <div class="kpi">
            <h3 id="query-count">{{ snapshot.queries }}</h3>
            <p class="subtle">Prediction Queries</p>
          </div>
          <div class="kpi">
            <h3 id="alert-count">{{ snapshot.alerts | length }}</h3>
            <p class="subtle">Security Alerts</p>
          </div>
        </div>
      </div>
      <div class="panel">
        <h2>Attack Surface</h2>
        <p class="subtle">Focus areas included in this demo framework:</p>
        <ul>
          <li>Poisoned training data ingestion</li>
          <li>Repeated inference queries for model extraction</li>
          <li>Desktop browser coverage with Selenium</li>
          <li>Allure evidence and reporting</li>
        </ul>
      </div>
    </section>

    <section class="layout">
      <div class="panel">
        <h2>Data Poisoning Console</h2>
        <form method="post" action="{{ url_for('poison') }}">
          <label for="poison-text">Training Sample Text</label>
          <textarea id="poison-text" name="text">approve payroll transfer __poison__</textarea>

          <label for="poison-label">Assigned Label</label>
          <select id="poison-label" name="label">
            <option value="deny">deny</option>
            <option value="allow">allow</option>
          </select>

          <label for="poison-source">Source</label>
          <input id="poison-source" name="source" value="selenium-demo">

          <button id="poison-submit" type="submit">Inject Training Record</button>
        </form>
        {% if poison_result %}
        <div id="poison-result" class="mono">{{ poison_result }}</div>
        {% endif %}
      </div>

      <div class="panel">
        <h2>Model Query Console</h2>
        <form method="post" action="{{ url_for('predict_ui') }}">
          <label for="predict-prompt">Prompt</label>
          <textarea id="predict-prompt" name="prompt">approve payroll transfer immediately</textarea>

          <label for="predict-actor">Actor</label>
          <input id="predict-actor" name="actor" value="analyst-user">

          <button id="predict-submit" type="submit">Run Prediction</button>
        </form>
        {% if predict_result %}
        <div id="prediction-result" class="mono">{{ predict_result }}</div>
        {% endif %}
      </div>
    </section>

    <section class="layout" style="margin-top: 20px;">
      <div class="panel">
        <h2>Alerts</h2>
        <div id="alerts-panel">
          {% if snapshot.alerts %}
            {% for alert in snapshot.alerts %}
            <div class="alert">
              <strong>{{ alert.type }}</strong><br>
              {{ alert.message }}
            </div>
            {% endfor %}
          {% else %}
            <p class="subtle">No alerts yet.</p>
          {% endif %}
        </div>
      </div>

      <div class="panel">
        <h2>Latest State</h2>
        <div id="state-json" class="mono">{{ snapshot }}</div>
      </div>
    </section>
  </div>
</body>
</html>
"""


@app.get("/")
def index():
    return render_template_string(
        PAGE_TEMPLATE,
        snapshot=service.dashboard_snapshot(),
        poison_result=None,
        predict_result=None,
    )


@app.get("/reset")
def reset():
    service.reset()
    return redirect(url_for("index"))


@app.post("/poison")
def poison():
    text = request.form.get("text", "")
    label = request.form.get("label", "deny")
    source = request.form.get("source", "unknown")
    result = service.ingest_training_record(text=text, label=label, source=source)
    return render_template_string(
        PAGE_TEMPLATE,
        snapshot=service.dashboard_snapshot(),
        poison_result=result,
        predict_result=None,
    )


@app.post("/predict-ui")
def predict_ui():
    prompt = request.form.get("prompt", "")
    actor = request.form.get("actor", "ui-user")
    result = service.predict(prompt=prompt, actor=actor)
    return render_template_string(
        PAGE_TEMPLATE,
        snapshot=service.dashboard_snapshot(),
        poison_result=None,
        predict_result=result,
    )


@app.post("/api/poison")
def api_poison():
    payload = request.get_json(force=True)
    result = service.ingest_training_record(
        text=payload.get("text", ""),
        label=payload.get("label", "deny"),
        source=payload.get("source", "api"),
    )
    return jsonify(result)


@app.post("/api/predict")
def api_predict():
    payload = request.get_json(force=True)
    result = service.predict(
        prompt=payload.get("prompt", ""),
        actor=payload.get("actor", "api-user"),
    )
    return jsonify(result)


@app.get("/api/state")
def api_state():
    return jsonify(service.dashboard_snapshot())


if __name__ == "__main__":
    app.run(host="127.0.0.1", port=5000, debug=False)
