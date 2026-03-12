import java.util.ArrayList;
import java.util.List;
import java.util.UUID;9

/**
 * ═══════════════════════════════════════════════════════
 * CAPSULE.JAVA — Data Structures & Algorithms Core
 * Digital Time Capsule Translation
 * ═══════════════════════════════════════════════════════
 */
public class DigitalTimeCapsule {

    /* ─────────────────────────────────────────────────────
       THE DATA MODEL
    ───────────────────────────────────────────────────── */
    public static class Capsule {
        private String id;
        private String title;
        private String message;
        private long unlockMs;
        private long createdAtMs;
        private boolean isOpen;

        public Capsule(String title, String message, long unlockMs) {
            this.id = "cap_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 5);
            this.title = title;
            this.message = message;
            this.unlockMs = unlockMs;
            this.createdAtMs = System.currentTimeMillis();
            this.isOpen = false;
        }

        // Getters and Setters
        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public long getUnlockMs() { return unlockMs; }
        public boolean isOpen() { return isOpen; }
        public void setOpen(boolean open) { isOpen = open; }

        @Override
        public String toString() {
            return "Capsule{id='" + id + "', title='" + title + "', isOpen=" + isOpen + "}";
        }
    }

    /* ═══════════════════════════════════════════════════
       DATA STRUCTURE 1: ARRAY (List)
       Purpose: Primary storage for all capsule objects.
       Complexity: O(1) append, O(n) search/delete
    ═══════════════════════════════════════════════════ */
    public static class CapsuleArray {
        private List<Capsule> data;

        public CapsuleArray() {
            this.data = new ArrayList<>();
        }

        public void push(Capsule capsule) { data.add(capsule); }

        public Capsule removeAt(int index) {
            if (index < 0 || index >= data.size()) return null;
            return data.remove(index);
        }

        public List<Capsule> getAll() { return data; }
        public int size() { return data.size(); }

        public boolean update(String id, boolean isOpen) {
            for (Capsule cap : data) {
                if (cap.getId().equals(id)) {
                    cap.setOpen(isOpen);
                    return true;
                }
            }
            return false;
        }

        public Capsule findById(String id) {
            for (Capsule cap : data) {
                if (cap.getId().equals(id)) return cap;
            }
            return null;
        }
    }

    /* ═══════════════════════════════════════════════════
       DATA STRUCTURE 2: STACK (LIFO)
       Purpose: Undo the last capsule creation.
       Complexity: O(1) push/pop
    ═══════════════════════════════════════════════════ */
    public static class UndoStack {
        private List<String> items; // Backed by an ArrayList for stack operations

        public UndoStack() {
            this.items = new ArrayList<>();
        }

        public void push(String capsuleId) { items.add(capsuleId); }

        public String pop() {
            if (isEmpty()) return null;
            return items.remove(items.size() - 1);
        }

        public String peek() {
            if (isEmpty()) return null;
            return items.get(items.size() - 1);
        }

        public boolean isEmpty() { return items.isEmpty(); }
        public int size() { return items.size(); }
        public List<String> getAll() { return new ArrayList<>(items); }
    }

    /* ═══════════════════════════════════════════════════
       DATA STRUCTURE 3: QUEUE (FIFO)
       Purpose: Manage capsule opening order chronologically.
       Complexity: O(1) enqueue, O(n) dequeue (array-backed)
    ═══════════════════════════════════════════════════ */
    public static class OpeningQueue {
        private List<String> items;

        public OpeningQueue() {
            this.items = new ArrayList<>();
        }

        public void enqueue(String capsuleId) {
            if (!items.contains(capsuleId)) {
                items.add(capsuleId);
            }
        }

        public String dequeue() {
            if (isEmpty()) return null;
            return items.remove(0); // Removes from the front (O(n) for ArrayLists)
        }

        public String front() {
            if (isEmpty()) return null;
            return items.get(0);
        }

        public boolean isEmpty() { return items.isEmpty(); }
        public int size() { return items.size(); }
        public List<String> getAll() { return new ArrayList<>(items); }
    }

    /* ═══════════════════════════════════════════════════
       ALGORITHMS: LINEAR SEARCH & BUBBLE SORT
    ═══════════════════════════════════════════════════ */
    public static class CapsuleAlgorithms {
        
        // ALGORITHM 1: Linear Search O(n)
        public static List<Capsule> linearSearch(List<Capsule> capsules, String query) {
            if (query == null || query.trim().isEmpty()) return new ArrayList<>(capsules);

            String q = query.toLowerCase().trim();
            List<Capsule> results = new ArrayList<>();

            for (Capsule cap : capsules) {
                boolean inTitle = cap.getTitle().toLowerCase().contains(q);
                boolean inMessage = cap.getMessage().toLowerCase().contains(q);
                
                if (inTitle || inMessage) {
                    results.add(cap);
                }
            }
            return results;
        }

        // ALGORITHM 2: Bubble Sort O(n²)
        public static List<Capsule> bubbleSort(List<Capsule> capsules, boolean ascending) {
            List<Capsule> arr = new ArrayList<>(capsules);
            int n = arr.size();

            for (int i = 0; i < n - 1; i++) {
                boolean swapped = false;

                for (int j = 0; j < n - 1 - i; j++) {
                    long dateA = arr.get(j).getUnlockMs();
                    long dateB = arr.get(j + 1).getUnlockMs();

                    boolean shouldSwap = ascending ? dateA > dateB : dateA < dateB;

                    if (shouldSwap) {
                        // SWAP
                        Capsule temp = arr.get(j);
                        arr.set(j, arr.get(j + 1));
                        arr.set(j + 1, temp);
                        swapped = true;
                    }
                }
                // Optimization: stop if array is already sorted
                if (!swapped) break;
            }
            return arr;
        }
    }

    /* ─────────────────────────────────────────────────────
       CAPSULE MANAGER
       Orchestrates the DS & Algos
    ───────────────────────────────────────────────────── */
    public static class CapsuleManager {
        private CapsuleArray capsuleArray;
        private UndoStack undoStack;
        private OpeningQueue openingQueue;

        public CapsuleManager() {
            this.capsuleArray = new CapsuleArray();
            this.undoStack = new UndoStack();
            this.openingQueue = new OpeningQueue();
        }

        public Capsule create(String title, String message, long unlockMs) {
            Capsule capsule = new Capsule(title, message, unlockMs);
            capsuleArray.push(capsule);
            undoStack.push(capsule.getId());
            return capsule;
        }

        public Capsule undoLast() {
            String lastId = undoStack.pop();
            if (lastId == null) return null;

            List<Capsule> allCapsules = capsuleArray.getAll();
            for (int i = 0; i < allCapsules.size(); i++) {
                if (allCapsules.get(i).getId().equals(lastId)) {
                    return capsuleArray.removeAt(i);
                }
            }
            return null;
        }

        public boolean delete(String id) {
            List<Capsule> all = capsuleArray.getAll();
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getId().equals(id)) {
                    capsuleArray.removeAt(i);
                    return true;
                }
            }
            return false;
        }

        private void refreshQueue() {
            long now = System.currentTimeMillis();
            List<Capsule> sorted = CapsuleAlgorithms.bubbleSort(capsuleArray.getAll(), true);

            for (Capsule cap : sorted) {
                if (!cap.isOpen() && cap.getUnlockMs() <= now) {
                    openingQueue.enqueue(cap.getId());
                }
            }
        }

        public List<String> processQueue() {
            refreshQueue();
            List<String> unlocked = new ArrayList<>();

            while (!openingQueue.isEmpty()) {
                String id = openingQueue.dequeue();
                Capsule cap = capsuleArray.findById(id);

                if (cap != null && !cap.isOpen()) {
                    capsuleArray.update(id, true);
                    unlocked.add(id);
                }
            }
            return unlocked;
        }

        public List<Capsule> search(String query) {
            return CapsuleAlgorithms.linearSearch(capsuleArray.getAll(), query);
        }

        public List<Capsule> getSorted(boolean ascending) {
            return CapsuleAlgorithms.bubbleSort(capsuleArray.getAll(), ascending);
        }
        
        // Getters
        public List<Capsule> getAll() { return capsuleArray.getAll(); }
        public int getCount() { return capsuleArray.size(); }
    }

    /* ─────────────────────────────────────────────────────
       MAIN METHOD (Testing the implementation)
    ───────────────────────────────────────────────────── */
    public static void main(String[] args) {
        CapsuleManager manager = new CapsuleManager();

        System.out.println("Creating capsules...");
        long now = System.currentTimeMillis();
        manager.create("Past Due", "This should open instantly.", now - 10000);
        manager.create("Future Capsule", "Opens in 10 mins", now + 600000);
        manager.create("Mistake", "I will undo this.", now + 900000);

        System.out.println("Total capsules: " + manager.getCount());

        System.out.println("Undoing last capsule...");
        manager.undoLast();
        System.out.println("Total capsules after undo: " + manager.getCount());

        System.out.println("\nProcessing Opening Queue...");
        List<String> unlocked = manager.processQueue();
        System.out.println("Unlocked IDs: " + unlocked);

        System.out.println("\nSearching for 'Future':");
        List<Capsule> searchResults = manager.search("Future");
        for (Capsule cap : searchResults) {
            System.out.println("Found: " + cap.getTitle());
        }
    }
}
