import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JButton botonNuevo;
    private File directorioActual = new File(".");
    private File archivoActual = null;

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
        
        // Panel para la lista de archivos con barra de herramientas
        JPanel panelLista = new JPanel(new BorderLayout());
        
        // Título y botones para la lista de archivos
        JPanel panelTituloLista = new JPanel(new BorderLayout());
        JLabel tituloLista = new JLabel("Archivos guardados");
        tituloLista.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tituloLista.setFont(new Font(tituloLista.getFont().getName(), Font.BOLD, 12));
        
        // Botón para añadir nuevo archivo
        JPanel panelBotonesLista = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevoEnLista = new JButton("+");
        btnNuevoEnLista.setToolTipText("Crear nuevo archivo");
        btnNuevoEnLista.addActionListener(e -> crearNuevoArchivo());
        panelBotonesLista.add(btnNuevoEnLista);
        
        panelTituloLista.add(tituloLista, BorderLayout.WEST);
        panelTituloLista.add(panelBotonesLista, BorderLayout.EAST);
        
        panelLista.add(panelTituloLista, BorderLayout.NORTH);
        panelLista.add(scrollLista, BorderLayout.CENTER);
        
        // Agregar evento de doble clic a la lista de archivos
        listaArchivos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String nombreArchivo = listaArchivos.getSelectedValue();
                    if (nombreArchivo != null) {
                        File archivo = new File(directorioActual, nombreArchivo);
                        abrirArchivo(archivo);
                    }
                }
            }
        });
        
        // Menú contextual para la lista de archivos
        JPopupMenu menuContextual = new JPopupMenu();
        JMenuItem itemAbrir = new JMenuItem("Abrir");
        JMenuItem itemEliminar = new JMenuItem("Eliminar");
        
        itemAbrir.addActionListener(e -> {
            String nombreArchivo = listaArchivos.getSelectedValue();
            if (nombreArchivo != null) {
                File archivo = new File(directorioActual, nombreArchivo);
                abrirArchivo(archivo);
            }
        });
        
        itemEliminar.addActionListener(e -> {
            String nombreArchivo = listaArchivos.getSelectedValue();
            if (nombreArchivo != null) {
                int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro que desea eliminar el archivo: " + nombreArchivo + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (opcion == JOptionPane.YES_OPTION) {
                    File archivo = new File(directorioActual, nombreArchivo);
                    if (archivo.delete()) {
                        modeloListaArchivos.removeElement(nombreArchivo);
                        if (archivoActual != null && archivoActual.getName().equals(nombreArchivo)) {
                            archivoActual = null;
                            areaTexto.setText("");
                            setTitle("Editor de Texto");
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "No se pudo eliminar el archivo",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
        
        menuContextual.add(itemAbrir);
        menuContextual.add(itemEliminar);
        
        listaArchivos.setComponentPopupMenu(menuContextual);

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
        
        // Botón de nuevo archivo
        botonNuevo = new JButton("Nuevo");
        botonNuevo.addActionListener(e -> crearNuevoArchivo());

        // Botón de guardar
        botonGuardar = new JButton("Guardar");
        botonGuardar.addActionListener(this);

        // Botón de abrir
        botonAbrir = new JButton("Abrir");
        botonAbrir.addActionListener(this);

        // Agregar componentes a la barra de herramientas
        barraHerramientas.add(botonNuevo);
        barraHerramientas.add(botonAbrir);
        barraHerramientas.add(botonGuardar);
        barraHerramientas.add(new JSeparator(JSeparator.VERTICAL));
        barraHerramientas.add(comboFuente);
        barraHerramientas.add(comboTamano);
        barraHerramientas.add(botonColor);

        // Menú
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");
        JMenuItem newItem = new JMenuItem("Nuevo");
        JMenuItem openItem = new JMenuItem("Abrir");
        JMenuItem saveItem = new JMenuItem("Guardar");
        JMenuItem saveAsItem = new JMenuItem("Guardar como...");
        JMenuItem exitItem = new JMenuItem("Salir");

        newItem.addActionListener(e -> crearNuevoArchivo());
        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        saveAsItem.addActionListener(e -> guardarArchivoComo());
        exitItem.addActionListener(this);

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);

        // Agregar componentes a la ventana
        this.add(barraHerramientas, BorderLayout.NORTH);
        this.add(scrollTexto, BorderLayout.CENTER);
        this.add(panelLista, BorderLayout.WEST);

        // Cargar archivos existentes en el directorio actual
        cargarArchivosExistentes();

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
            seleccionarYAbrirArchivo();
        }

        if (e.getSource() == botonGuardar || e.getActionCommand().equals("Guardar")) {
            guardarArchivo();
        }

        if (e.getActionCommand().equals("Salir")) {
            System.exit(0);
        }
    }
    
    private void crearNuevoArchivo() {
        // Verificar si hay cambios sin guardar
        if (archivoActual != null && !areaTexto.getText().isEmpty()) {
            int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea guardar los cambios antes de crear un nuevo archivo?",
                "Guardar cambios",
                JOptionPane.YES_NO_CANCEL_OPTION
            );
            
            if (opcion == JOptionPane.YES_OPTION) {
                guardarArchivo();
            } else if (opcion == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        // Crear nuevo archivo
        archivoActual = null;
        areaTexto.setText("");
        setTitle("Editor de Texto - Nuevo archivo");
    }

    private void seleccionarYAbrirArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(directorioActual);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);

        int respuesta = fileChooser.showOpenDialog(this);
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            directorioActual = archivo.getParentFile();
            abrirArchivo(archivo);
        }
    }

    private void abrirArchivo(File archivo) {
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            areaTexto.read(lector, null);
            archivoActual = archivo;
            this.setTitle("Editor de Texto - " + archivo.getName());
            
            // Agregar a la lista si no está ya
            if (!estaEnLista(archivo.getName())) {
                modeloListaArchivos.addElement(archivo.getName());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al abrir el archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean estaEnLista(String nombreArchivo) {
        for (int i = 0; i < modeloListaArchivos.getSize(); i++) {
            if (modeloListaArchivos.getElementAt(i).equals(nombreArchivo)) {
                return true;
            }
        }
        return false;
    }

    private void guardarArchivo() {
        if (archivoActual != null) {
            // Si ya tenemos un archivo abierto, guardar directamente
            guardarEnArchivo(archivoActual);
        } else {
            // Si no hay archivo abierto, mostrar diálogo para guardar
            guardarArchivoComo();
        }
    }
    
    private void guardarArchivoComo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(directorioActual);
        int respuesta = fileChooser.showSaveDialog(this);

        if (respuesta == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            // Añadir extensión .txt si no tiene extensión
            if (!archivo.getName().contains(".")) {
                archivo = new File(archivo.getAbsolutePath() + ".txt");
            }
            directorioActual = archivo.getParentFile();
            guardarEnArchivo(archivo);
        }
    }

    private void guardarEnArchivo(File archivo) {
        try (FileWriter escritor = new FileWriter(archivo)) {
            escritor.write(areaTexto.getText());
            archivoActual = archivo;
            this.setTitle("Editor de Texto - " + archivo.getName());
            
            // Agregar a la lista si no está ya
            if (!estaEnLista(archivo.getName())) {
                modeloListaArchivos.addElement(archivo.getName());
            }
            JOptionPane.showMessageDialog(this, "Archivo guardado exitosamente", "Guardado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarArchivosExistentes() {
        File[] archivos = directorioActual.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
        if (archivos != null) {
            for (File archivo : archivos) {
                modeloListaArchivos.addElement(archivo.getName());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditorTexto());
    }
}