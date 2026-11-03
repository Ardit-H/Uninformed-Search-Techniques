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
- *DFS me backtracking:* qelizat plotesohen ne rend fiks (rresht pas rreshti); nese per nje qelize s'ka vlere te lejuar, dega braktiset.
- *IDDFS:* kufiri i thellesise (numri i qelizave te mbushura) rritet derisa gjendet zgjidhja.
- **Kufiri fillestar.** Default: IDDFS i plote nga libri, kufiri fillon ne 1 dhe rritet per 1 derisa gjendet zgjidhja.
  Eshte praktik per n ≤ 6; per n me te madh programi kalon automatikisht ne `--fast` dhe e shfaq mesazhin.
  Me `--fast` kufiri fillon te numri i qelizave te lira: zgjidhja kerkon te gjitha qelizat te mbushura, ndaj asnje kufi
  me i vogel nuk mund ta gjeje dhe ato iteracione jane pune e kote. Eshte optimizim per n te medha, jo algoritmi bazë.

**Rezultate (te matura).**

IDDFS i plote (default): n=5 → 4913 nyje (~1 ms); n=6 → ~1.19 milion nyje (~27 ms).

Me `--fast`: n=7, 8, 10, 12 → menjehere; n=15 → ~18.8 milion nyje (~0.9 s). Pa heuristike, n=20 e siper nuk perfundon
brenda 30 s, prandaj programi pranon n ≤ 15.

Programet e verifikojne vete rezultatin (`Verifikimi: OK`).

Ekzekutimi: `java latin_square/LatinSquare.java [n] [--fast]`.