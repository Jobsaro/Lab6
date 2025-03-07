/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author DELL
 */
public class Gui {
    private JFrame ventana;
    private JTextPane areaTexto;
    private JList<String> listaArchivos;
    private DefaultListModel<String> modeloListaArchivos;
    private JComboBox<String> comboFuente;
    private JComboBox<Integer> comboTamano;
    private JButton botonColor;
    private JButton botonGuardar;
    private JButton botonAbrir;
    private File directorioActual = new File(".");

    public Gui() {
        ventana = new JFrame("Editor de Texto");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(800, 600);
        
        areaTexto = new JTextPane();
        JScrollPane scrollTexto = new JScrollPane(areaTexto);
        
        modeloListaArchivos = new DefaultListModel<>();
        listaArchivos = new JList<>(modeloListaArchivos);
        JScrollPane scrollLista = new JScrollPane(listaArchivos);
        scrollLista.setPreferredSize(new Dimension(150, 0));
        
        JPanel barraHerramientas = new JPanel();
        barraHerramientas.setLayout(new FlowLayout());

        comboFuente = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames());
        comboFuente.addActionListener(e -> actualizarFuente());
        
        comboTamano = new JComboBox<>(new Integer[]{12, 16, 20, 24, 28, 32, 48, 64});
        comboTamano.addActionListener(e -> actualizarFuente());
        
        botonColor = new JButton("Color");
        botonColor.addActionListener(e -> {
            Color color = JColorChooser.showDialog(ventana, "Elige un color", Color.BLACK);
            if (color != null) {
                areaTexto.setForeground(color);
            }
        });
        
        botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(e -> guardarArchivo());
        
        botonAbrir = new JButton("Abrir");
        botonAbrir.addActionListener(e -> abrirArchivo());
        
        barraHerramientas.add(comboFuente);
        barraHerramientas.add(comboTamano);
        barraHerramientas.add(botonColor);
        barraHerramientas.add(botonGuardar);
        barraHerramientas.add(botonAbrir);
        
        ventana.add(barraHerramientas, BorderLayout.NORTH);
        ventana.add(scrollTexto, BorderLayout.CENTER);
        ventana.add(scrollLista, BorderLayout.WEST);
        
        ventana.setVisible(true);
    }
    
    private void actualizarFuente() {
        String nombreFuente = (String) comboFuente.getSelectedItem();
        int tamanoFuente = (Integer) comboTamano.getSelectedItem();
        areaTexto.setFont(new Font(nombreFuente, Font.PLAIN, tamanoFuente));
    }
    
    private void guardarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(ventana) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (FileWriter escritor = new FileWriter(archivo)) {
                escritor.write(areaTexto.getText());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(ventana, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void abrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(ventana) == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                areaTexto.read(lector, null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(ventana, "Error al abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
}
