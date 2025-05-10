package servicio;
import modelo.Rtemperatura;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Modelotemperatura{

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Rtemperatura> cargarTemperaturas(String rutaArchivo) {
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
            e.printStackTrace();
        }
        return lista;
    }

    public Map<String, Double> calcularPromediosPorCiudad(List<Rtemperatura> lista, LocalDate desde, LocalDate hasta) {
        return lista.stream()
            .filter(r -> !r.getFecha().isBefore(desde) && !r.getFecha().isAfter(hasta))
            .collect(Collectors.groupingBy(
                Rtemperatura::getCiudad,
                Collectors.averagingDouble(Rtemperatura::getTemperatura)
            ));
    }
}





































