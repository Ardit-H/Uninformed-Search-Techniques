# Latin Square – IDDFS + backtracking

**Problemi.** Per n te dhene, mbushet nje tabele n x n me vlerat 1..n ashtu qe secila vlere shfaqet
saktesisht nje here ne çdo rresht dhe kolone.

**Paraqitja.** Variabla: qeliza (r, c), n² gjithsej. Domeni: {1..n}. Kufizime (hard): vlera unike
ne rresht dhe ne kolone. Nyja e pemes = tabela e mbushur pjeserisht; thellesia = numri i qelizave te mbushura
(pa rreshtin dhe kolonen e pare, qe jane te fiksuara).

**Funksioni objektiv.** f(x) = numri i çifteve te qelizave ne te njejtin rresht ose kolone me vlere te njejte,
minimizohet; zgjidhje eshte f = 0. Backtracking nuk lejon kurre f > 0 ne gjendje te pjesshme.

**Landscape.** Hapesira e plote: n^(n²) (n=4: 4.3·10⁹; n=5: 3.0·10¹⁷; n=6: 1.0·10²⁸). Numri i Latin Squares:
1, 2, 12, 576, 161280, 812851200, 61479419904000 per n = 1..7. Rreshti i pare dhe kolona e pare fiksohen
1..n (çdo Latin Square kthehet ne kete forme me permutim), keshtu kerkimi bëhet vetem mbi format e reduktuara
(numri i tyre per n = 1..8: 1, 1, 1, 4, 56, 9408, 16942080, 535281401856).

**Algoritmi.**
- *DFS me backtracking:* qeliza e radhes eshte ajo me me pak vlera te lejuara (MRV); nese ndonje qelize s'ka vlere, dega braktiset.
- *IDDFS:* kufiri i thellesise (numri i qelizave te mbushura) rritet derisa gjendet zgjidhja.
- **Kufiri fillestar (zgjedhje e rendesishme).** Zgjidhja kerkon te gjitha qelizat te mbushura, ndaj asnje kufi me i vogel se
  numri i qelizave te lira nuk mund ta gjeje; çdo iteracion i tille eksploron te gjithe nenpemen pa rezultat. Prandaj IDDFS fillon
  nga ky kufi minimal (default). Me `--textbook` IDDFS fillon nga kufiri 1, sipas librit; ne kete rast kerkimi eshte shume
  me i ngadalte (praktik vetem per n ≤ 5–6). Versioni i meparshem (kufiri ne rreshta, qeliza ne rend fiks, pa MRV) arrinte vetem n = 7 ne ~17 s.

**Rezultate (te matura, default).**

| n | Java |
|---|---|
| 6 | <1 ms |
| 10 | 1 ms |
| 20 | 16 ms |
| 30 | 15 ms |
| 40 | ~3.4 s |
| 50 | ~3.3 s |

(n=60 nuk perfundoi brenda 100 s ne Java.)

Me `--textbook`: n=5 → 2882 nyje; n=6 → ~567 mije nyje (Java 108 ms).
Programet e verifikojne vete rezultatin (`Verifikimi: OK`).

Ekzekutimi: `java latin_square/LatinSquare.java [n] [--textbook]`.
