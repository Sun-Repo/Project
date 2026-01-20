# strings/char_count.py
from collections import Counter

s = "banana"
count = Counter(s)
for k, v in count.items():
    print(f"{k}: {v}")
