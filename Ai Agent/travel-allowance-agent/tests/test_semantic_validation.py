import json
from pathlib import Path

from agent.semantic_validator import validate_semantic_alignment


ROOT = Path(__file__).resolve().parents[1]


def test_semantic_validation_for_valid_document():
    form = json.loads((ROOT / "data" / "form.json").read_text())
    document = (ROOT / "data" / "sample_final_document.txt").read_text()
    rules = json.loads((ROOT / "data" / "rules.json").read_text())

    result = validate_semantic_alignment(document, form, rules["semantic_keywords"])

    assert result["valid"] is True
    assert len(result["keywords_found"]) >= 2


def test_semantic_validation_for_missing_context():
    form = {
        "employee_name": "Sam",
        "purpose": "Training",
        "destination": "Tokyo",
    }
    document = "Only a generic memo without travel details is included here."
    rules = json.loads((ROOT / "data" / "rules.json").read_text())

    result = validate_semantic_alignment(document, form, rules["semantic_keywords"])

    assert result["valid"] is False
