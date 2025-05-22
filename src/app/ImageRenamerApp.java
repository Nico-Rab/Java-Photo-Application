package app;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class ImageRenamerApp extends JFrame {
    private static final String SOURCE_FOLDER = "images";
    private static final String DEST_FOLDER = "photos";
    private static final String[] IMAGE_EXTS = { "jpg", "jpeg", "png" };
    private static final int CROP_SIZE = 200; // crop size in original pixels

    private File[] files;
    private int index = 0;

    private JTextField colorField = new JTextField(3);
    private JTextField grainField = new JTextField(5);
    private JCheckBox flopCheckbox = new JCheckBox("Flop?");
    private JLabel filenameLabel = new JLabel();
    private ImageCanvas canvas = new ImageCanvas();

    public ImageRenamerApp() {
        super("Image Renamer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        new File(DEST_FOLDER).mkdirs();
        loadFiles();
        initUI();
        loadNext();
    }

    private void loadFiles() {
        File dir = new File(SOURCE_FOLDER);
        dir.mkdirs();
        files = dir.listFiles((d, name) -> {
            String lower = name.toLowerCase();
            return Arrays.stream(IMAGE_EXTS).anyMatch(lower::endsWith) && !name.contains("-");
        });
        if (files == null)
            files = new File[0];
    }

    private void initUI() {
        JPanel control = new JPanel(new FlowLayout(FlowLayout.LEFT));
        control.setBackground(Color.DARK_GRAY);
        JLabel cLabel = new JLabel("Color:");
        cLabel.setForeground(Color.WHITE);
        JLabel gLabel = new JLabel("Grain:");
        gLabel.setForeground(Color.WHITE);
        flopCheckbox.setForeground(Color.WHITE);
        flopCheckbox.setBackground(Color.DARK_GRAY);
        JButton renameBtn = new JButton("Rename & Next");
        JButton deleteBtn = new JButton("Delete");
        filenameLabel.setForeground(Color.WHITE);

        control.add(cLabel);
        control.add(colorField);
        control.add(gLabel);
        control.add(grainField);
        control.add(flopCheckbox);
        control.add(renameBtn);
        control.add(deleteBtn);
        control.add(Box.createHorizontalStrut(20));
        control.add(filenameLabel);

        add(control, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        renameBtn.addActionListener(e -> renameAndNext());
        deleteBtn.addActionListener(e -> deleteFile());

        InputMap im = canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = canvas.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exit");
        am.put("exit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK), "delete");
        am.put("delete", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                deleteFile();
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "flop");
        am.put("flop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                flopCheckbox.setSelected(!flopCheckbox.isSelected());
            }
        });
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "rename");
        am.put("rename", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                renameAndNext();
            }
        });

        pack();
        setVisible(true);
    }

    private void loadNext() {
        if (index >= files.length) {
            JOptionPane.showMessageDialog(this, "All images processed.");
            System.exit(0);
        }
        File f = files[index++];
        filenameLabel.setText(f.getName());
        colorField.setText("");
        grainField.setText("");
        flopCheckbox.setSelected(false);
        canvas.loadImage(f);
    }

    private void deleteFile() {
        File f = canvas.getFile();
        if (f != null && f.delete())
            loadNext();
        else
            JOptionPane.showMessageDialog(this, "Delete failed.");
    }

    private void renameAndNext() {
        String color = colorField.getText().trim().toUpperCase();
        if (color.length() != 3 || !color.matches("[A-Z]{3}")) {
            JOptionPane.showMessageDialog(this, "Color must be 3 letters (e.g. FFA)");
            return;
        }
        String grain = grainField.getText().trim().toUpperCase();
        BufferedImage img = canvas.getImage();
        Rectangle rect = canvas.getCropRect();
        if (img == null || rect == null)
            return;
        BufferedImage crop = img.getSubimage(rect.x, rect.y, rect.width, rect.height);
        String base = color + "-" + grain + (flopCheckbox.isSelected() ? "_flop" : "");
        String outName = base + ".jpg";
        String prefix = color.substring(0, 2);
        File outDir = new File(DEST_FOLDER, prefix);
        outDir.mkdirs();
        File outFile = new File(outDir, outName);
        try {
            ImageIO.write(crop, "JPEG", outFile);
            File src = canvas.getFile();
            src.delete();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage());
        }
        loadNext();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageRenamerApp::new);
    }

    class ImageCanvas extends JPanel {
        private BufferedImage orig;
        private BufferedImage display;
        private File file;
        private double zoom = 1.0;
        private Point offset = new Point(0, 0);
        private Rectangle crop;
        private Point dragStart;

        ImageCanvas() {
            setBackground(Color.BLACK);
            MouseAdapter ma = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (crop != null && crop.contains(e.getPoint())) {
                        dragStart = e.getPoint();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    dragStart = null;
                }

                public void mouseDragged(MouseEvent e) {
                    if (dragStart != null) {
                        int dx = e.getX() - dragStart.x;
                        int dy = e.getY() - dragStart.y;
                        crop.translate(dx, dy);
                        clampCrop();
                        dragStart = e.getPoint();
                        repaint();
                    }
                }

                public void mouseWheelMoved(MouseWheelEvent e) {
                    zoom *= (e.getWheelRotation() < 0 ? 1.2 : 1 / 1.2);
                    zoom = Math.max(0.2, Math.min(5.0, zoom));
                    layoutImage();
                    repaint();
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
            addMouseWheelListener(ma);
        }

        void loadImage(File f) {
            try {
                // Read and rotate image upright
                BufferedImage img = ImageIO.read(f);
                if (img != null) {
                    // Rotate 90° clockwise to correct orientation
                    int w = img.getWidth(), h = img.getHeight();
                    AffineTransform tx = new AffineTransform();
                    tx.translate(h / 2.0, w / 2.0);
                    tx.rotate(Math.toRadians(90));
                    tx.translate(-w / 2.0, -h / 2.0);
                    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
                    BufferedImage rotated = new BufferedImage(h, w, img.getType());
                    op.filter(img, rotated);
                    img = rotated;
                }
                orig = img;
            } catch (IOException ex) {
                orig = null;
            }
            file = f;
            zoom = 1.0;
            offset.setLocation(0, 0);
            layoutImage();
            initCropRect();
            repaint();
        }

        void layoutImage() {
            if (orig == null)
                return;
            int pw = getWidth(), ph = getHeight();
            double scale = Math.min((double) pw / orig.getWidth(), (double) ph / orig.getHeight());
            int w = (int) (orig.getWidth() * scale * zoom);
            int h = (int) (orig.getHeight() * scale * zoom);
            display = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = display.createGraphics();
            // High-quality rendering hints
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(orig, 0, 0, w, h, null);
            g2.dispose();
            offset.x = (pw - w) / 2;
            offset.y = (ph - h) / 2;
            initCropRect();
        }

        void initCropRect() {
            int size = (int) (CROP_SIZE * zoom);
            if (display != null) {
                size = Math.min(size, Math.min(display.getWidth(), display.getHeight()));
                int x = offset.x + (display.getWidth() - size) / 2;
                int y = offset.y + (display.getHeight() - size) / 2;
                crop = new Rectangle(x, y, size, size);
            }
        }

        void clampCrop() {
            if (crop == null || display == null)
                return;
            int minX = offset.x, minY = offset.y;
            int maxX = offset.x + display.getWidth() - crop.width;
            int maxY = offset.y + display.getHeight() - crop.height;
            crop.x = Math.max(minX, Math.min(crop.x, maxX));
            crop.y = Math.max(minY, Math.min(crop.y, maxY));
        }

        BufferedImage getImage() {
            return orig;
        }

        File getFile() {
            return file;
        }

        Rectangle getCropRect() {
            if (orig == null || crop == null)
                return null;
            double scale = (double) orig.getWidth() / display.getWidth();
            int ix = (int) ((crop.x - offset.x) * scale);
            int iy = (int) ((crop.y - offset.y) * scale);
            int isz = (int) (crop.width * scale);
            return new Rectangle(ix, iy, isz, isz);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (display != null)
                g.drawImage(display, offset.x, offset.y, null);
            if (crop != null) {
                g.setColor(Color.RED);
                ((Graphics2D) g).setStroke(new BasicStroke(2));
                g.drawRect(crop.x, crop.y, crop.width, crop.height);
            }
        }
    }
}