import java.util.*;

class Solution {

    public static void main(String []args){

        Solution sol = new Solution();

        //------------
        int     N           = 4;
        String  artifacts   = "1B 2C,2D 4D";
        String  searched    = "2B 2D 3D 4D 4A";
        int[]   answer      = sol.solution (N, artifacts, searched);

        sol.showSolution (N, artifacts, searched, answer); // answer should be {1, 1}

        //------------
        N           = 3;
        artifacts   = "1A 1B,2C 2C";
        searched    = "1B";
        answer      = sol.solution (N, artifacts, searched); // Answer should be {0, 1}

        sol.showSolution (N, artifacts, searched, answer);

    } // main

    //-------------------------------------------------------------------------
    private void showSolution (int N, String artifacts, String searched, int[] answer)
    {
        System.out.println();
        System.out.println ("N         = " + N);
        System.out.println ("Artifacts = " + artifacts);
        System.out.println ("Searched  = " + searched);
        System.out.println ("Answer: Total = " + answer [0] + ", Partial = " + answer [1]);
        System.out.println ("\n-----------------");

    } // showSolution

    //-------------------------------------------------------------------------
    public int[] solution(int N, String artifacts, String searched) {
        // write your code in Java

        String[] arts = artifacts.split(",");
        int totalArtifacts = arts.length;

        // Given N = 4 and artifacts = {"1B 2C", "2D 4D"} this method returns
        // all cells of the artifact: {{"1B", "1C", "2B", "2C"}, {"2D", "3D", "4D"}}
        String[][] artCells = getArtifactCells(arts);

        //--------
        // Map each cell of an artifact to its artifact ID
        // example: "1B" -> 0, "1C" -> 0, "2B" -> 0, "2C" -> 0,
        // "2D" -> 1, "3D" -> 1, "4D" -> 1
        Map<String, Integer> artCellMap = makeArtifactCellMap(artCells);

        //--------
        // Make a list with the counts of the number of tiles in each
        // artifact. The list index is the artifact ID.
        // In the above example, artifact 1 has 4 tiles, while artifact #2 has 3 tiles.
        // The size of the array is 'totalArtifacts'
        int[] artTileCounts = makeArtifactTileCounts(artCells);
        showTileCounts("Artifact Tile Counts", artTileCounts);

        //--------
        int[] searchedCounts = new int[totalArtifacts];

        // Searched is of the form "2B 2D 3D 4D 4A"
        String[] searchedTiles = searched.split(" ");
        for (String searchedTile : searchedTiles) {
            Integer artifactId = artCellMap.get(searchedTile);

            // If we searched a cell of an existing artifact, increment
            // that artifact's count
            if (artifactId != null) {
                searchedCounts [artifactId]++;
            }
        }
        showTileCounts("Searched Tile Counts", searchedCounts);

        return getTotalPartialCounts (artTileCounts, searchedCounts);

    } // solution

    //---------------------------------------------------------------
    private int[] getTotalPartialCounts (int[] artTileCounts, int[] searchedCounts)
    {
        int totalCount = 0, partialCount = 0;

        // Compare the artifact tile counts with the searched counts
        // to find out the total & partial searches
        for (int i = 0; i < searchedCounts.length; i++)
        {
            int sc = searchedCounts [i];
            if (sc == 0) continue;

            if (sc == artTileCounts [i])
                totalCount++;
            else if (sc < artTileCounts [i])
                partialCount++;
        }
        return new int[] {totalCount, partialCount};

    } // getTotalPartialCounts

    //---------------------------------------------------------------
    private void showTileCounts (String msg, int[] tileCounts)
    {
        System.out.print (msg + ": ");
        for (int tileCount: tileCounts)
            System.out.print (tileCount + " ");
        System.out.println();

    } // showTileCounts

    //---------------------------------------------------------------
    // Given N = 4 and artifacts = {"1B 2C", "2D 4D"} this method returns
    // all cells of the artifact: {{"1B", "1C", "2B", "2C"}, {"2D", "3D", "4D"}}

    private String[][] getArtifactCells (String[] arts)
    {
        String[][] allArts = new String [arts.length][];

        int i = 0;
        for (String artifact: arts) {
            allArts [i++] = expandArtifact (artifact);
        }
        return allArts;

    } // getArtifactCells

    //--------------------------------------------------------------
    // Make a list with the counts of the number of tiles in each
    // artifact. The list index is the artifact ID.

    private int[] makeArtifactTileCounts (String[][] artifacts)
    {
        int[] artTileCounts = new int [artifacts.length];

        int i = 0;
        for (String[] artifact: artifacts) {
            artTileCounts [i++] = artifact.length;
        }
        return artTileCounts;

    } // makeArtifactTileCounts

    //------------------------------------------------------------------
    /* Input: All cells of all artifact. Example:
     *    {{"1B", "1C", "2B", "2C"}, {"2D", "3D", "4D"}}
     * Output:
     *    "1B" -> 0, "1C" -> 0, "2B" -> 0, "2C" -> 0,
     *    "2D" -> 1, "3D" -> 1, "4D" -> 1
     */
    private Map makeArtifactCellMap (String[][] artifacts)
    {
        Map <String, Integer> map = new HashMap<>();
        int artifactId = 0;

        for (String[] artifact: artifacts) { // For each artifact

            // Put each artifact cell into map. Value is artifact ID
            for (String artCell: artifact) {
                map.put (artCell, artifactId);
            }
            artifactId++;
        }
        return map;

    } // makeArtifactCellMap

    //-------------------------------------------------------
    /**
     * Given top left and bottom right corners such as
     * 12E and 14G (in the format "12E 14G"), this function
     * returns the location of all cells in the range in an array
     * Output:
     * {"12E", "12F", "12G", "13E", "13F", "13F", "14E", "14F", "14G"}
     */
    private String[] expandArtifact (String extremes)
    {
        String[] ends = extremes.split (" ");
        String first = ends [0]; // e.g. 12E
        String last = ends [1];  // e.g. 14G

        // If top left equals bottom right, there's only one cell
        if (first.equals (last)) return new String[] {first};

        String[] firstP = first.split ("(?<=\\d)(?=\\D)");
        String[] lastP  = last.split ("(?<=\\d)(?=\\D)");

        int row1 = Integer.parseInt (firstP [0]); // e.g. 12
        int row2 = Integer.parseInt (lastP [0]);  // e.g. 14

        char col1 = firstP [1].charAt (0); // e.g. 'E'
        char col2 = lastP [1].charAt (0);  // e.g. 'G'

        //---------
        // Calculate the number of cells in the rectangle
        int cellCount = (row2 - row1 + 1) * (col2 - col1 + 1);
        String[] cellLocs = new String [cellCount];

        int i = 0;
        for (int row = row1; row <= row2; row++) {
            for (char col = col1; col <= col2; col++) {
                cellLocs [i++] = "" + row + col;
            }
        }
        return cellLocs;

    } // expandArtifact

    //-------------------------------------------------------
    private void showCellLocs (String ends, String[] locs)
    {
        System.out.println (ends + " contains the following cells:");
        for (String loc: locs)
            System.out.print (loc + " ");

    } // showCellLocs
}
