/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author Nadiesda Fuentes
 */
public class EditorTexto extends JFrame implements ActionListener {

    private JTextPane areaTexto;
    private JList<String> listaArchivos;
    private DefaultListModel<String> modeloListaArchivos;
    private JComboBox<String> comboFuente;
    private JComboBox<Integer> comboTamano;
    private JButton botonColor;
    private JButton botonGuardar;
    private JButton botonAbrir;
    private File directorioActual = new File(".");

    public EditorTexto() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Editor de Texto");
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);

        // Área de texto
        areaTexto = new JTextPane();
        JScrollPane scrollTexto = new JScrollPane(areaTexto);

        // Lista de archivos
        modeloListaArchivos = new DefaultListModel<>();
        listaArchivos = new JList<>(modeloListaArchivos);
        JScrollPane scrollLista = new JScrollPane(listaArchivos);
        scrollLista.setPreferredSize(new Dimension(150, 0));

        // Barra de herramientas
        JPanel barraHerramientas = new JPanel();
        barraHerramientas.setLayout(new FlowLayout());

        // Selector de fuente
        comboFuente = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames());
        comboFuente.addActionListener(e -> actualizarFuente());

        // Selector de tamaño
        comboTamano = new JComboBox<>(new Integer[]{12, 16, 20, 24, 28, 32, 48, 64});
        comboTamano.addActionListener(e -> actualizarFuente());

        // Botón de color
        botonColor = new JButton("Color");
        botonColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(this, "Elige un color", Color.BLACK);
            if (color != null) {
                areaTexto.setForeground(color);
            }
        });

        // Botón de guardar
        botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(this);

        // Botón de abrir
        botonAbrir = new JButton("Abrir");
        botonAbrir.addActionListener(this);

        // Agregar componentes a la barra de herramientas
        barraHerramientas.add(comboFuente);
        barraHerramientas.add(comboTamano);
        barraHerramientas.add(botonColor);
        barraHerramientas.add(botonGuardar);
        barraHerramientas.add(botonAbrir);

        // Menú
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem openItem = new JMenuItem("Abrir");
        JMenuItem saveItem = new JMenuItem("Guardar");
        JMenuItem exitItem = new JMenuItem("Salir");

        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);

        // Agregar componentes a la ventana
        this.add(barraHerramientas, BorderLayout.NORTH);
        this.add(scrollTexto, BorderLayout.CENTER);
        this.add(scrollLista, BorderLayout.WEST);

        this.setVisible(true);
    }

    private void actualizarFuente() {
        String nombreFuente = (String) comboFuente.getSelectedItem();
        int tamanoFuente = (Integer) comboTamano.getSelectedItem();
        areaTexto.setFont(new Font(nombreFuente, Font.PLAIN, tamanoFuente));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonAbrir || e.getActionCommand().equals("Abrir")) {
            abrirArchivo();
        }

        if (e.getSource() == botonGuardar || e.getActionCommand().equals("Guardar")) {
            guardarArchivo();
        }

        if (e.getActionCommand().equals("Salir")) {
            System.exit(0);
        }
    }

    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(directorioActual);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int respuesta = fileChooser.showOpenDialog(this);
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                areaTexto.read(lector, null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(directorioActual);
        int respuesta = fileChooser.showSaveDialog(this);

        if (respuesta == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (FileWriter escritor = new FileWriter(archivo)) {
                escritor.write(areaTexto.getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditorTexto());
    }
}