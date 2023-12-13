package Consultas;

import Conexion.ConexionMariaDB;
import libs.Leer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;

public class AñadirNuevoEmpleado {

    public static void añadirDesdeXML() {
        PreparedStatement ps;
        ResultSet rs;
        boolean validacion;

        try (Connection conexion = ConexionMariaDB.conectar("constructora")) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Path p = Path.of("src/main/resources/empleados.xml");
            Document document = builder.parse(p.toFile());

            NodeList nodeList = document.getElementsByTagName("empleado");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);

                String dni = element.getElementsByTagName("dni").item(0).getTextContent();
                String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                Double sueldo = Double.valueOf(element.getElementsByTagName("sueldo").item(0).getTextContent());
                String nombreObra = element.getElementsByTagName("obraAsignada").item(0).getTextContent();

                //validacion DNI
                validacion = true;
                ps = conexion.prepareStatement("SELECT dni FROM empleado WHERE dni = ?;");
                ps.setString(1, dni);
                rs = ps.executeQuery();
                if (rs.next() || dni.length() > 9) {
                    System.out.println("El DNI ya existe o tiene mas de 10 numeros: " + dni);
                    validacion = false;
                }

                //validacion nombre
                if (nombre == null || nombre.isEmpty()) {
                    System.out.println("El nombre es nulo");
                    validacion = false;
                }

                //validacion obra asignada
                ps = conexion.prepareStatement("SELECT nombre FROM obra WHERE nombre = ?;");
                ps.setString(1, nombreObra);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    System.out.println("La obra asignada no existe: " + nombreObra);
                    validacion = false;
                }

                //insertamos los datos
                if (validacion) {
                    ps = conexion.prepareStatement("INSERT INTO empleado (dni, nombre, sueldo, nombreObra) VALUES (?, ?, ?, ?)");
                    ps.setString(1, dni);
                    ps.setString(2, nombre);
                    ps.setDouble(3, sueldo);
                    ps.setString(4, nombreObra);
                    ps.executeUpdate();
                    System.out.println("Empleado agregado correctamente.");
                }
            }
        } catch (ParserConfigurationException e) {
            System.out.println("Error al parsear el XML");
        } catch (IOException e) {
            System.out.println("Error de ejecucion");
        } catch (SAXException e) {
            System.out.println("Error de parseo");
        } catch (SQLException e) {
            System.out.println("Error en la base de datos");
        }
    }
}
