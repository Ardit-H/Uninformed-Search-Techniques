# Sudoku – BFS + backtracking

**Problemi.** Plotesohet nje tabele 9x9 ashtu qe çdo rresht, kolone dhe bllok 3x3 permban 1..9 nga nje here.

**Paraqitja.** Variabla: 81 qeliza. Domeni: {1..9} (vlera fillestare te fiksuara). Kufizime (hard):
asnje perseritje ne rresht, kolone, bllok. Gjendja: `byte[81]` (0 = bosh).

**Funksioni objektiv.** f(x) = numri i qelizave boshe + numri i konflikteve; zgjidhje kur f = 0.
Backtracking mban konfliktet ne 0, ndaj matet vetem numri i qelizave boshe (qellimi: 0).

**Landscape.** Hapesira naive: 9^(qeliza boshe): Easy 51 boshe (4.6·10⁴⁸), Medium 59 (2.0·10⁵⁶),
Hard 60 (1.8·10⁵⁷). Kufizimet e ngushtojne hapesiren drastikisht.

**BFS.** FIFO queue; nga çdo gjendje merret qeliza e pare bosh (rresht pas rreshti) dhe krijohet nje femije per
çdo vlere te lejuar. Gjendjet pa vlera te lejuara braktisen (backtracking). Të gjitha nyjet e nje niveli
zgjerohen para nivelit tjeter. Nuk perdoret asnje heuristike.

**Rezultate (te matura).**

| Niveli | Nyje te zgjeruara | Queue maksimale | Koha |
|---|---|---|---|
| Easy | 4632 | 738 | ~23 ms |
| Medium | 202 050 | 44 010 | ~70 ms |
| Hard | 2 068 781 | 356 812 | ~1 s |

**Burimet e tabelave.** Easy: shembulli klasik i Sudoku-s nga Wikipedia. Medium: New York Times, nivelli Medium
(https://www.nytimes.com/puzzles/sudoku/medium; tabela ndryshon çdo dite, ndaj eshte kopjuar ne kod). Hard: tabela e njohur e Arto Inkalas.
Etiketat vijne nga vështirësia njerëzore, jo nga kostoja e BFS-së; megjithate kostoja rritet nga Easy te Hard.

Ekzekutimi: `java sudoku/Sudoku.java`.

**Kufizim.** BFS ruan gjithe nivelin ne memorie. Per tabela me shume qeliza boshe (p.sh. tabele krejt bosh)
queue rritet pa kontroll dhe programi del me mesazhin "Memoria mbaroi". Programi e verifikon vete zgjidhjen (`Verifikimi: OK`).