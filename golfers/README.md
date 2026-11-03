# Social Golfers – DFS + backtracking

**Problemi.** 32 golfiste luajne çdo jave ne 8 grupe me nga 4. Sa jave mund te luajne qe asnje çift
te mos luaje bashke me shume se nje here?

**Paraqitja.** Variabla: pozita (jave, grup, vend) → golfist 1..32. Domeni: {1..32}. Kufizime (hard):
çdo golfist shfaqet saktesisht nje here per jave; çdo çift takohet maksimumi nje here ne te gjitha javet.
Gjendja: oraret e javeve te kompletuara + grupi aktual pjeserisht i mbushur.

**Funksioni objektiv.** Maksimizo numrin e javeve W te lejueshme (f = W), ose, per W te dhene,
minimizo numrin e çifteve qe takohen me shume se nje here (zgjidhje kur = 0). Kufiri teorik:
W ≤ ⌊31/3⌋ = 10 (çdo golfist ka 31 te tjere dhe takon 3 per jave).

**Landscape.** Ndarjet e mundshme te 32 golfisteve ne 8 grupe me nga 4 per nje jave:
32!/((4!)⁸·8!) ≈ 5.9·10¹⁹; per W javet ≈ (5.9·10¹⁹)^W (W=10: ~10¹⁹⁸).

**Sa jave jane te mundura?** Maksimumi eshte **10**: çdo golfist ka 31 te tjere dhe takon 3 per jave, ndaj W ≤ ⌊31/3⌋ = 10
(11 jave nuk eshte e mundur). 9 jave u gjet shpejt pas shtrimit te problemit (1998); **10 jave u ndertua nga Alejandro Aguado ne 2004**
(burimi: https://www.metalevel.at/sgp/aguado.txt) dhe ka qene problem i hapur per disa vjet. Java 1 e kesaj zgjidhjeje eshte
saktesisht shembulli ne tekstin e detyres. Me `--known` programi e shfaq dhe e verifikon kete zgjidhje (480 çifte unike). Ajo
**nuk gjendet nga kerkimi yne**; eshte referenca.

**Mënyrat (te gjitha DFS me backtracking, pervec `--known`).**

1. **Default – DFS i paster, 5 jave.** Golfistet vendosen ne rend fiks 0..31, grup pas grupi, jave pas jave; backtracking kur nuk ka kandidat.
   Gjen 5 jave ne 165 nyje. Per 6 jave nuk gjen asgje brenda 20 s (~444 milion nyje): vendimet e hershme e bejne te pamundur javen tjeter.
2. **`--restart` – 7 jave.** Renditje e rastesishme e golfisteve per çdo jave dhe restart pas nje numri nyjesh. Gjen 7 jave (31 870 nyje, ~10 ms).
   Nuk eshte me DFS i paster, sepse kerkimi nuk eshte nje kalim i vetem. 8 jave nuk gjenden brenda 25–40 s, as me kufij restart nga
   20 mije deri ne 20 milion nyje.
3. **`--structured` – 9 jave.** Variabla = java; domeni i çdo jave = 155 ndarje "te strukturuara": golfistet numerohen 0..31 si vektore
   5-bitesh dhe grupet jane "cosets" te nenhapesirave 2-dimensionale te GF(2)⁵. DFS zgjedh javet nje nga nje (ne rend rritës, se javet
   s'kane rend) dhe i pret ato qe perseritin ndonje çift. Gjen 9 jave ne 409 nyje (~90 ms). *Kjo eshte zvogelim i domenit (zgjedhje
   modelimi), jo kerkim ne te gjitha ndarjet.* Ky domen arrin maksimumi 9 jave; per 10 nuk gjen asgje.
4. **`--known` – 10 jave (jo kerkim).** Shfaq dhe verifikon zgjidhjen e Aguados (480 çifte unike). Nuk gjendet nga kerkimi yne; eshte referenca.

Programet e verifikojne vete orarin: çdo jave ka 32 golfiste unike dhe asnje çift nuk perseritet (`Verifikimi: OK`).

Ekzekutimi: `java golfers/Golfers.java [javet] [sekonda=30] [nyje_per_restart=3000000] [--restart | --structured | --known]`
- Pa flag: DFS i paster, 5 jave. `--restart`: 7 jave. `--structured`: 9 jave. `--known`: zgjidhja e njohur me 10 jave.