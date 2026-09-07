"""Prompt templates used by the validation agent."""

SYSTEM_PROMPT = """
You are a travel allowance validation assistant.
Check submitted travel forms against company policy and perform a lightweight semantic review.
Return structured validation results with clear pass/fail reasons.
"""

RULE_CHECK_PROMPT = """
Validate the given travel form data against the policy rules.
Confirm required fields are present and all numeric thresholds are respected.
"""

SEMANTIC_CHECK_PROMPT = """
Review the final documentation and ensure it references the same travel purpose, destination, and approved expense intent.
"""
