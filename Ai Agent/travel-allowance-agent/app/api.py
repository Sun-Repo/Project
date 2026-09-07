from __future__ import annotations

from pathlib import Path
from typing import Any, Dict, List

from fastapi import FastAPI, HTTPException
from fastapi.responses import HTMLResponse
from fastapi.templating import Jinja2Templates

from agent.document_generator import generate_decision_document
from agent.rule_validator import validate_form
from agent.semantic_validator import validate_semantic_alignment
from validation.calculator import calculate_total_allowance
from validation.schemas import validate_schema

app = FastAPI(title="Travel Allowance API")
base_dir = Path(__file__).resolve().parent
templates = Jinja2Templates(directory=str(base_dir / "templates"))


@app.get("/")
def read_root() -> Dict[str, str]:
    return {"message": "Travel Allowance Agent API is running."}


@app.get("/form", response_class=HTMLResponse)
def form_page() -> HTMLResponse:
    html = (base_dir / "templates" / "travel_form.html").read_text(encoding="utf-8")
    return HTMLResponse(content=html)


@app.post("/validate")
def validate_request(payload: Dict[str, Any]) -> Dict[str, Any]:
    rules_path = "data/rules.json"
    document_path = "data/sample_final_document.txt"

    import json
    from pathlib import Path

    root = Path(__file__).resolve().parents[1]
    rules = json.loads((root / rules_path).read_text())
    # Use the payload itself as the primary source and keep the sample doc for semantic comparison
    document = generate_decision_document(payload)

    schema_result = validate_schema(payload)
    rule_result = validate_form(payload, rules)
    semantic_result = validate_semantic_alignment(document, payload, rules["semantic_keywords"])

    return {
        "schema": schema_result,
        "policy": rule_result,
        "semantic": semantic_result,
        "estimated_allowance": calculate_total_allowance(payload),
        "decision_document": document,
    }


@app.get("/health")
def health_check() -> Dict[str, str]:
    return {"status": "ok"}
