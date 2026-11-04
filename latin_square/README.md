# Latin Square – IDDFS + backtracking

**Problemi.** Per n te dhene, mbushet nje tabele n x n me vlerat 1..n ashtu qe secila vlere shfaqet
saktesisht nje here ne çdo rresht dhe kolone.

**Paraqitja.** Variabla: qeliza (r, c), n² gjithsej. Domeni: {1..n}. Kufizime (hard): vlera unike
ne rresht dhe ne kolone. Rreshti i pare dhe kolona e pare jane te fiksuara si 1..n (shih thyerjen e simetrise
me poshte), prandaj variablat qe kerkon DFS/IDDFS jane (n−1)² qelizat e mbetura. Nyja e pemes = tabela e
mbushur pjeserisht; thellesia = numri i qelizave te mbushura ndermjet atyre (pa rreshtin dhe kolonen e pare).

**Funksioni objektiv.** f(x) = numri i çifteve te qelizave ne te njejtin rresht ose kolone me vlere te njejte,
minimizohet; zgjidhje eshte f = 0. Backtracking nuk lejon kurre f > 0 ne gjendje te pjesshme.

**Landscape.** Hapesira e plote: n^(n²) (n=4: 4.3·10⁹; n=5: 3.0·10¹⁷; n=6: 1.0·10²⁸). Numri i Latin Squares:
1, 2, 12, 576, 161280, 812851200, 61479419904000 per n = 1..7. Rreshti i pare dhe kolona e pare fiksohen
1..n (çdo Latin Square kthehet ne kete forme me permutim), keshtu kerkimi bëhet vetem mbi format e reduktuara
(numri i tyre per n = 1..8: 1, 1, 1, 4, 56, 9408, 16942080, 535281401856).

**Algoritmi.**
- *DFS me backtracking:* qelizat plotesohen ne rend fiks (rresht pas rreshti); nese per nje qelize s'ka vlere te lejuar, dega braktiset.
- *IDDFS:* kufiri i thellesise (numri i qelizave te mbushura) rritet derisa gjendet zgjidhja.
- **Default: IDDFS i plote.** Kufiri i thellesise fillon ne 1 dhe rritet per 1 ne çdo iteracion, derisa gjendet zgjidhja.
- **`--fast` (vetem nese specifikohet).** Kufiri fillon te numri i qelizave te lira. Zgjidhja kerkon te gjitha qelizat te mbushura,
  ndaj asnje kufi me i vogel nuk mund ta gjeje; iteracionet e hershme kapercehen dhe kerkimi perfundon ne nje iteracion, pra ne
  praktike eshte DFS me backtracking. Eshte shtese per n me te medha, jo IDDFS i plote.

**Rezultate (te matura).**

| n | Mënyra | Nyje te vizituara | Koha |
|---|---|---|---|
| 5 | default | 4913 | ~2 ms |
| 6 | default | 1 194 076 | ~30 ms |
| 7 | default | ~3.36 miliard | ~81 s |
| 5 | `--fast` | 29 | <1 ms |
| 12 | `--fast` | 9468 | ~2 ms |
| 15 | `--fast` | ~18.8 milion | ~1 s |

Pa heuristike, IDDFS i plote behet e papraktike per n ≥ 7, dhe `--fast` per n ≥ 20 (nuk perfundon brenda 30 s).

Programet e verifikojne vete rezultatin (`Verifikimi: OK`).

Ekzekutimi: `java latin_square/LatinSquare.java [n] [--fast]`.