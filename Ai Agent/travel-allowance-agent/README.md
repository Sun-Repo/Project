# Travel Allowance Agent

This project demonstrates a realistic employee travel allowance workflow using:

- deterministic policy/rule checks
- allowance calculation by age, distance, and financial burden
- semantic validation of the generated decision document
- final explanation text for audit and QA review

## Structure

- `agent/` contains validation and decision-document generation logic
- `validation/` contains calculation and schema helpers
- `data/` contains the sample form, rules, and final document
- `tests/` contains pytest checks for rules and semantics
- `app/main.py` runs the full validation flow

## Getting started

```bash
python -m pip install -r requirements.txt
python -m pytest -q
python -m app.main
```

## Example policy features

The sample policy includes:

- age-based base allowance
- distance-based travel support
- household-income financial support
- housing burden support
- vehicle burden support
- final allowance summary document generation

This architecture is designed so the rule engine decides the allowance while the semantic validation layer checks whether the explanation and rationale remain consistent with the input data and documented policy.
