import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SistemaBoletos extends JFrame {

    // COMPONENTES GRAFICOS
    private JComboBox<String> comboRuta;
    private JTextField txtCedula, txtNombre, txtBoletos;
    private JTextArea areaVentas;
    private JLabel lblQuitoGye, lblQuitoCuenca, lblQuitoLoja, lblTotal;

    // ESTRUCTURAS DE DATOS
    private Queue<String> colaVentas = new LinkedList<>(); // Cola de ventas

    // Guarda cuantos boletos se han vendido por ruta
    private Map<String, Integer> vendidos = new HashMap<>();

    // Guarda cuantos boletos ha comprado cada cédula
    private Map<String, Integer> boletosPorCedula = new HashMap<>();

    private double totalRecaudado = 0;

    // Capacidad maxima por ruta
    private final int CAPACIDAD = 20;

    public SistemaBoletos() {
        setTitle("Sistema de Venta de Boletos");
        setSize(650, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // COLORES
        Color fondo = new Color(30, 30, 30);
        Color panelColor = new Color(45, 45, 45);
        Color texto = Color.WHITE;
        Color borde = new Color(70, 130, 180);

        getContentPane().setBackground(fondo);
        setLayout(new BorderLayout(10, 10));

        // Inicializamos valores
        vendidos.put("QUITO - GUAYAQUIL", 0);
        vendidos.put("QUITO - CUENCA", 0);
        vendidos.put("QUITO - LOJA", 0);

        // FUENTE DEL PROGRA
        Font fuente = new Font("Times New Roman", Font.PLAIN, 16);

        // PANEL FORMULARIO
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBackground(panelColor);
        panelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borde, 2),
                "Datos de Compra",
                0, 0,
                fuente,
                texto
        ));

        comboRuta = new JComboBox<>(new String[]{
                "QUITO - GUAYAQUIL",
                "QUITO - CUENCA",
                "QUITO - LOJA"
        });

        txtCedula = new JTextField();
        txtNombre = new JTextField();
        txtBoletos = new JTextField();

        comboRuta.setFont(fuente);
        txtCedula.setFont(fuente);
        txtNombre.setFont(fuente);
        txtBoletos.setFont(fuente);

        comboRuta.setBackground(Color.WHITE);

        // Labels
        JLabel lblRuta = new JLabel("Ruta:");
        JLabel lblCedula = new JLabel("Cédula:");
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblBoletos = new JLabel("Boletos:");

        lblRuta.setForeground(texto);
        lblCedula.setForeground(texto);
        lblNombre.setForeground(texto);
        lblBoletos.setForeground(texto);

        lblRuta.setFont(fuente);
        lblCedula.setFont(fuente);
        lblNombre.setFont(fuente);
        lblBoletos.setFont(fuente);

        panelFormulario.add(lblRuta);
        panelFormulario.add(comboRuta);

        panelFormulario.add(lblCedula);
        panelFormulario.add(txtCedula);

        panelFormulario.add(lblNombre);
        panelFormulario.add(txtNombre);

        panelFormulario.add(lblBoletos);
        panelFormulario.add(txtBoletos);

        JButton btnComprar = new JButton("COMPRAR");
        btnComprar.setBackground(new Color(0, 153, 76));
        btnComprar.setForeground(Color.WHITE);
        btnComprar.setFont(new Font("Times New Roman", Font.BOLD, 16));
        btnComprar.setFocusPainted(false);

        panelFormulario.add(new JLabel());
        panelFormulario.add(btnComprar);

        // PANEL HISTORIAL
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(panelColor);
        panelCentro.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borde, 2),
                "Historial de Ventas",
                0, 0,
                fuente,
                texto
        ));

        areaVentas = new JTextArea();
        areaVentas.setEditable(false);
        areaVentas.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        areaVentas.setBackground(new Color(20, 20, 20));
        areaVentas.setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(areaVentas);
        panelCentro.add(scroll, BorderLayout.CENTER);

        // PANEL ESTADISTICAS
        JPanel panelInferior = new JPanel(new GridLayout(4, 1, 5, 5));
        panelInferior.setBackground(panelColor);
        panelInferior.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borde, 2),
                "Estadísticas",
                0, 0,
                fuente,
                texto
        ));

        lblQuitoGye = new JLabel("GYE vendidos: 0 / disponibles: 20");
        lblQuitoCuenca = new JLabel("CUENCA vendidos: 0 / disponibles: 20");
        lblQuitoLoja = new JLabel("LOJA vendidos: 0 / disponibles: 20");
        lblTotal = new JLabel("Total recaudado: $0");

        lblQuitoGye.setForeground(texto);
        lblQuitoCuenca.setForeground(texto);
        lblQuitoLoja.setForeground(texto);
        lblTotal.setForeground(texto);

        lblQuitoGye.setFont(fuente);
        lblQuitoCuenca.setFont(fuente);
        lblQuitoLoja.setFont(fuente);
        lblTotal.setFont(new Font("Times New Roman", Font.BOLD, 16));

        panelInferior.add(lblQuitoGye);
        panelInferior.add(lblQuitoCuenca);
        panelInferior.add(lblQuitoLoja);
        panelInferior.add(lblTotal);

        // AGREGAR AL FRAME
        add(panelFormulario, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // Evento
        btnComprar.addActionListener(e -> comprarBoletos());
    }

    // METODO PRINCIPAL DE COMPRA
    private void comprarBoletos() {
        try {
            String ruta = (String) comboRuta.getSelectedItem();
            String cedula = txtCedula.getText();
            String nombre = txtNombre.getText();
            int cantidad = Integer.parseInt(txtBoletos.getText());

            // VALIDACIONES
            // No permitir negativos ni mayores a 5
            if (cantidad <= 0 || cantidad > 5) {
                JOptionPane.showMessageDialog(this, "Solo puedes comprar entre 1 y 5 boletos");
                return;
            }

            // Validar boletos por cédula
            int comprados = boletosPorCedula.getOrDefault(cedula, 0);
            if (comprados + cantidad > 5) {
                JOptionPane.showMessageDialog(this, "Máximo 5 boletos por cédula");
                return;
            }

            // Validar capacidad
            int vendidosRuta = vendidos.get(ruta);
            if (vendidosRuta + cantidad > CAPACIDAD) {
                JOptionPane.showMessageDialog(this, "No hay suficientes asientos disponibles");
                return;
            }

            // CALCULAR PRECIO
            double precio = 0;
            if (ruta.equals("QUITO - GUAYAQUIL")) precio = 10.50;
            if (ruta.equals("QUITO - CUENCA")) precio = 12.75;
            if (ruta.equals("QUITO - LOJA")) precio = 15.00;

            double total = precio * cantidad;

            // GUARDAR EN LA COLA
            String venta = "Ruta: " + ruta +
                    " | Boletos: " + cantidad +
                    " | Nombre: " + nombre +
                    " | Total: $" + total;

            colaVentas.add(venta); // Se agrega a la cola

            // ACTUALIZAR DATOS
            vendidos.put(ruta, vendidosRuta + cantidad);
            boletosPorCedula.put(cedula, comprados + cantidad);
            totalRecaudado += total;

            // MOSTRAR EN TEXTAREA
            areaVentas.append(venta + "\n");

            // ACTUALIZAR LABELS
            actualizarLabels();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa un número válido");
        }
    }

    // ACTUALIZA LOS LABELS
    private void actualizarLabels() {

        lblQuitoGye.setText("GYE vendidos: " +
                vendidos.get("QUITO - GUAYAQUIL") +
                " / disponibles: " +
                (CAPACIDAD - vendidos.get("QUITO - GUAYAQUIL")));

        lblQuitoCuenca.setText("CUENCA vendidos: " +
                vendidos.get("QUITO - CUENCA") +
                " / disponibles: " +
                (CAPACIDAD - vendidos.get("QUITO - CUENCA")));

        lblQuitoLoja.setText("LOJA vendidos: " +
                vendidos.get("QUITO - LOJA") +
                " / disponibles: " +
                (CAPACIDAD - vendidos.get("QUITO - LOJA")));

        lblTotal.setText("Total recaudado: $" + totalRecaudado);
    }

    //MAIN
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SistemaBoletos().setVisible(true);
        });
    }
}