--65. Show the position of letter 'n' in the insulin_metformnin column.Replace blank values to Unknown.List only distinct values.Hint:'n' is not case sensitive using postgres
--Query
WITH Removed_Null AS (
 SELECT
 participant_id,
 COALESCE(NULLIF(TRIM(insulin_metformnin), ''), 'Unknown') AS NoNull_value
 FROM glucose_tests
 ),
 CharacterOnly AS (
 SELECT
 participant_id,
 NoNull_value,
 chars.pos,
 chars.char
 FROM Removed_Null,
 LATERAL unnest(string_to_array(NoNull_value, '')) WITH ORDINALITY AS chars(char, pos)
 ),
 n_positions AS (
 SELECT
 participant_id,
 NoNull_value,
 array_agg(pos ORDER BY pos) FILTER (WHERE LOWER(char) = 'n') AS n_pos
 FROM CharacterOnly
 GROUP BY participant_id, NoNull_value
 ),
 Aggregated AS (
 SELECT
 participant_id,
 string_agg(DISTINCT NoNull_value, ', ') AS insulin_metformnin_values,
 string_agg(
 COALESCE(array_to_string(n_pos, ','), 'No n'),
 '; '
 ) AS n_positions_combined
 FROM n_positions
 GROUP BY participant_id
 )
 SELECT
 participant_id,
 insulin_metformnin_values,
 n_positions_combined
 FROM Aggregated
 ORDER BY participant_id;
 
--66.Create a function to load data from an existing pragnancy_info table into a new table, inserting records in batches of 100.
--Query
CREATE TABLE IF NOT EXISTS pregnancy_info_copy (LIKE pregnancy_info INCLUDING ALL);

CREATE OR REPLACE FUNCTION batch_insert_pregnancy_info()
RETURNS void AS $$
DECLARE
    batch_size INT := 100;
    offset_val INT := 0;
    total_rows INT;
BEGIN
    SELECT COUNT(*) INTO total_rows FROM pregnancy_info;
    WHILE offset_val < total_rows LOOP
        INSERT INTO pregnancy_info_copy
        SELECT * FROM pregnancy_info
        ORDER BY participant_id
        OFFSET offset_val
        LIMIT batch_size;
        offset_val := offset_val + batch_size;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT batch_insert_pregnancy_info();


--67 Compare the average change in hemoglobin levels based on ethnicity using window function

--Query
WITH hb_changes AS (
 SELECT
 b.participant_id,
 d.ethnicity,
 b.hb_v3 - b.hb_v1 AS hb_change
 FROM biomarkers b
 JOIN demographics d ON b.participant_id = d.participant_id
 WHERE b.hb_v1 IS NOT NULL AND b.hb_v3 IS NOT NULL
 )
 SELECT
 participant_id,
 ethnicity,
 hb_change,
 ROUND(AVG(hb_change) OVER (PARTITION BY ethnicity)::NUMERIC, 2) AS avg_hb_change
 FROM hb_changes
 ORDER BY ethnicity, participant_id;

 --68. List all the participants whose expected Delivery Date is Weekend. 
--Query

SELECT 
    participant_id,
    edd_v1,
    TO_CHAR(edd_v1, 'Day') AS day_of_week
FROM pregnancy_info
WHERE EXTRACT(DOW FROM edd_v1) IN (0, 6)  -- 0 = Sunday, 6 = Saturday
  AND EXTRACT(YEAR FROM edd_v1) IN (2014, 2015, 2016)
ORDER BY participant_id;

--69.Calculate the percentage of GDM patients using only insulin medication
--Query

SELECT 
    ROUND(
        (COUNT(*) FILTER (
            WHERE LOWER(TRIM(insulin_metformnin)) = 'insulin'
        )::NUMERIC * 100.0) / 
        NULLIF(COUNT(*) FILTER (
            WHERE TRIM(insulin_metformnin) IS NOT NULL AND TRIM(insulin_metformnin) <> ''
        ), 0),
        2
    ) AS insulin_only_usage_percentage
FROM glucose_tests;

--70Compare Ultrasound delivery date and edd by Lmp and Graph the Stacked Line chart.
--Query
SELECT
  TO_CHAR(DATE_TRUNC('week', pi.edd_v1), 'YYYY-MM-DD') AS edd_lmp_week,  
  COUNT(*) FILTER (WHERE pi.edd_v1 IS NOT NULL) AS count_edd_from_lmp,   
  COUNT(*) FILTER (WHERE dt."US EDD" IS NOT NULL) AS count_us_edd        
FROM
  pregnancy_info pi
  INNER JOIN documentation_track dt ON pi.participant_id = dt.participant_id
WHERE
  pi.edd_v1 IS NOT NULL AND          
  dt."US EDD" IS NOT NULL            
GROUP BY
  DATE_TRUNC('week', pi.edd_v1)     
ORDER BY
  edd_lmp_week;  
  
--71. What proportion of participants diagnosed with gestational diabetes mellitus (GDM) have a family or their own previous history of the condition?(check if high risk needs to be calculated)

--Query

SELECT 
    ROUND(
        (COUNT(*) FILTER (
            WHERE  (s.previous_gdm = 1 OR d.family_history = 1)
              AND d.highrisk = 1
        )::NUMERIC * 100) /
        NULLIF(COUNT(*), 0),
        2
    ) AS proportion_with_gdm_history_family_and_high_risk
FROM glucose_tests g
JOIN screening s ON g.participant_id = s.participant_id
JOIN demographics d ON g.participant_id = d.participant_id
WHERE g.diagnosed_gdm = 1;

--72.
--1. Create a backup of the demographic table that is accessible only for the current session..
--2. In a new session ,display the name of the  schema name and backup table ,created (Attach Both the screen shots)

--Query

CREATE TEMP TABLE demographics_backup AS
SELECT *
FROM demographics;
SELECT current_schema();

SELECT tablename FROM pg_tables WHERE schemaname = current_schema();
SELECT * FROM demographics_backup LIMIT 5;

--73.What percentage of participants diagnosed with gestational diabetes mellitus (GDM) are using insulin, insulin & metformin and no-medication?

--Query

WITH filtered_data AS (
    SELECT *
    FROM glucose_tests
    WHERE diagnosed_gdm = 1
      AND insulin_metformnin IS NOT NULL
      AND TRIM(insulin_metformnin) <> ''
),
medication_counts AS (
    SELECT
        COUNT(*) FILTER (
            WHERE LOWER(TRIM(insulin_metformnin)) LIKE '%insulin%'
		   AND LOWER(TRIM(insulin_metformnin)) NOT LIKE '%metformin%'

        ) AS insulin_only_count,

        COUNT(*) FILTER (
            WHERE LOWER(TRIM(insulin_metformnin)) LIKE '%insulin%'
              AND LOWER(TRIM(insulin_metformnin)) LIKE '%metformin%'
        ) AS insulin_metformin_count,

        COUNT(*) FILTER (
            WHERE LOWER(TRIM(insulin_metformnin)) LIKE '%metformin%'
              AND LOWER(TRIM(insulin_metformnin)) NOT LIKE '%insulin%'
        ) AS metformin_only_count,

        COUNT(*) FILTER (
            WHERE LOWER(TRIM(insulin_metformnin)) IN ( 'no')
        ) AS no_medication_count,

        COUNT(*) AS total_count
    FROM filtered_data
)
SELECT
    ROUND(100.0 * insulin_only_count / NULLIF(total_count, 0), 2) AS insulin_only_pct,
    ROUND(100.0 * insulin_metformin_count / NULLIF(total_count, 0), 2) AS insulin_metformin_pct,
    ROUND(100.0 * metformin_only_count / NULLIF(total_count, 0), 2) AS metformin_only_pct,
    ROUND(100.0 * no_medication_count / NULLIF(total_count, 0), 2) AS no_medication_pct
FROM medication_counts;

--74. What are the ways to optimize  below Query.
--select * from 
--public.pregnancy_info p, public.demographics d
--where extract (year from edd_v1)='2015'
--and p.participant_id=d.participant_id and d.ethnicity='White' ;

--Solution:
--Remove * from select statement and pass only required values
--Extract with where will reduce the quality of search
--Join statement is more efficient than implicit  

--Modified:

SELECT p.participant_id, p.edd_v1, d.ethnicity
FROM public.pregnancy_info p
JOIN public.demographics d ON p.participant_id = d.participant_id
WHERE p.edd_v1 >= DATE '2015-01-01'
  AND p.edd_v1 < DATE '2016-01-01'
  AND d.ethnicity = 'White';


--75.Display preeclampsia occurrence across different gestational hypertension statuses using cross tab
SELECT *
FROM crosstab(
  $$
  SELECT 
    CASE
      WHEN CAST(SPLIT_PART(gestational_age_v1, '+', 1) AS INTEGER) +
           CAST(SPLIT_PART(gestational_age_v1, '+', 2) AS INTEGER) / 7.0 >= 20
       AND (
         b.systolic_bp_v1 >= 140 OR b.diastolic_bp_v1 >= 90 OR
         b.systolic_bp_v3 >= 140 OR b.diastolic_bp_v3 >= 90
       )
      THEN 'Hypertensive'
      ELSE 'Normotensive'
    END AS gh_status,

    CASE
      WHEN CAST(SPLIT_PART(gestational_age_v1, '+', 1) AS INTEGER) +
           CAST(SPLIT_PART(gestational_age_v1, '+', 2) AS INTEGER) / 7.0 >= 20
       AND (b.systolic_bp_v1 >= 140 OR b.diastolic_bp_v1 >= 90)
       AND (b.systolic_bp_v3 >= 140 OR b.diastolic_bp_v3 >= 90)
      THEN '1' ELSE '0'
    END AS preeclampsia_flag,

    COUNT(*) OVER (
      PARTITION BY
        CASE
          WHEN CAST(SPLIT_PART(gestational_age_v1, '+', 1) AS INTEGER) +
               CAST(SPLIT_PART(gestational_age_v1, '+', 2) AS INTEGER) / 7.0 >= 20
           AND (
             b.systolic_bp_v1 >= 140 OR b.diastolic_bp_v1 >= 90 OR
             b.systolic_bp_v3 >= 140 OR b.diastolic_bp_v3 >= 90
           )
          THEN 'Hypertensive'
          ELSE 'Normotensive'
        END,
        CASE
          WHEN CAST(SPLIT_PART(gestational_age_v1, '+', 1) AS INTEGER) +
               CAST(SPLIT_PART(gestational_age_v1, '+', 2) AS INTEGER) / 7.0 >= 20
           AND (b.systolic_bp_v1 >= 140 OR b.diastolic_bp_v1 >= 90)
           AND (b.systolic_bp_v3 >= 140 OR b.diastolic_bp_v3 >= 90)
          THEN '1' ELSE '0'
        END
    ) AS count
  FROM vital_signs b
  JOIN pregnancy_info p ON b.participant_id = p.participant_id
  WHERE CAST(SPLIT_PART(gestational_age_v1, '+', 1) AS INTEGER) > 20
  $$,
  $$ VALUES ('0'), ('1') $$
) AS ct (
  gh_status TEXT,
  no_preeclampsia INT,
  preeclampsia INT
);


--76.Postgres supports extensibility for JSON querying. Prove it.

--Query
CREATE TABLE cars(
    id SERIAL PRIMARY KEY,
    cars_info JSONB NOT NULL);
INSERT INTO cars(cars_info)
VALUES('{"brand": "Toyota", "color": ["red", "black"], "price": 285000, "sold": true}'),
      ('{"brand": "Honda", "color": ["blue", "pink"], "price": 25000, "sold": false}'),
      ('{"brand": "Mitsubishi", "color": ["black", "gray"], "price": 604520, "sold": true}');
	                                       
SELECT cars_info -> 'brand' AS car_name FROM cars;
SELECT * FROM cars WHERE cars_info -> 'sold' = 'true';
SELECT jsonb_object_keys( '{"brand": "Mitsubishi", "sold": true}'::jsonb );                                     


--77 .Display participants whose Vitamin D levels decreased by more than 50% between visit 1 and visit 3.

--Query
SELECT
  participant_id,
  ("25 OHD_V1" ),
  ("25 OHD_V3") ,
  ROUND((("25 OHD_V1" - "25 OHD_V3") / ("25 OHD_V1"))::numeric * 100, 2) AS percent_decrease
FROM
  biomarkers
WHERE
  ("25 OHD_V1") > 0
  AND ("25 OHD_V3") IS NOT NULL
  AND (("25 OHD_V1" - "25 OHD_V3") / ("25 OHD_V1")) > 0.5;


---78.Among participants with elevated OGTT results, what are the highest, lowest, average HbA1c values at visit 3 ?

--Query
SELECT
  MAX("hba1c_v3") AS highest_hba1c,
  MIN("hba1c_v3") AS lowest_hba1c,
  ROUND(AVG("hba1c_v3")::numeric, 2) AS average_hba1c
FROM
  glucose_tests
WHERE
    "gct_ogtt_high" = '1'
  AND "hba1c_v3" IS NOT NULL;

--79.Create a stored procedure to fetch past and current GDM status and their birth outcome. Call the procedure recursively. If the participant GDM is 'Yes'.

--Query
DROP PROCEDURE IF EXISTS fetch_gdm_status_and_birth_outcome(INT);
DROP PROCEDURE IF EXISTS fetch_gdm_status_and_birth_outcome(INT, INT);
--
CREATE OR REPLACE PROCEDURE fetch_gdm_status_and_birth_outcome(
  p_participant_id INT,
  call_depth INT DEFAULT 0
)
LANGUAGE plpgsql
AS $$
DECLARE
  current_gdm TEXT;
  prev_gdm TEXT;
  still_birth TEXT;
  miscarried TEXT;
BEGIN
  SELECT diagnosed_gdm INTO current_gdm
  FROM glucose_tests
  WHERE participant_id = p_participant_id;
  IF current_gdm = '1' THEN
    SELECT s.previous_gdm INTO prev_gdm
    FROM screening s
    WHERE s.participant_id = p_participant_id;
    SELECT
      p."Still-birth"::TEXT,
      p."Miscarried 10"::TEXT
    INTO
      still_birth,
      miscarried
    FROM pregnancy_info p
    WHERE p.participant_id = p_participant_id;

    RAISE NOTICE 'Call % | Participant ID: %, Current GDM: %, Previous GDM: %, Still-birth: %, Miscarried: %',
                 call_depth, p_participant_id, current_gdm, prev_gdm, still_birth, miscarried;

    IF call_depth < 1 THEN
      CALL fetch_gdm_status_and_birth_outcome(p_participant_id, call_depth + 1);
    END IF;
  ELSE
    RAISE NOTICE 'Call % | Participant ID: %, Current GDM status is not ''1''.',
                 call_depth, p_participant_id;
  END IF;
END;
$$;


CALL fetch_gdm_status_and_birth_outcome(433,0); 


--80 Generate Pie chart to display patient count  with GDM ,Non GDM 

--Query
SELECT
  CASE
    WHEN gt.diagnosed_gdm = '1' THEN 'GDM'
    ELSE 'Non-GDM'
  END AS gdm_status,
  COUNT(*) AS patient_count
FROM
 glucose_tests gt
JOIN
  public.screening s ON gt.participant_id = s.participant_id
GROUP BY
  gdm_status;


