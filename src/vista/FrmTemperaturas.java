package vista;
import modelo.Rtemperatura;
import servicio.Modelotemperatura;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import datechooser.beans.DateChooserCombo;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FrmTemperaturas extends JFrame {

    private DateChooserCombo fechaInicio;
    private DateChooserCombo fechaFin;
    private JButton btnGenerar;
    private JButton btnCalcularExtremos; 
    private JPanel panelGrafico;
    private Modelotemperatura servicio;
    private List<Rtemperatura> datos;

    public FrmTemperaturas() {
        setTitle("Temperaturas por Ciudad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel panelControles = new JPanel();
        fechaInicio = new DateChooserCombo();
        fechaFin = new DateChooserCombo();
        btnGenerar = new JButton("Generar Promedios");
        btnCalcularExtremos = new JButton("Calcular Extremos"); 
        panelControles.add(new JLabel("Desde:"));
        panelControles.add(fechaInicio);
        panelControles.add(new JLabel("Hasta:"));
        panelControles.add(fechaFin);
        panelControles.add(btnGenerar);
        panelControles.add(btnCalcularExtremos); 

        add(panelControles, BorderLayout.NORTH);

        panelGrafico = new JPanel();
        panelGrafico.setLayout(new BorderLayout());
        add(panelGrafico, BorderLayout.CENTER);

        servicio = new Modelotemperatura();
        try {
            datos = servicio.cargarTemperaturas("C:/Users/Daniel/Desktop/Segundo lab/Temperaturas/src/Datos/Temperaturas.csv");
            if (datos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron datos en el archivo csv.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar el csv: " + e.getMessage());
            e.printStackTrace();
        }

        btnGenerar.addActionListener(e -> generarGrafico());
        btnCalcularExtremos.addActionListener(e -> calcularExtremos()); 
    }

    private void calcularExtremos() {
        try {
            Calendar calFecha = fechaInicio.getSelectedDate();

            if (calFecha == null) {
                JOptionPane.showMessageDialog(this, "seleccione una fecha.");
                return;
            }

            LocalDate fechaSeleccionada = calFecha.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            List<Rtemperatura> datosFiltrados = datos.stream()
                    .filter(r -> r.getFecha().equals(fechaSeleccionada))
                    .toList();

            if (datosFiltrados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos para la fecha.");
                return;
            }

            Optional<Rtemperatura> maxTemp = datosFiltrados.stream()
                    .max(Comparator.comparingDouble(Rtemperatura::getTemperatura));

            Optional<Rtemperatura> minTemp = datosFiltrados.stream()
                    .min(Comparator.comparingDouble(Rtemperatura::getTemperatura));

            maxTemp.ifPresent(max -> 
                minTemp.ifPresent(min -> {
                    String mensaje = String.format(
                        "En la fecha %s:\n- Ciudad más calurosa: %s (%.2f °C)\n- Ciudad más fría: %s (%.2f °C)",
                        fechaSeleccionada,
                        max.getCiudad(), max.getTemperatura(),
                        min.getCiudad(), min.getTemperatura()
                    );
                    JOptionPane.showMessageDialog(this, mensaje);
                })
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al calcular : " + ex.getMessage());
        }
    }

    private void generarGrafico() {
        try {
            Calendar calDesde = fechaInicio.getSelectedDate();
            Calendar calHasta = fechaFin.getSelectedDate();

            if (calDesde == null || calHasta == null) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione ambas fechas.");
                return;
            }

            LocalDate desde = calDesde.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            LocalDate hasta = calHasta.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (desde.isAfter(hasta)) {
                JOptionPane.showMessageDialog(this, "La fecha inicial no puede ir despues a la fecha final.");
                return;
            }

            Map<String, Double> promedios = servicio.calcularPromediosPorCiudad(datos, desde, hasta);

            if (promedios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay datos.");
                return;
            }

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            promedios.forEach((ciudad, temp) -> dataset.addValue(temp, "Temperatura", ciudad));

            JFreeChart chart = ChartFactory.createBarChart(
                    "Promedio de Temperaturas",
                    "Ciudad",
                    "Temperatura (°C)",
                    dataset
            );

            panelGrafico.removeAll();
            panelGrafico.add(new ChartPanel(chart), BorderLayout.CENTER);
            panelGrafico.revalidate();
            panelGrafico.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error : " + ex.getMessage());
        }
    }
}
