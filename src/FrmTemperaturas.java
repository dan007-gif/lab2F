package vista;

import entidades.RegistroTemperatura;
import servicios.ServicioTemperatura;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import datechooser.beans.DateChooserCombo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class FrmTemperaturas extends JFrame {

    private DateChooserCombo fechaInicio;
    private DateChooserCombo fechaFin;
    private JButton btnGenerar;
    private JPanel panelGrafico;
    private Modelotemperatura servicio;
    private List<Rtemperatura> datos;

    public FrmTemperaturas() {
        setTitle("Temperaturas por Ciudad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Panel superior con controles
        JPanel panelControles = new JPanel();
        fechaInicio = new DateChooserCombo();
        fechaFin = new DateChooserCombo();
        btnGenerar = new JButton("Generar Promedios");
        panelControles.add(new JLabel("Desde:"));
        panelControles.add(fechaInicio);
        panelControles.add(new JLabel("Hasta:"));
        panelControles.add(fechaFin);
        panelControles.add(btnGenerar);

        add(panelControles, BorderLayout.NORTH);

        // Panel para la gráfica
        panelGrafico = new JPanel();
        add(panelGrafico, BorderLayout.CENTER);

        // Servicio
        servicio = new ServicioTemperatura();
        datos = servicio.cargarTemperaturas("datos/temperaturas.csv");

        btnGenerar.addActionListener(e -> generarGrafico());
    }

    private void generarGrafico() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate desde = LocalDate.parse(fechaInicio.getText(), formatter);
            LocalDate hasta = LocalDate.parse(fechaFin.getText(), formatter);

            Map<String, Double> promedios = servicio.calcularPromediosPorCiudad(datos, desde, hasta);

            // Crear gráfico
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            promedios.forEach((ciudad, temp) -> dataset.addValue(temp, "Temperatura", ciudad));

            JFreeChart chart = ChartFactory.createBarChart(
                    "Promedio de Temperaturas",
                    "Ciudad",
                    "Temperatura (°C)",
                    dataset
            );

            panelGrafico.removeAll();
            panelGrafico.add(new ChartPanel(chart));
            panelGrafico.validate();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error procesando fechas o datos.");
        }
    }
}
