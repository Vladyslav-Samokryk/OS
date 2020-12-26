/* It is in this file, specifically the replacePage function that will
   be called by MemoryManagement when there is a page fault.  The
   users of this program should rewrite PageFault to implement the
   page replacement algorithm.
*/

// This PageFault file is an example of the FIFO Page Replacement
// Algorithm as described in the Memory Management section.

import java.util.*;

public class PageFault {


    /**
     * The page replacement algorithm for the memory management sumulator.
     * This method gets called whenever a page needs to be replaced.
     * <p>
     * The page replacement algorithm included with the simulator is
     * FIFO (first-in first-out).  A while or for loop should be used
     * to search through the current memory contents for a canidate
     * replacement page.  In the case of FIFO the while loop is used
     * to find the proper page while making sure that virtPageNum is
     * not exceeded.
     * <pre>
     *   Page page = ( Page ) mem.elementAt( oldestPage )
     * </pre>
     * This line brings the contents of the Page at oldestPage (a
     * specified integer) from the mem vector into the page object.
     * Next recall the contents of the target page, replacePageNum.
     * Set the physical memory address of the page to be added equal
     * to the page to be removed.
     * <pre>
     *   controlPanel.removePhysicalPage( oldestPage )
     * </pre>
     * Once a page is removed from memory it must also be reflected
     * graphically.  This line does so by removing the physical page
     * at the oldestPage value.  The page which will be added into
     * memory must also be displayed through the addPhysicalPage
     * function call.  One must also remember to reset the values of
     * the page which has just been removed from memory.
     *
     * @param mem is the vector which contains the contents of the pages
     *   in memory being simulated.  mem should be searched to find the
     *   proper page to remove, and modified to reflect any changes.
     * @param virtPageNum is the number of virtual pages in the
     *   simulator (set in Kernel.java).
     * @param newPageNum is the requested page which caused the
     *   page fault.
     * @param controlPanel represents the graphical element of the
     *   simulator, and allows one to modify the current display.
     */
    public static void replacePage ( Vector mem , int virtPageNum , int newPageNum , ControlPanel controlPanel )
    {
        int priority1 = -1;
        int priority2 = -1;
        int priority3 = -1;
        int priority4 = -1;
        int countdown = 4;
        for (int i = 0; i < virtPageNum; i++) {
            Page page = ( Page ) mem.elementAt( i );
            if (page.physical == -1) continue;
            if (page.R == 0) {
                if (page.M == 0) {
                    if (priority4 == -1) {
                        priority4 = i;
                        countdown--;
                    }
                } else {
                    if (priority3 == -1) {
                        priority3 = i;
                        countdown--;
                    }
                }
            } else {
                if (page.M == 0) {
                    if (priority2 == -1) {
                        priority2 = i;
                        countdown--;
                    }
                } else {
                    if (priority1 == -1) {
                        priority1 = i;
                        countdown--;
                    }
                }
            }
            if (countdown == 0) break;
        }
        int oldPageNum = -1;

        if (priority4 != -1) {
            oldPageNum = priority4;
        } else if (priority3 != -1) {
            oldPageNum = priority3;
        } else if (priority2 != -1) {
            oldPageNum = priority2;
        } else if (priority1 != -1) {
            oldPageNum = priority1;
        }

        Page page = ( Page ) mem.elementAt( oldPageNum );
        Page nextpage = ( Page ) mem.elementAt( newPageNum );
        controlPanel.removePhysicalPage( oldPageNum );
        nextpage.physical = page.physical;
        controlPanel.addPhysicalPage( nextpage.physical , newPageNum );
        page.inMemTime = 0;
        page.lastTouchTime = 0;
        page.R = 0;
        page.M = 0;
        page.physical = -1;
    }
}
