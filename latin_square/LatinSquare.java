import java.util.Arrays;
import java.util.Scanner;

/**
 * Latin Square me Iterative Deepening DFS (IDDFS) + backtracking.
 *
 * Çfare eshte nje Latin Square: tabele n x n me vlerat 1..n, ku çdo vlere shfaqet
 * saktesisht nje here ne çdo rresht dhe ne çdo kolone.
 *
 * Si funksionon kerkimi:
 *  - Nje "nyje" e pemes eshte tabela e mbushur pjeserisht. Thellesia e nyjes = sa qeliza jane mbushur
 *    (pa numeruar rreshtin dhe kolonen e pare, qe jane te fiksuara).
 *  - IDDFS ben DFS me kufi thellesie dhe e rrit kufirin: startLimit, startLimit+1, ... deri ne numrin e
 *    qelizave te lira. Ne çdo iteracion DFS nuk lejohet te mbushe me shume qeliza se kufiri.
 *  - Qeliza e radhes zgjidhet dinamikisht: ajo me me pak vlera te lejuara (MRV). Nese ndonje qelize
 *    nuk ka asnje vlere te lejuar, kthehemi mbrapa (backtracking) dhe provojme vlere tjeter.
 *
 * Dy mënyra per kufirin fillestar:
 *  - E zakonshme (default): startLimit = numri i qelizave te lira. Zgjidhja kerkon te gjitha qelizat te mbushura,
 *    ndaj asnje kufi me i vogel nuk mund ta gjeje; ato iteracione do te ishin pune e kote (eksplorim i plote pa
 *    rezultat). Keshtu IDDFS fillon nga thellesia minimale ku mund te ekzistoje zgjidhje.
 *  - --textbook: startLimit = 1, IDDFS i plote nga libri. Eshte shume i ngadalte: praktik vetem per n <= 5.
 *
 * Thyerje simetrie: rreshti i pare dhe kolona e pare fiksohen si 1..n. Çdo Latin Square
 * mund te kthehet ne kete forme duke ndryshuar rendin e rreshtave/kolonave/simboleve.
 *
 * Perdorimi: java LatinSquare [n] [--textbook]
 */
public class LatinSquare {
    static int size;                 // n
    static int[][] square;           // tabela; 0 = qelize e paplotesuar
    static long[] usedInRow;          // usedInRow[r] = bitmaske e vlerave te perdorura ne rreshtin r (bit v = vlera v)
    static long[] usedInColumn;       // usedInColumn[c] = bitmaske e vlerave te perdorura ne kolonen c
    static int remainingCells;       // sa qeliza te lira kane mbetur
    static long visitedNodes;        // numri i nyjeve te vizituara (per statistike)

    /** Pergatit tabelen: rreshti i pare dhe kolona e pare fiksohen 1..n, te tjerat jane te lira. */
    static void resetSquare() {
        square = new int[size][size];
        usedInRow = new long[size];
        usedInColumn = new long[size];
        for (int i = 0; i < size; i++) {
            fix(0, i, i + 1);          // rreshti i pare: 1, 2, ..., n
            if (i > 0) fix(i, 0, i + 1); // kolona e pare:  1, 2, ..., n
        }
        remainingCells = (size - 1) * (size - 1);
    }

    static void fix(int row, int col, int value) {
        square[row][col] = value;
        usedInRow[row] |= 1L << value;
        usedInColumn[col] |= 1L << value;
    }

    /** Bitmaska e vlerave qe lejohen ne qelizen (row, col): jo ne rresht, jo ne kolone. */
    static long allowedValues(int row, int col) {
        long allValues = ((1L << (size + 1)) - 1) & ~1L; // bitet 1..n
        return allValues & ~usedInRow[row] & ~usedInColumn[col];
    }

    /**
     * Kerkim me thellesi te kufizuar (DLS): depth = sa qeliza jane mbushur deri tani.
     * Kthen true nese tabela u mbush plotesisht.
     */
    static boolean depthLimitedSearch(int depth, int depthLimit) {
        visitedNodes++;
        if (remainingCells == 0) return true;      // asnje qelize e lire: zgjidhja u gjet
        if (depth == depthLimit) return false;     // arritem kufirin e thellesise (cutoff)

        // zgjidh qelizen e lire me me pak vlera te lejuara (MRV)
        int chosenRow = -1, chosenCol = -1, fewestOptions = size + 1;
        long chosenMask = 0L;
        for (int row = 1; row < size; row++) {
            for (int col = 1; col < size; col++) {
                if (square[row][col] != 0) continue;
                long mask = allowedValues(row, col);
                int options = Long.bitCount(mask);
                if (options < fewestOptions) {
                    chosenRow = row;
                    chosenCol = col;
                    chosenMask = mask;
                    fewestOptions = options;
                    if (options == 0) return false; // qelize pa mundesi: dega braktiset (backtracking)
                }
            }
        }

        remainingCells--;
        for (long remaining = chosenMask; remaining != 0; remaining &= remaining - 1) {
            long bit = remaining & -remaining;      // vlera e radhes, si bit
            int value = Long.numberOfTrailingZeros(bit);
            square[chosenRow][chosenCol] = value;
            usedInRow[chosenRow] |= bit;
            usedInColumn[chosenCol] |= bit;
            if (depthLimitedSearch(depth + 1, depthLimit)) return true;
            usedInRow[chosenRow] &= ~bit;          // backtracking: kjo vlere nuk coi gjekundi
            usedInColumn[chosenCol] &= ~bit;
            square[chosenRow][chosenCol] = 0;
        }
        remainingCells++;
        return false;
    }

    /** IDDFS: rrit kufirin e thellesise (numri i qelizave te mbushura) derisa gjendet zgjidhja. */
    static boolean iterativeDeepeningSearch(int startLimit) {
        int freeCells = (size - 1) * (size - 1);
        for (int depthLimit = startLimit; depthLimit <= freeCells; depthLimit++) {
            resetSquare();
            if (depthLimitedSearch(0, depthLimit)) return true;
        }
        return false;
    }

    /** Kontroll i pavarur: a eshte tabela Latin Square e vlefshme? */
    static boolean isValidLatinSquare() {
        for (int i = 0; i < size; i++) {
            boolean[] seenInRow = new boolean[size + 1];
            boolean[] seenInColumn = new boolean[size + 1];
            for (int j = 0; j < size; j++) {
                int rowValue = square[i][j], columnValue = square[j][i];
                if (rowValue < 1 || rowValue > size || seenInRow[rowValue]) return false;
                if (columnValue < 1 || columnValue > size || seenInColumn[columnValue]) return false;
                seenInRow[rowValue] = true;
                seenInColumn[columnValue] = true;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        boolean textbook = Arrays.asList(args).contains("--textbook");
        String numberArgument = null;
        for (String arg : args) if (!arg.startsWith("--")) numberArgument = arg;
        if (numberArgument != null) {
            size = Integer.parseInt(numberArgument);
        } else {
            System.out.print("Jepni n: ");
            size = new Scanner(System.in).nextInt();
        }
        if (size < 1 || size > 60) {
            System.out.println("n duhet te jete ne [1, 60].");
            return;
        }

        long startTime = System.nanoTime();
        boolean found = iterativeDeepeningSearch(textbook ? 1 : (size - 1) * (size - 1));
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        if (!found) {
            System.out.println("Nuk u gjet zgjidhje.");
            return;
        }
        int cellWidth = String.valueOf(size).length() + 1;
        for (int[] row : square) {
            StringBuilder line = new StringBuilder();
            for (int value : row) line.append(String.format("%" + cellWidth + "d", value));
            System.out.println(line);
        }
        System.out.println("Verifikimi: " + (isValidLatinSquare() ? "OK" : "GABIM"));
        System.out.println("Nyje te vizituara: " + visitedNodes + ", koha: " + elapsedMs + " ms");
    }
}
