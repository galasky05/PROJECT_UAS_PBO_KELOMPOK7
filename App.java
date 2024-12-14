import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Konstanta untuk dimensi game board
        final int TILE_SIZE = 32;
        final int ROWS = 16;
        final int COLUMNS = 16;
        final int BOARD_WIDTH = TILE_SIZE * COLUMNS;
        final int BOARD_HEIGHT = TILE_SIZE * ROWS;

        // Membuat JFrame sebagai container utama
        JFrame frame = new JFrame("Space Invaders");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Menambahkan panel game SpaceInvaders ke JFrame
        SpaceInvaders spaceInvaders = new SpaceInvaders();
        frame.add(spaceInvaders);
        frame.pack(); // Menyesuaikan ukuran frame ke preferred size panel

        // Menampilkan frame
        frame.setVisible(true);

        // Meminta fokus ke panel game agar key listener aktif
        spaceInvaders.requestFocusInWindow();
    }
}
