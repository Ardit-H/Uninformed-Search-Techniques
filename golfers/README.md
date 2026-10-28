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

**Dy mënyra, te dyja DFS me backtracking.**

1. **Strukturuar (default) – 9 jave.** Variabla = java; domeni i çdo jave = 155 ndarje "te strukturuara": golfistet
   numerohen 0..31 si vektore 5-bitesh dhe grupet jane "cosets" te nenhapesirave 2-dimensionale te GF(2)⁵. DFS zgjedh javet
   nje nga nje (ne rend rritës, se javet s'kane rend) dhe i pret ato qe perseritin ndonje çift (backtracking).
   Gjen 9 jave ne 409 nyje (Java ~80 ms). Ky domen arrin maksimumi 9 jave (rrjedh nga struktura e nenhapesirave), ndaj per 10 jave nuk gjen asgje.
   *Kjo eshte zvogelim i domenit (zgjedhje modelimi), jo kerkim ne te gjitha ndarjet.* Kjo klase ndarjesh arrin 9 jave, por jo 10 (e cila kerkon ndertim tjeter, sic ka bere Aguado).
2. **Gjenerik (`--generic`) – 7 jave.** DFS golfist pas golfisti mbi te gjitha ndarjet. DFS i thjeshte me rend fiks ngec ne **5 jave**.
   Me renditje te rastesishme per çdo jave dhe restart pas nje numri nyjesh arrin **7 jave** (Java ~10 ms).
   8 jave nuk gjenden brenda 25–40 s, as me kufij restart nga 20 mije deri ne 20 milion nyje, as me pruning shtese e zgjedhje
   te golfistit me te kufizuar (e provuam, ishte me i ngadalte).

Programet e verifikojne vete orarin: çdo jave ka 32 golfiste unike dhe asnje çift nuk perseritet (`Verifikimi: OK`).

Ekzekutimi:
- Java: `java golfers/Golfers.java [javet] [sekonda=30] [nyje_per_restart=3000000] [--generic | --known]`
- Default: 9 jave (strukturuar), 7 jave me `--generic`, `--known` = zgjidhja e njohur me 10 jave (vetem shfaqje dhe verifikim).
