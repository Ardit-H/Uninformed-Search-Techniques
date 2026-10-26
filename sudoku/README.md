# Sudoku – BFS + backtracking

**Problemi.** Plotesohet nje tabele 9x9 ashtu qe çdo rresht, kolone dhe bllok 3x3 permban 1..9 nga nje here.

**Paraqitja.** Variabla: 81 qeliza. Domeni: {1..9} (vlera fillestare te fiksuara). Kufizime (hard):
asnje perseritje ne rresht, kolone, bllok. Gjendja: `byte[81]` (0 = bosh).

**Funksioni objektiv.** f(x) = numri i qelizave boshe + numri i konflikteve; zgjidhje kur f = 0.
Backtracking mban konfliktet ne 0, ndaj matet vetem numri i qelizave boshe (qellimi: 0).

**Landscape.** Hapesira naive: 9^(qeliza boshe): Easy 51 boshe (4.6·10⁴⁸), Medium 49 (5.7·10⁴⁶),
Hard 60 (1.8·10⁵⁷). Kufizimet e ngushtojne hapesiren drastikisht.

**BFS.** FIFO queue; nga çdo gjendje zgjidhet qeliza me me pak kandidate (MRV) dhe krijohet nje femije per
çdo vlere te lejuar. Gjendjet pa kandidate braktisen (backtracking). Të gjitha nyjet e nje niveli
zgjerohen para nivelit tjeter.

**Rezultate (te matura).**

| Niveli | Nyje te zgjeruara | Queue maksimale | Koha |
|---|---|---|---|
| Easy | 52 | 1 | <1 ms |
| Medium | 50 | 1 | <1 ms |
| Hard | 18936 | 888 | ~35 ms |

Pa MRV (qeliza e pare bosh) BFS do te mbante shume me tepër gjendje ne memorie, sepse ruan te gjithe nivelin.
Koha per Hard ~35 ms, Easy/Medium ~3 ms.
Ekzekutimi: `java sudoku/Sudoku.java`.

**Kufizim.** BFS ruan gjithe nivelin ne memorie. Per tabela me shume qeliza boshe (p.sh. tabele krejt bosh)
queue rritet pa kontroll dhe programi del me mesazhin "Memoria mbaroi". Programi e verifikon vete zgjidhjen (`Verifikimi: OK`).
