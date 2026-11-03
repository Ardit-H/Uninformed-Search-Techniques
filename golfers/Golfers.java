import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Social Golfers Problem me DFS + backtracking.
 *
 * Problemi: 32 golfiste luajne çdo jave ne 8 grupe me nga 4. Sa jave mund te luajne
 * ashtu qe asnje çift golfistesh te mos jete ne te njejtin grup me shume se nje here?
 *
 * Si funksionon kerkimi (DFS):
 *  - Golfistet vendosen nje nga nje: grup pas grupi, jave pas jave, ne rendin 0, 1, 2, ...
 *  - Kur nje grup mbushet (4 golfiste), shenojme qe ata 6 çifte jane takuar.
 *  - Kur nje jave mbushet (8 grupe), fillojme javen tjeter.
 *  - Backtracking: nese per nje grup nuk ka asnje kandidat te vlefshem, kthehemi mbrapa
 *    dhe ndryshojme vendimin e meparshem.
 *  - Kerkimi vazhdon derisa arrin numrin e kerkuar te javeve ose mbaron koha, dhe ruan orarin me me shume jave.
 *
 * Mënyrat (te gjitha DFS me backtracking, pervec --known):
 *
 * 0) DEFAULT - DFS i paster. Rend fiks i golfisteve, pa rastesi, pa restart. Gjen 5 jave dhe ngec aty,
 *    sepse vendimet e hershme e bejne te pamundur javen tjeter.
 *
 * 1) --restart - DFS me renditje te rastesishme te golfisteve per çdo jave dhe restart pas nje numri nyjesh.
 *    Gjen 7 jave. Nuk eshte me DFS i paster, sepse kerkimi nuk eshte nje kalim i vetem.
 *
 * 2) --structured - zvogelim i domenit. Variabla = java; domeni i çdo jave = nje nga 155 ndarjet "e strukturuara"
 *    te 32 golfisteve ne 8 grupe: golfistet numerohen 0..31 si vektore 5-bitesh dhe grupet jane "cosets" te nje
 *    nenhapesire 2-dimensionale te GF(2)^5. DFS zgjedh javet nje nga nje (ne rend rritës, se javet s'kane rend)
 *    dhe i pret ato qe perseritin ndonje çift. Gjen 9 jave. Kjo NUK eshte kerkim ne te gjitha ndarjet e mundshme.
 *
 * 3) --known - nuk eshte kerkim. Shfaq dhe verifikon zgjidhjen e njohur me 10 jave (Aguado, 2004;
 *    burimi: https://www.metalevel.at/sgp/aguado.txt). 10 jave ESHTE e mundur (11 nuk eshte).
 */
public class Golfers {
    static final int PLAYERS = 32;
    static final int GROUP_SIZE = 4;
    static final int GROUPS_PER_WEEK = PLAYERS / GROUP_SIZE;   // 8
    static final int MAX_WEEKS = 11;                           // kufiri teorik eshte 10 jave: floor(31 / 3)

    // --- gjendja e kerkimit ---
    static final int[][][] schedule = new int[MAX_WEEKS][GROUPS_PER_WEEK][GROUP_SIZE]; // golfistet 0..31
    static final long[] hasMet = new long[PLAYERS];  // hasMet[p] = bitmaske e golfisteve me te cilet p ka luajtur
    static final int[][] playerOrder = new int[MAX_WEEKS][PLAYERS]; // renditja e golfisteve per çdo jave

    // --- parametra dhe statistika ---
    // Zgjidhja e njohur me 10 jave (Alejandro Aguado, 2004). Javet ndahen me |, grupet me ;, golfistet me , (0..31).
    static final String KNOWN_TEN_WEEKS =
            "0,1,2,3;4,5,22,23;6,7,20,21;8,25,26,27;9,10,11,24;12,13,15,30;14,28,29,31;16,17,18,19|0,4,8,28;1,6,18,23;2,7,17,22;3,5,26,31;9,13,14,27;10,15,19,21;11,25,29,30;12,16,20,24|0,11,14,21;1,7,10,28;2,15,20,25;3,13,22,24;4,9,18,31;5,16,27,30;6,8,19,29;12,17,23,26|0,18,24,27;1,9,19,26;2,8,11,16;3,10,17,25;4,7,12,29;5,6,14,15;13,20,23,28;21,22,30,31|0,6,13,26;1,4,11,15;2,9,21,28;3,8,14,23;5,12,18,25;7,19,24,30;10,16,22,29;17,20,27,31|0,7,25,31;1,5,24,29;2,12,14,19;3,18,28,30;4,6,10,27;8,13,17,21;9,15,16,23;11,20,22,26|0,5,19,20;1,14,22,25;2,23,27,29;3,4,16,21;6,9,17,30;7,11,13,18;8,10,12,31;15,24,26,28|0,15,17,29;1,13,16,31;2,4,26,30;3,6,11,12;5,7,8,9;10,14,18,20;19,22,27,28;21,23,24,25|0,9,12,22;1,8,20,30;2,5,10,13;3,7,15,27;4,14,17,24;6,16,25,28;11,19,23,31;18,21,26,29|0,10,23,30;1,12,21,27;2,6,24,31;3,9,20,29;4,13,19,25;5,11,17,28;7,14,16,26;8,15,18,22";

    static int targetWeeks;
    static long deadlineNanos;
    static long visitedNodes;
    static long nodesInThisRestart;
    static long restartNodeLimit = 3_000_000;
    static boolean timeIsUp;
    static boolean restartRequested;

    // --- rezultati me i mire deri tani ---
    static int[][][] bestSchedule = new int[0][][];
    static int bestWeekCount = 0;

    /** Numeron nyjen dhe kontrollon a duhet te ndalojme (koha mbaroi ose duhet restart). */
    static boolean shouldStop() {
        visitedNodes++;
        nodesInThisRestart++;
        if (nodesInThisRestart > restartNodeLimit) restartRequested = true;
        if ((visitedNodes & 0xFFFF) == 0 && System.nanoTime() > deadlineNanos) timeIsUp = true;
        return timeIsUp || restartRequested;
    }

    /** Ruan kopjen e orarit nese ka me shume jave se me e mira deri tani. */
    static void saveIfBest(int completedWeeks) {
        if (completedWeeks <= bestWeekCount) return;
        bestWeekCount = completedWeeks;
        bestSchedule = new int[completedWeeks][GROUPS_PER_WEEK][GROUP_SIZE];
        for (int w = 0; w < completedWeeks; w++)
            for (int g = 0; g < GROUPS_PER_WEEK; g++)
                bestSchedule[w][g] = schedule[w][g].clone();
    }

    /** Golfisti i pare i papercaktuar sipas renditjes se javes (ai do te jete anetari i pare i grupit te ri). */
    static int firstUnassignedPlayer(int week, long unassigned) {
        for (int position = 0; position < PLAYERS; position++) {
            int player = playerOrder[week][position];
            if ((unassigned & (1L << player)) != 0) return player;
        }
        throw new IllegalStateException("Nuk ka golfist te papercaktuar");
    }

    /** Shenon (ose ç'shenon) qe te gjithe golfistet e grupit jane takuar me njeri-tjetrin. */
    static void markGroupMet(long groupMask, boolean met) {
        for (long m = groupMask; m != 0; m &= m - 1) {
            int player = Long.numberOfTrailingZeros(m);
            long others = groupMask & ~(1L << player);
            if (met) hasMet[player] |= others;
            else hasMet[player] &= ~others;
        }
    }

    /**
     * DFS: vendos golfistet e grupit "group" te javes "week".
     *
     * @param seat       sa golfiste jane tashme ne grupin aktual (0 = grup i ri)
     * @param lastPos    pozicioni (ne renditjen e javes) i golfistit te fundit te shtuar ne grup;
     *                   golfistet e tjere shtohen vetem me pozicion me te madh, qe te mos
     *                   provojme te njejtin grup ne rende te ndryshme
     * @param unassigned bitmaske e golfisteve qe s'kane grup akoma kete jave
     * @param groupMask  bitmaske e golfisteve te grupit aktual
     * @return true nese u arrit numri i kerkuar i javeve
     */
    static boolean placePlayers(int week, int group, int seat, int lastPos, long unassigned, long groupMask) {
        if (shouldStop()) return false;

        if (group == GROUPS_PER_WEEK) {                       // java u kompletua
            saveIfBest(week + 1);
            if (week + 1 == targetWeeks) return true;
            return placePlayers(week + 1, 0, 0, -1, (1L << PLAYERS) - 1, 0L);
        }

        if (seat == 0) {                                      // fillon grup i ri
            int first = firstUnassignedPlayer(week, unassigned);
            schedule[week][group][0] = first;
            return placePlayers(week, group, 1, positionOf(week, first), unassigned & ~(1L << first), 1L << first);
        }

        // kandidatet: te lire dhe te pa-takuar me asnje anetar te grupit aktual
        for (int pos = lastPos + 1; pos < PLAYERS; pos++) {
            int candidate = playerOrder[week][pos];
            long bit = 1L << candidate;
            if ((unassigned & bit) == 0) continue;            // ka grup tashme kete jave
            if ((hasMet[candidate] & groupMask) != 0) continue; // ka luajtur me dike nga grupi
            schedule[week][group][seat] = candidate;
            long newGroupMask = groupMask | bit;
            long newUnassigned = unassigned & ~bit;

            boolean reachedTarget;
            if (seat == GROUP_SIZE - 1) {                     // grupi u mbush
                markGroupMet(newGroupMask, true);
                reachedTarget = placePlayers(week, group + 1, 0, -1, newUnassigned, 0L);
                if (reachedTarget) return true;
                markGroupMet(newGroupMask, false);            // backtracking: ç'shenojme takimet
            } else {
                reachedTarget = placePlayers(week, group, seat + 1, pos, newUnassigned, newGroupMask);
                if (reachedTarget) return true;
            }
            if (timeIsUp || restartRequested) return false;
        }
        return false;                                         // asnje kandidat: backtracking
    }

    static int positionOf(int week, int player) {
        for (int pos = 0; pos < PLAYERS; pos++)
            if (playerOrder[week][pos] == player) return pos;
        throw new IllegalStateException();
    }

    /** Kontroll i pavarur: çdo jave ka secili golfist saktesisht nje here, asnje çift s'perseritet. */
    static boolean isValidSchedule(int[][][] weeks) {
        boolean[][] pairUsed = new boolean[PLAYERS][PLAYERS];
        for (int[][] week : weeks) {
            boolean[] seen = new boolean[PLAYERS];
            for (int[] group : week) {
                for (int a = 0; a < group.length; a++) {
                    if (seen[group[a]]) return false;
                    seen[group[a]] = true;
                    for (int b = a + 1; b < group.length; b++) {
                        int low = Math.min(group[a], group[b]), high = Math.max(group[a], group[b]);
                        if (pairUsed[low][high]) return false;
                        pairUsed[low][high] = true;
                    }
                }
            }
            for (boolean s : seen) if (!s) return false;
        }
        return true;
    }

    /** Pergatit renditje te reja per javet (java 1 ruan renditjen 1..32 ne restart-in e pare). */
    static void shufflePlayerOrders(Random random, boolean keepFirstWeekSorted) {
        for (int week = 0; week < MAX_WEEKS; week++) {
            List<Integer> players = new ArrayList<>();
            for (int p = 0; p < PLAYERS; p++) players.add(p);
            if (!(week == 0 && keepFirstWeekSorted)) Collections.shuffle(players, random);
            for (int pos = 0; pos < PLAYERS; pos++) playerOrder[week][pos] = players.get(pos);
        }
    }

    // ======================================================================
    // MËNYRA E STRUKTURUAR (--structured): DFS mbi 155 ndarjet e mundshme te javes
    // ======================================================================

    static int[][][] candidateWeeks;      // candidateWeeks[i] = java i: 8 grupe me nga 4 golfiste
    static boolean[][] weeksCompatible;   // weeksCompatible[i][j] = javet i dhe j s'kane asnje çift te perbashket
    static int[] chosenWeeks = new int[MAX_WEEKS];

    /** Ndertimi i domenit: çdo nenhapesire H = {0, a, b, a^b} e GF(2)^5 jep nje jave (grupet = cosets te H). */
    static void buildCandidateWeeks() {
        java.util.Set<Long> seen = new java.util.HashSet<>();
        List<int[][]> weeks = new ArrayList<>();
        for (int a = 1; a < PLAYERS; a++) {
            for (int b = a + 1; b < PLAYERS; b++) {
                long key = (1L << 0) | (1L << a) | (1L << b) | (1L << (a ^ b)); // nenhapesira si bitmaske
                if (!seen.add(key)) continue;
                int[] subspace = {0, a, b, a ^ b};
                boolean[] grouped = new boolean[PLAYERS];
                int[][] week = new int[GROUPS_PER_WEEK][];
                int groupCount = 0;
                for (int x = 0; x < PLAYERS; x++) {
                    if (grouped[x]) continue;
                    int[] group = new int[GROUP_SIZE];
                    for (int i = 0; i < GROUP_SIZE; i++) {
                        group[i] = x ^ subspace[i];
                        grouped[group[i]] = true;
                    }
                    Arrays.sort(group);
                    week[groupCount++] = group;
                }
                weeks.add(week);
            }
        }
        candidateWeeks = weeks.toArray(new int[0][][]);
        int count = candidateWeeks.length;
        weeksCompatible = new boolean[count][count];
        for (int i = 0; i < count; i++)
            for (int j = 0; j < count; j++)
                weeksCompatible[i][j] = i != j && !shareAPair(candidateWeeks[i], candidateWeeks[j]);
    }

    /** A kane dy javet te pakten nje çift golfistesh qe luajne bashke ne te dyja? */
    static boolean shareAPair(int[][] weekA, int[][] weekB) {
        boolean[][] together = new boolean[PLAYERS][PLAYERS];
        for (int[] group : weekA)
            for (int x : group) for (int y : group) together[x][y] = true;
        for (int[] group : weekB)
            for (int i = 0; i < GROUP_SIZE; i++)
                for (int j = i + 1; j < GROUP_SIZE; j++)
                    if (together[group[i]][group[j]]) return true;
        return false;
    }

    /**
     * DFS: zgjedh javen e radhes nga kandidatet me indeks >= firstCandidate.
     * Backtracking: nese java perseritet ndonje çift me nje jave te zgjedhur, kalohet te tjetra;
     * nese s'ka kandidat, kthehemi mbrapa dhe ndryshojme javen e meparshme.
     */
    static boolean chooseWeeks(int chosenCount, int firstCandidate) {
        if (shouldStop()) return false;
        if (chosenCount > bestWeekCount) {
            bestWeekCount = chosenCount;
            bestSchedule = new int[chosenCount][][];
            for (int w = 0; w < chosenCount; w++) bestSchedule[w] = candidateWeeks[chosenWeeks[w]];
        }
        if (chosenCount == targetWeeks) return true;
        int weeksStillNeeded = targetWeeks - chosenCount;
        for (int index = firstCandidate; index < candidateWeeks.length; index++) {
            if (candidateWeeks.length - index < weeksStillNeeded) return false; // nuk kane mbetur kandidate
            boolean compatible = true;
            for (int w = 0; w < chosenCount && compatible; w++)
                compatible = weeksCompatible[chosenWeeks[w]][index];
            if (!compatible) continue;
            chosenWeeks[chosenCount] = index;
            if (chooseWeeks(chosenCount + 1, index + 1)) return true;
            if (timeIsUp) return false;                   // backtracking: provojme kandidatin tjeter
        }
        return false;
    }

    /** Shfaq dhe verifikon zgjidhjen e njohur me 10 jave (nuk eshte kerkim). */
    static void showKnownSolution() {
        String[] weekTexts = KNOWN_TEN_WEEKS.split("[|]");
        int[][][] weeks = new int[weekTexts.length][GROUPS_PER_WEEK][GROUP_SIZE];
        for (int w = 0; w < weekTexts.length; w++) {
            String[] groupTexts = weekTexts[w].split(";");
            for (int g = 0; g < groupTexts.length; g++) {
                String[] players = groupTexts[g].split(",");
                for (int i = 0; i < players.length; i++) weeks[w][g][i] = Integer.parseInt(players[i]);
            }
        }
        for (int w = 0; w < weeks.length; w++) {
            StringBuilder line = new StringBuilder(String.format("Java %2d:", w + 1));
            for (int[] group : weeks[w]) {
                line.append(" (");
                for (int i = 0; i < group.length; i++)
                    line.append(i > 0 ? " " : "").append(String.format("%2d", group[i] + 1));
                line.append(")");
            }
            System.out.println(line);
        }
        System.out.println();
        System.out.println("Zgjidhje e njohur (Aguado, 2004), jo e gjetur nga kerkimi yne: " + weeks.length + " jave.");
        System.out.println("Verifikimi: " + (isValidSchedule(weeks) ? "OK" : "GABIM"));
    }

    /** Rend fiks 0, 1, 2, ... 31 per te gjitha javet (DFS i paster). */
    static void setIdentityOrders() {
        for (int week = 0; week < MAX_WEEKS; week++)
            for (int p = 0; p < PLAYERS; p++) playerOrder[week][p] = p;
    }

    public static void main(String[] args) {
        // Perdorimi: java Golfers [javet] [sekonda=30] [nyje_per_restart=3000000] [--restart | --structured | --known]
        //            default: DFS i paster, 5 jave; --restart: 7 jave; --structured: 9 jave
        boolean restartMode = Arrays.asList(args).contains("--restart");
        boolean structured = Arrays.asList(args).contains("--structured");
        if (Arrays.asList(args).contains("--known")) {
            showKnownSolution();
            return;
        }
        List<String> numbers = new ArrayList<>();
        for (String arg : args) if (!arg.startsWith("--")) numbers.add(arg);
        targetWeeks = numbers.size() > 0 ? Integer.parseInt(numbers.get(0)) : (structured ? 9 : restartMode ? 7 : 5);
        int seconds = numbers.size() > 1 ? Integer.parseInt(numbers.get(1)) : 30;
        if (numbers.size() > 2) restartNodeLimit = Long.parseLong(numbers.get(2));
        if (targetWeeks < 1 || targetWeeks > MAX_WEEKS) {
            System.out.println("Javet e kerkuara duhet te jene ne [1, " + MAX_WEEKS + "].");
            return;
        }

        long startTime = System.nanoTime();
        deadlineNanos = startTime + seconds * 1_000_000_000L;
        boolean found = false;
        int restarts = 0;
        if (structured) {
            restartNodeLimit = Long.MAX_VALUE;   // mënyra e strukturuar nuk perdor restart
            buildCandidateWeeks();
            found = chooseWeeks(0, 0);
        } else if (restartMode) {
            Random random = new Random(42);
            while (!found && !timeIsUp) {
                shufflePlayerOrders(random, restarts == 0);
                Arrays.fill(hasMet, 0L);
                nodesInThisRestart = 0;
                restartRequested = false;
                restarts++;
                found = placePlayers(0, 0, 0, -1, (1L << PLAYERS) - 1, 0L);
                if (!restartRequested && !timeIsUp) break;   // hapesira u kerkua e gjithe pa rezultat
            }
        } else {
            restartNodeLimit = Long.MAX_VALUE;   // DFS i paster: pa restart, rend fiks
            setIdentityOrders();
            found = placePlayers(0, 0, 0, -1, (1L << PLAYERS) - 1, 0L);
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        for (int w = 0; w < bestWeekCount; w++) {
            StringBuilder line = new StringBuilder(String.format("Java %2d:", w + 1));
            for (int[] group : bestSchedule[w]) {
                line.append(" (");
                for (int i = 0; i < group.length; i++)
                    line.append(i > 0 ? " " : "").append(String.format("%2d", group[i] + 1));
                line.append(")");
            }
            System.out.println(line);
        }
        System.out.println();
        if (found) System.out.println("U gjet orar per " + targetWeeks + " jave.");
        else if (timeIsUp) System.out.println("Koha mbaroi. Orari me i mire: " + bestWeekCount + " jave (kerkuar: " + targetWeeks + ").");
        else System.out.println("Hapesira u kerkua plotesisht. Maksimumi i gjetur: " + bestWeekCount + " jave.");
        System.out.println("Verifikimi: " + (isValidSchedule(bestSchedule) ? "OK" : "GABIM"));
        System.out.println("Nyje te vizituara: " + visitedNodes + ", restart: " + restarts + ", koha: " + elapsedMs + " ms");
    }
}