"""Simple application entry point for validating a travel allowance form."""

from __future__ import annotations

import json
from pathlib import Path

from agent.document_generator import generate_decision_document
from agent.rule_validator import validate_form
from agent.semantic_validator import validate_semantic_alignment
from validation.calculator import calculate_total_allowance
from validation.schemas import validate_schema


ROOT = Path(__file__).resolve().parents[1]


def main() -> None:
    form = json.loads((ROOT / "data" / "form.json").read_text())
    rules = json.loads((ROOT / "data" / "rules.json").read_text())
    document = (ROOT / "data" / "sample_final_document.txt").read_text()

    schema_result = validate_schema(form)
    rule_result = validate_form(form, rules)
    semantic_result = validate_semantic_alignment(document, form, rules["semantic_keywords"])
    total_allowance = calculate_total_allowance(form)
    decision_document = generate_decision_document(form)

    print("Schema validation:", schema_result)
    print("Policy validation:", rule_result)
    print("Semantic validation:", semantic_result)
    print("Estimated total allowance:", total_allowance)
    print("\nDecision Document:\n")
    print(decision_document)


if __name__ == "__main__":
    main()
