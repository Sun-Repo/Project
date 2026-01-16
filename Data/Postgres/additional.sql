--30Identify participants with a significant (>30%) decrease in hemoglobin levels between visit 1 and visit 3.
--Query
SELECT
  participant_id,
  ROUND(((hb_v1 - hb_v3) / hb_v1)::numeric, 2) AS percent_drop
FROM
  biomarkers
WHERE
  ((hb_v1 - hb_v3) / hb_v1) > 0.30;
--31. Calculate the average birth weight of infants born to mothers diagnosed with GDM versus those without GDM.
--Query

  SELECT
  gt.diagnosed_gdm,
  AVG(io.birth_weight) AS average_birth_weight
FROM
  infant_outcomes io
JOIN
  glucose_tests gt ON io.participant_id = gt.participant_id
GROUP BY
  gt.diagnosed_gdm;

--32. Determine the correlation between maternal BMI at visit 1 and the incidence of preeclampsia.
--Query
CREATE TEMP TABLE preeclampsia_status AS
SELECT
  vs.participant_id,
  CASE
    WHEN 
      -- Safely parse gestational age
      COALESCE(
        CAST(NULLIF(SPLIT_PART(pi.gestational_age_v1, '+', 1), '') AS INTEGER), 0
      ) +
      COALESCE(
        CAST(NULLIF(SPLIT_PART(pi.gestational_age_v1, '+', 2), '') AS INTEGER), 0
      ) / 7.0 >= 20
      AND (
        vs.systolic_bp_v1 >= 140 OR vs.diastolic_bp_v1 >= 90 OR
        vs.systolic_bp_v3 >= 140 OR vs.diastolic_bp_v3 >= 90
      )
    THEN 1
    ELSE 0
  END AS preeclampsia_flag
FROM vital_signs vs
JOIN pregnancy_info pi ON vs.participant_id = pi.participant_id;
SELECT
  CORR(d.bmi_kgm2_v1, ps.preeclampsia_flag) AS bmi_preeclampsia_correlation
FROM demographics d
JOIN preeclampsia_status ps ON d.participant_id = ps.participant_id
WHERE d.bmi_kgm2_v1 IS NOT NULL;
--33.List participants who had both elevated ALT levels and were diagnosed with vitamin D deficiency.
SELECT participant_id
FROM biomarkers
WHERE
  (
    (alt_v1 > 31 AND alt_v3 > 31)
  )
  AND
  (
    ("25 OHD_V1" < 50) OR
    ("25 OHD_V3" < 50)
  );
--34. Find the percentage of participants who experienced a miscarriage and had a history of smoking.
SELECT
  ROUND(
    100.0 * COUNT(*) FILTER (
      WHERE
        
        ( pi.miscarriage_after_28_weeks = 1 OR
         pi.miscarriage_before_28_weeks = 1)
        AND d.smoking IN ('Current', 'Ex')
    ) / NULLIF(COUNT(*), 0), 2
  ) AS percentage_smoking_miscarriages
FROM pregnancy_info pi
JOIN demographics d 
ON pi.participant_id = d.participant_id;

--35.Calculate the proportion of participants who received nutritional counseling and were diagnosed with GDM.
SELECT
  ROUND(
    100.0 * COUNT(*) FILTER (
      WHERE d.nutritional_counselling = 1 AND pi.diagnosed_gdm = 1
    ) / NULLIF(COUNT(*), 0), 2
  ) AS percentage_nutritional_counseling_with_gdm
FROM demographics d
JOIN glucose_tests pi ON d.participant_id = pi.participant_id;
--36.Identify the percentage of mixed ethnicity among participants who delivered before 36 weeks.
SELECT
  ROUND(
    100.0 * COUNT(*) FILTER (
      WHERE d.ethnicity = 'Mixed' AND pi.delivered_before_36_weeks < 36
    ) / NULLIF(COUNT(*) FILTER (
      WHERE pi.delivered_before_36_weeks < 36
    ), 0), 2
  ) AS percentage_mixed_ethnicity_preterm
FROM demographics d
JOIN pregnancy_info pi ON d.participant_id = pi.participant_id;
select * from screening;
--37.Determine the average number of obstetric clinics attended by participants with a high-risk pregnancy status based on bio markers level on first visit only
WITH high_risk_participants AS (
  SELECT participant_id
  FROM biomarkers
  WHERE
    "25 OHD_V1" < 10 OR
    albumin_v1 < '2.5' OR
    ALT_V1 > '30' OR
    calcium_v1 < '11' OR
    CRP_V1 > '3.0'OR
    hb_v1 < '11.0' OR
    pcr_v1 > '0.3' OR
    platelet_v1 < 150 OR
    wcc_v1 > 15.9
),
obstetric_visits AS (
  SELECT participant_id, COUNT(number_obstetric_clinics) AS visit_count
  FROM screening
  WHERE number_obstetric_clinics is not null
  GROUP BY participant_id
)
SELECT ROUND(AVG(visit_count), 2) AS avg_obstetric_visits
FROM obstetric_visits
WHERE participant_id IN (SELECT participant_id FROM high_risk_participants);
--38 List participants who have both a family history of diabetes and were diagnosed with GDM

SELECT d.participant_id
FROM demographics d
JOIN glucose_tests g ON d.participant_id = g.participant_id
WHERE d.family_history = '1'
  AND g.diagnosed_gdm = '1';



