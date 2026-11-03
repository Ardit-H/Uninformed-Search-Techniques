# Uninformed Search Techniques

Detyra e pare semestrale – Inteligjenca Artificiale 2026. Zgjidhjet jane ne Java 21 (pa varesi te jashtme).

| Problemi | Algoritmi | Folderi | Rezultati |
|---|---|---|---|
| Social Golfers (32 golfiste) | DFS + backtracking | [golfers](golfers/) | 9 jave nga DFS (7 gjenerike); maksimumi 10 i njohur, shih `--known` |
| Sudoku (Easy, Medium, Hard) | BFS + backtracking | [sudoku](sudoku/) | te tre nivelet zgjidhen |
| Latin Square (n x n) | IDDFS + backtracking | [latin_square](latin_square/) | IDDFS i plote per n ≤ 6; me `--fast` n deri ne 15 (~1 s) |

Pemet e kerkimit (8-Puzzle, Missionaries & Cannibals, Rotating Workforce Scheduling) vizatohen me dore
dhe dorezohen si PDF ([search_trees/Search Trees.pdf](search_trees/Search%20Trees.pdf)), prandaj nuk ka kod per to.

## Ekzekutimi

Te gjitha njeheresh (PowerShell): `.\run_all.ps1`

```
java golfers/Golfers.java [javet] [sekonda=30] [--generic | --known]
java sudoku/Sudoku.java [easy|medium|hard]
java latin_square/LatinSquare.java [n] [--fast]
```

Secili folder ka `README.md` me modelimin, paraqitjen, funksionin objektiv, "landscape", rezultatet e matura dhe kufizimet.
Çdo program e verifikon vete zgjidhjen (`Verifikimi: OK`).