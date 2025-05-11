package servicio;
import modelo.Rtemperatura;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Modelotemperatura {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Rtemperatura> cargarTemperaturas(String rutaArchivo) throws Exception {
        List<Rtemperatura> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            br.readLine(); 
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.trim().split(",");
                String ciudad = partes[0].trim();
                LocalDate fecha = LocalDate.parse(partes[1].trim(), FORMATO);
                double temp = Double.parseDouble(partes[2].trim());
                lista.add(new Rtemperatura(ciudad, fecha, temp));
            }
        } catch (Exception e) {
            throw new Exception("Error al cargar el archivo: " + e.getMessage(), e);
        }
        return lista;
    }

    public Map<String, Double> calcularPromediosPorCiudad(List<Rtemperatura> lista, LocalDate desde, LocalDate hasta) {
        Map<String, List<Double>> temperaturasPorCiudad = new HashMap<>();

        for (Rtemperatura r : lista) {
            if (!r.getFecha().isBefore(desde) && !r.getFecha().isAfter(hasta)) {
                temperaturasPorCiudad
                    .computeIfAbsent(r.getCiudad(), k -> new ArrayList<>())
                    .add(r.getTemperatura());
            }
        }

        Map<String, Double> promediosPorCiudad = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : temperaturasPorCiudad.entrySet()) {
            List<Double> temperaturas = entry.getValue();
            double suma = 0;
            for (double temp : temperaturas) {
                suma += temp;
            }
            double promedio = temperaturas.isEmpty() ? 0 : suma / temperaturas.size();
            promediosPorCiudad.put(entry.getKey(), promedio);
        }

        return promediosPorCiudad;
    }

    public String calcularExtremos(List<Rtemperatura> datos, LocalDate fechaSeleccionada) throws Exception {
        List<Rtemperatura> datosFiltrados = new ArrayList<>();
        for (Rtemperatura r : datos) {
            if (r.getFecha().equals(fechaSeleccionada)) {
                datosFiltrados.add(r);
            }
        }

        if (datosFiltrados.isEmpty()) {
            throw new Exception("No hay datos para la fecha seleccionada.");
        }

        Rtemperatura maxTemp = datosFiltrados.get(0);
        Rtemperatura minTemp = datosFiltrados.get(0);

        for (Rtemperatura r : datosFiltrados) {
            if (r.getTemperatura() > maxTemp.getTemperatura()) {
                maxTemp = r;
            }
            if (r.getTemperatura() < minTemp.getTemperatura()) {
                minTemp = r;
            }
        }

        return String.format(
                "En la fecha %s:\n- Ciudad más calurosa: %s (%.2f °C)\n- Ciudad más fría: %s (%.2f °C)",
                fechaSeleccionada,
                maxTemp.getCiudad(), maxTemp.getTemperatura(),
                minTemp.getCiudad(), minTemp.getTemperatura()
        );
    }
}





































