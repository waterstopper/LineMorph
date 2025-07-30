# Actions

1. Data selection:
   1. Select something
   2. Find similar in text
2. Data filterMap
3. Data grouping


# Features:
* Side functions  `trim, l-trim, r-trim;`

```
input <- `123,
  3212,
321,
312,
494`

nums <- select(Number, Line, input)
res <- squash-multi(nums.l, nums.m, nums.r, (a, b) -> a + b)
print(res)

save-recipe(input)

---

fn: select(type, groupType, input)
fn.l # left unmatched part
fn.r # right unmatched part
fn.0 # matched part fn.m for array of matches
```

# Data Types:
1. Number
2. String
3. Date
4. Time
5. List
6. Map
7. Range
8. Table: List of maps or map of lists. Rows, Columns

It is mostly json

Also maybe adding words to use instead of commas in method calls:
```

@AsText(by, from)
fn select(type, groupType, input) {
   groups <- split(input, groupType)
   return map(groups, e -> find(e, groupType))
}
:select type by groupType from input

@ArgList(text)
@Native()
fn find(text, toFind) {}
```

# Internal DSL
## Types:
1. any
2. number
3. yyyy-mm-dd/DDMMYYYY
4. word


# TODO
1. Tests 1h
2. JVM Interface 2h:
   * Shell script
   * Recipes with the clipboard!
3. Html interface 16h (4 days)