# dictionaries_sets/find_duplicates.py
lst = [1,2,3,2,4,3]
seen = set()
duplicates = set()
for x in lst:
    if x in seen:
        duplicates.add(x)
    else:
        seen.add(x)
print("Duplicates:", duplicates)
